package kz.kazpost.database;

import kz.kazpost.exception.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Opens SQLite connections and creates the normalized project schema.
 */
public final class DatabaseConnection {
    private final String jdbcUrl;

    public DatabaseConnection() {
        this(Path.of("data", "kazpost.db"));
    }

    public DatabaseConnection(Path databaseFile) {
        Objects.requireNonNull(databaseFile, "Database path is required");
        try {
            Path absolutePath = databaseFile.toAbsolutePath();
            Path parent = absolutePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            this.jdbcUrl = "jdbc:sqlite:" + absolutePath;
            initializeSchema();
        } catch (IOException | SQLException exception) {
            throw new DataAccessException("SQLite деректер қорын дайындау мүмкін болмады.", exception);
        }
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
        }
        return connection;
    }

    private void initializeSchema() throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("""
                CREATE TABLE IF NOT EXISTS senders (
                    sender_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    phone TEXT NOT NULL UNIQUE,
                    address TEXT NOT NULL
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS recipients (
                    recipient_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    phone TEXT NOT NULL UNIQUE,
                    address TEXT NOT NULL
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS post_offices (
                    office_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    office_code TEXT NOT NULL UNIQUE,
                    address TEXT NOT NULL,
                    city TEXT NOT NULL
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS parcels (
                    parcel_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tracking_number TEXT NOT NULL UNIQUE,
                    sender_id INTEGER NOT NULL,
                    recipient_id INTEGER NOT NULL,
                    office_id INTEGER NOT NULL,
                    weight REAL NOT NULL CHECK (weight > 0),
                    status TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    FOREIGN KEY(sender_id) REFERENCES senders(sender_id),
                    FOREIGN KEY(recipient_id) REFERENCES recipients(recipient_id),
                    FOREIGN KEY(office_id) REFERENCES post_offices(office_id)
                )
                """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS tracking_events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    parcel_id INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    description TEXT,
                    event_date TEXT NOT NULL,
                    FOREIGN KEY(parcel_id) REFERENCES parcels(parcel_id) ON DELETE CASCADE
                )
                """);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_parcels_sender ON parcels(sender_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_parcels_recipient ON parcels(recipient_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_events_parcel_date ON tracking_events(parcel_id, event_date)");
        }
    }
}