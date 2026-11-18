package kz.kazpost.service;

import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import kz.kazpost.storage.CsvStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AsyncReportServiceTest {
    @TempDir
    Path tempDir;

    private Parcel sampleParcel() {
        return new Parcel("KZ0000000301",
                new Sender("Асан", "+77210000001", "Алматы"),
                new Recipient("Үсен", "+77210000002", "Астана"),
                new PostOffice("ASY-01", "Абай 1", "Алматы"), 1.5);
    }

    @Test
    void exportsCsvOnBackgroundWorkerThread() throws Exception {
        AtomicReference<String> workerName = new AtomicReference<>();
        CsvStorage recordingStorage = new CsvStorage() {
            @Override
            public void exportToCsv(List<Parcel> parcels, Path target) throws IOException {
                workerName.set(Thread.currentThread().getName());
                super.exportToCsv(parcels, target);
            }
        };

        Path target = tempDir.resolve("async-report.csv");
        try (AsyncReportService service = new AsyncReportService(recordingStorage)) {
            Path saved = service.exportAsync(List.of(sampleParcel()), target)
                    .get(5, TimeUnit.SECONDS);

            assertEquals(target.toAbsolutePath(), saved);
            assertTrue(Files.exists(saved));
            assertEquals("kazpost-report-worker", workerName.get());
        }
    }

    @Test
    void exportFutureCompletesWithRequestedAbsolutePath() throws Exception {
        Path target = tempDir.resolve("report.csv");
        try (AsyncReportService service = new AsyncReportService(new CsvStorage())) {
            Path result = service.exportAsync(List.of(sampleParcel()), target)
                    .get(5, TimeUnit.SECONDS);

            assertEquals(target.toAbsolutePath(), result);
            assertTrue(Files.readString(result).contains("KZ0000000301"));
        }
    }

    @Test
    void exportFailureIsReturnedThroughFuture() {
        CsvStorage failingStorage = new CsvStorage() {
            @Override
            public void exportToCsv(List<Parcel> parcels, Path target) throws IOException {
                throw new IOException("Test file error");
            }
        };

        try (AsyncReportService service = new AsyncReportService(failingStorage)) {
            ExecutionException exception = assertThrows(ExecutionException.class,
                    () -> service.exportAsync(List.of(sampleParcel()), tempDir.resolve("failure.csv"))
                            .get(5, TimeUnit.SECONDS));

            assertTrue(exception.getCause() instanceof IOException);
            assertEquals("Test file error", exception.getCause().getMessage());
        }
    }
}
