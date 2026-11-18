package kz.kazpost.repository;

import kz.kazpost.database.DatabaseConnection;
import kz.kazpost.exception.DataAccessException;
import kz.kazpost.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParcelDaoTest {
    @TempDir
    Path tempDir;

    private Parcel createParcel(String trackingNumber, String senderPhone) {
        return new Parcel(trackingNumber,
                new Sender("Жіберуші", senderPhone, "Алматы"),
                new Recipient("Алушы", "+77090000002", "Астана"),
                new PostOffice("DAO-01", "Абай 1", "Алматы"),
                2.0);
    }

    private ParcelDao createDao() {
        return new ParcelDao(new DatabaseConnection(tempDir.resolve("dao-test.db")));
    }

    @Test
    void savesAndRetrievesParcel() {
        ParcelDao dao = createDao();
        Parcel parcel = createParcel("KZ0000000101", "+77090000001");

        dao.save(parcel);

        Optional<Parcel> loaded = dao.findById(parcel.getTrackingNumber());
        assertTrue(loaded.isPresent());
        assertEquals("Жіберуші", loaded.orElseThrow().getSender().getFullName());
    }

    @Test
    void returnsEmptyWhenTrackingNumberDoesNotExist() {
        ParcelDao dao = createDao();
        assertTrue(dao.findById("KZ9999999999").isEmpty());
    }

    @Test
    void updateStatusPersistsEventHistory() {
        ParcelDao dao = createDao();
        Parcel parcel = createParcel("KZ0000000102", "+77090000003");
        dao.save(parcel);

        parcel.updateStatus(DeliveryStatus.ACCEPTED, "Бөлімшеде қабылданды");
        dao.updateStatus(parcel);

        Parcel loaded = dao.findById(parcel.getTrackingNumber()).orElseThrow();
        assertEquals(DeliveryStatus.ACCEPTED, loaded.getStatus());
        assertEquals(2, loaded.getTrackingHistory().size());
    }
}