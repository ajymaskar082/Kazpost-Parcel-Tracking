package kz.kazpost.service;

import kz.kazpost.database.DatabaseConnection;
import kz.kazpost.exception.TrackingNumberNotFoundException;
import kz.kazpost.model.*;
import kz.kazpost.repository.ParcelDao;
import kz.kazpost.storage.CsvStorage;
import kz.kazpost.strategy.StandardDeliveryStrategy;
import kz.kazpost.util.TrackingNumberGenerator;
import kz.kazpost.factory.ParcelFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParcelServiceIntegrationTest {
    @TempDir
    Path tempDir;

    private ParcelService createService() {
        DatabaseConnection database = new DatabaseConnection(tempDir.resolve("test.db"));
        return new ParcelService(new ParcelDao(database), new ParcelFactory(),
                new TrackingNumberGenerator(), new CsvStorage(), new StandardDeliveryStrategy());
    }

    private Parcel createParcel(ParcelService service) {
        return service.registerParcel(
                new Sender("Айжан", "+77010000001", "Алматы"),
                new Recipient("Марат", "+77010000002", "Астана"),
                new PostOffice("ALM-01", "Абая 10", "Алматы"),
                2.0);
    }

    @Test
    void registeredParcelIsPersistedAndCanBeFound() throws Exception {
        ParcelService service = createService();
        Parcel created = createParcel(service);
        Parcel loaded = service.findParcel(created.getTrackingNumber());
        assertEquals(created.getTrackingNumber(), loaded.getTrackingNumber());
        assertEquals("Айжан", loaded.getSender().getFullName());
    }

    @Test
    void statusAndTrackingEventArePersistedTogether() throws Exception {
        ParcelService service = createService();
        Parcel created = createParcel(service);
        service.updateStatus(created.getTrackingNumber(), DeliveryStatus.ACCEPTED, "Қабылданды");

        Parcel loaded = createService().findParcel(created.getTrackingNumber());
        assertEquals(DeliveryStatus.ACCEPTED, loaded.getStatus());
        assertEquals(2, loaded.getTrackingHistory().size());
        assertEquals("Қабылданды", loaded.getTrackingHistory().get(1).getDescription());
    }

    @Test
    void unknownTrackingNumberThrowsCustomException() {
        ParcelService service = createService();
        assertThrows(TrackingNumberNotFoundException.class,
                () -> service.findParcel("UNKNOWN"));
    }

    @Test
    void streamStatisticsCountParcelsByStatus() throws Exception {
        ParcelService service = createService();
        createParcel(service);
        createParcel(service);
        Map<DeliveryStatus, Long> statistics = service.getStatistics();
        assertEquals(2L, statistics.get(DeliveryStatus.REGISTERED));
    }

    @Test
    void csvReportIsExported() throws Exception {
        ParcelService service = createService();
        createParcel(service);
        Path report = tempDir.resolve("report.csv");
        service.exportReport(report);

        assertTrue(Files.exists(report));
        assertTrue(Files.readString(report).contains("tracking_number"));
    }
}