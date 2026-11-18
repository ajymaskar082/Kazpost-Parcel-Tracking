package kz.kazpost.storage;

import kz.kazpost.model.Parcel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV report export. Database remains the source of truth for application data.
 */
public class CsvStorage {
    public void exportToCsv(List<Parcel> parcels, Path target) throws IOException {
        Path absolute = target.toAbsolutePath();
        if (absolute.getParent() != null) {
            Files.createDirectories(absolute.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(absolute, StandardCharsets.UTF_8)) {
            writer.write("tracking_number,sender,recipient,office,weight,status,created_at,latest_event");
            writer.newLine();
            for (Parcel parcel : parcels) {
                String latestEvent = parcel.getTrackingHistory().isEmpty()
                        ? "" : parcel.getTrackingHistory().get(parcel.getTrackingHistory().size() - 1).getEventInfo();
                writeRow(writer, List.of(
                        parcel.getTrackingNumber(),
                        parcel.getSender().getFullName(),
                        parcel.getRecipient().getFullName(),
                        parcel.getPostOffice().getOfficeInfo(),
                        Double.toString(parcel.getWeight()),
                        parcel.getStatus().name(),
                        parcel.getCreatedAt().toString(),
                        latestEvent
                ));
            }
        }
    }

    public List<String[]> readCsv(Path source) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                rows.add(parseLine(line));
            }
        }
        return rows;
    }

    private void writeRow(BufferedWriter writer, List<String> values) throws IOException {
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) writer.write(',');
            writer.write(escape(values.get(index)));
        }
        writer.newLine();
    }

    private String escape(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private String[] parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                values.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(current);
            }
        }
        values.add(cell.toString());
        return values.toArray(String[]::new);
    }
}