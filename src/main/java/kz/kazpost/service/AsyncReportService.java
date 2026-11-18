package kz.kazpost.service;

import kz.kazpost.model.Parcel;
import kz.kazpost.storage.CsvStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Runs CSV report exports on a background thread so the console menu
 * does not have to perform file I/O on its main thread.
 */
public final class AsyncReportService implements AutoCloseable {
    private final CsvStorage csvStorage;
    private final ExecutorService executor;

    public AsyncReportService(CsvStorage csvStorage) {
        this(csvStorage, Executors.newFixedThreadPool(2, task -> {
            Thread thread = new Thread(task, "kazpost-report-worker");
            thread.setDaemon(false);
            return thread;
        }));
    }

    public AsyncReportService(CsvStorage csvStorage, ExecutorService executor) {
        this.csvStorage = Objects.requireNonNull(csvStorage, "CSV сақтау сервисі қажет.");
        this.executor = Objects.requireNonNull(executor, "Executor қажет.");
    }

    /**
     * Exports a stable snapshot of parcels asynchronously.
     * The returned future completes with the absolute target path.
     */
    public CompletableFuture<Path> exportAsync(List<Parcel> parcels, Path target) {
        List<Parcel> snapshot = List.copyOf(Objects.requireNonNull(parcels, "Сәлемдемелер тізімі қажет."));
        Path absoluteTarget = Objects.requireNonNull(target, "CSV жолы қажет.").toAbsolutePath();

        return CompletableFuture.supplyAsync(() -> {
            try {
                csvStorage.exportToCsv(snapshot, absoluteTarget);
                return absoluteTarget;
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, executor);
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Фондық есеп ағынын тоқтату мүмкін болмады.");
                }
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
