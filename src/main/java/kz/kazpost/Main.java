package kz.kazpost;

import kz.kazpost.database.DatabaseConnection;
import kz.kazpost.repository.ParcelDao;
import kz.kazpost.service.ParcelService;
import kz.kazpost.ui.ConsoleMenu;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        DatabaseConnection database = new DatabaseConnection();
        ParcelDao parcelDao = new ParcelDao(database);
        ParcelService parcelService = new ParcelService(parcelDao);
        ConsoleMenu menu = new ConsoleMenu(parcelService, new Scanner(System.in));
        menu.run();
    }
}