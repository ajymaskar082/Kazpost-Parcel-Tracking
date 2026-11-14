package kz.kazpost.storage;

import kz.kazpost.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void exportsParcelToCsv() throws Exception {
        CsvStorage storage = new CsvStorage();
        Parcel parcel = new Parcel("KZ0000000003",
                new Sender("Асан", "+77000000003", "Алматы"),
                new Recipient("Үсен", "+77000000004", "Астана"),
                new PostOffice("A-03", "Абай 3", "Алматы"), 1.5);
        Path target = tempDir.resolve("nested").resolve("parcels.csv");

        storage.exportToCsv(List.of(parcel), target);

        String csv = Files.readString(target);
        assertTrue(csv.contains("KZ0000000003"));
        assertTrue(csv.contains("Асан"));
    }

    @Test
    void escapesQuotesAndCommasInExportedNames() throws Exception {
        CsvStorage storage = new CsvStorage();
        Parcel parcel = new Parcel("KZ0000000005",
                new Sender("Асқар, \"Азамат\"", "+77000000005", "Алматы"),
                new Recipient("Үсен", "+77000000006", "Астана"),
                new PostOffice("A-05", "Абай 5", "Алматы"), 1.0);
        Path target = tempDir.resolve("quoted.csv");

        storage.exportToCsv(List.of(parcel), target);
        List<String[]> rows = storage.readCsv(target);

        assertEquals(1, rows.size());
        assertEquals("Асқар, \"Азамат\"", rows.get(0)[1]);
    }

    @Test
    void readsQuotedCsvFields() throws Exception {
        Path source = tempDir.resolve("input.csv");
        Files.writeString(source, "tracking_number,sender\nKZ0000000004,\"Асан, Төлеген\"\n");

        List<String[]> rows = new CsvStorage().readCsv(source);

        assertEquals(1, rows.size());
        assertEquals("Асан, Төлеген", rows.get(0)[1]);
    }
}