package kz.kazpost.repository;

import kz.kazpost.database.DatabaseConnection;
import kz.kazpost.exception.DataAccessException;
import kz.kazpost.model.DeliveryStatus;
import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import kz.kazpost.model.TrackingEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO implementation: all SQL for Parcel persistence is kept in one class.
 */
public final class ParcelDao extends Repository<Parcel> {
    private final DatabaseConnection database;

    public ParcelDao(DatabaseConnection database) {
        this.database = database;
    }

    @Override
    public void save(Parcel parcel) {
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long senderId = savePerson(connection, "senders", "sender_id",
                        parcel.getSender().getFullName(), parcel.getSender().getPhone(), parcel.getSender().getAddress());
                long recipientId = savePerson(connection, "recipients", "recipient_id",
                        parcel.getRecipient().getFullName(), parcel.getRecipient().getPhone(), parcel.getRecipient().getAddress());
                long officeId = saveOffice(connection, parcel.getPostOffice());

                long parcelId;
                try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO parcels(tracking_number, sender_id, recipient_id, office_id, weight, status, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, parcel.getTrackingNumber());
                    statement.setLong(2, senderId);
                    statement.setLong(3, recipientId);
                    statement.setLong(4, officeId);
                    statement.setDouble(5, parcel.getWeight());
                    statement.setString(6, parcel.getStatus().name());
                    statement.setString(7, parcel.getCreatedAt().toString());
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Parcel ID was not generated.");
                        }
                        parcelId = keys.getLong(1);
                    }
                }
                insertEvents(connection, parcelId, parcel.getTrackingHistory());
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Сәлемдемені SQLite деректер қорына сақтау мүмкін болмады.", exception);
        }
    }

    public boolean existsByTrackingNumber(String trackingNumber) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM parcels WHERE tracking_number = ?")) {
            statement.setString(1, trackingNumber);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Трек-нөмірді тексеру мүмкін болмады.", exception);
        }
    }

    @Override
    public Optional<Parcel> findById(String trackingNumber) {
        String sql = """
            SELECT p.parcel_id, p.tracking_number, p.weight, p.status, p.created_at,
                   s.full_name AS sender_name, s.phone AS sender_phone, s.address AS sender_address,
                   r.full_name AS recipient_name, r.phone AS recipient_phone, r.address AS recipient_address,
                   o.office_code, o.address AS office_address, o.city
            FROM parcels p
            JOIN senders s ON s.sender_id = p.sender_id
            JOIN recipients r ON r.recipient_id = p.recipient_id
            JOIN post_offices o ON o.office_id = p.office_id
            WHERE p.tracking_number = ?
            """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, trackingNumber);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                long parcelId = result.getLong("parcel_id");
                List<TrackingEvent> events = loadEvents(connection, parcelId);
                Parcel parcel = new Parcel(
                        result.getString("tracking_number"),
                        new Sender(result.getString("sender_name"), result.getString("sender_phone"), result.getString("sender_address")),
                        new Recipient(result.getString("recipient_name"), result.getString("recipient_phone"), result.getString("recipient_address")),
                        new PostOffice(result.getString("office_code"), result.getString("office_address"), result.getString("city")),
                        result.getDouble("weight"),
                        DeliveryStatus.valueOf(result.getString("status")),
                        LocalDateTime.parse(result.getString("created_at")),
                        events
                );
                return Optional.of(parcel);
            }
        } catch (SQLException | IllegalArgumentException exception) {
            throw new DataAccessException("Сәлемдеме деректерін оқу мүмкін болмады.", exception);
        }
    }

    @Override
    public List<Parcel> findAll() {
        List<String> trackingNumbers = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT tracking_number FROM parcels ORDER BY created_at DESC");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                trackingNumbers.add(result.getString(1));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Сәлемдемелер тізімін оқу мүмкін болмады.", exception);
        }

        List<Parcel> parcels = new ArrayList<>();
        for (String trackingNumber : trackingNumbers) {
            findById(trackingNumber).ifPresent(parcels::add);
        }
        return parcels;
    }

    /**
     * Updates current status and appends its history event in one transaction.
     */
    public void updateStatus(Parcel parcel) {
        List<TrackingEvent> history = parcel.getTrackingHistory();
        if (history.isEmpty()) {
            throw new IllegalArgumentException("Статус тарихы бос болмауы керек.");
        }
        TrackingEvent latest = history.get(history.size() - 1);
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long parcelId;
                try (PreparedStatement select = connection.prepareStatement(
                        "SELECT parcel_id FROM parcels WHERE tracking_number = ?")) {
                    select.setString(1, parcel.getTrackingNumber());
                    try (ResultSet result = select.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException("Parcel not found: " + parcel.getTrackingNumber());
                        }
                        parcelId = result.getLong(1);
                    }
                }
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE parcels SET status = ? WHERE parcel_id = ?")) {
                    update.setString(1, parcel.getStatus().name());
                    update.setLong(2, parcelId);
                    update.executeUpdate();
                }
                insertEvents(connection, parcelId, List.of(latest));
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Сәлемдеме статусын сақтау мүмкін болмады.", exception);
        }
    }

    private long savePerson(Connection connection, String table, String idColumn,
                            String fullName, String phone, String address) throws SQLException {
        String insertSql = "INSERT OR IGNORE INTO " + table + "(full_name, phone, address) VALUES (?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
            insert.setString(1, fullName);
            insert.setString(2, phone);
            insert.setString(3, address);
            insert.executeUpdate();
        }
        try (PreparedStatement select = connection.prepareStatement(
                "SELECT " + idColumn + " FROM " + table + " WHERE phone = ?")) {
            select.setString(1, phone);
            try (ResultSet result = select.executeQuery()) {
                if (!result.next()) throw new SQLException("Could not find saved person.");
                return result.getLong(1);
            }
        }
    }

    private long saveOffice(Connection connection, PostOffice office) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement("""
            INSERT OR IGNORE INTO post_offices(office_code, address, city) VALUES (?, ?, ?)
            """)) {
            insert.setString(1, office.getOfficeCode());
            insert.setString(2, office.getAddress());
            insert.setString(3, office.getCity());
            insert.executeUpdate();
        }
        try (PreparedStatement select = connection.prepareStatement(
                "SELECT office_id FROM post_offices WHERE office_code = ?")) {
            select.setString(1, office.getOfficeCode());
            try (ResultSet result = select.executeQuery()) {
                if (!result.next()) throw new SQLException("Could not find saved post office.");
                return result.getLong(1);
            }
        }
    }

    private void insertEvents(Connection connection, long parcelId, List<TrackingEvent> events) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO tracking_events(parcel_id, status, description, event_date)
            VALUES (?, ?, ?, ?)
            """)) {
            for (TrackingEvent event : events) {
                statement.setLong(1, parcelId);
                statement.setString(2, event.getStatus().name());
                statement.setString(3, event.getDescription());
                statement.setString(4, event.getEventDate().toString());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<TrackingEvent> loadEvents(Connection connection, long parcelId) throws SQLException {
        List<TrackingEvent> events = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT event_id, status, description, event_date
            FROM tracking_events WHERE parcel_id = ? ORDER BY event_id
            """)) {
            statement.setLong(1, parcelId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    events.add(new TrackingEvent(
                            result.getLong("event_id"),
                            DeliveryStatus.valueOf(result.getString("status")),
                            LocalDateTime.parse(result.getString("event_date")),
                            result.getString("description")
                    ));
                }
            }
        }
        return events;
    }

    private void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            original.addSuppressed(rollbackException);
        }
    }
}