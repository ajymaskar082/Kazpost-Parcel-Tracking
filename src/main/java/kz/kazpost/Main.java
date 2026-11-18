package kz.kazpost;

import kz.kazpost.database.DatabaseConnection;
import kz.kazpost.repository.ParcelDao;
import kz.kazpost.service.AsyncReportService;
import kz.kazpost.service.ParcelService;
import kz.kazpost.storage.CsvStorage;
import kz.kazpost.ui.ConsoleMenu;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        DatabaseConnection database = new DatabaseConnection();
        ParcelDao parcelDao = new ParcelDao(database);
        ParcelService parcelService = new ParcelService(parcelDao);

        try (AsyncReportService asyncReportService = new AsyncReportService(new CsvStorage());
             Scanner scanner = new Scanner(System.in)) {
            ConsoleMenu menu = new ConsoleMenu(parcelService, asyncReportService, scanner);
            menu.run();
        }
    }
}
