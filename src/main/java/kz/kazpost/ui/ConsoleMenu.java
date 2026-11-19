package kz.kazpost.ui;

import kz.kazpost.demo.OopDemo;
import kz.kazpost.exception.TrackingNumberNotFoundException;
import kz.kazpost.model.DeliveryStatus;
import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import kz.kazpost.service.ParcelService;
import kz.kazpost.service.AsyncReportService;
import kz.kazpost.strategy.ExpressDeliveryStrategy;
import kz.kazpost.strategy.StandardDeliveryStrategy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.Scanner;

/**
 * Text-based user interface. Business and persistence logic stays outside this class.
 */
public class ConsoleMenu {
    private final ParcelService parcelService;
    private final AsyncReportService asyncReportService;
    private final Scanner scanner;

    public ConsoleMenu(ParcelService parcelService, AsyncReportService asyncReportService, Scanner scanner) {
        this.parcelService = parcelService;
        this.asyncReportService = asyncReportService;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("==============================================");
        System.out.println(" ҚАЗПОШТА — СӘЛЕМДЕМЕЛЕРДІ БАҚЫЛАУ ЖҮЙЕСІ");
        System.out.println("==============================================");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> registerParcel();
                    case "2" -> findParcel();
                    case "3" -> listParcels();
                    case "4" -> updateStatus();
                    case "5" -> showStatistics();
                    case "6" -> exportCsv();
                    case "7" -> chooseDeliveryStrategy();
                    case "8" -> exportCsvAsync();
                    case "9" -> OopDemo.run();
                    case "0" -> running = false;
                    default -> System.out.println("Мәзірден 0–9 аралығындағы бөлімді таңдаңыз.");
                }
            } catch (TrackingNumberNotFoundException exception) {
                System.out.println("Қате: " + exception.getMessage());
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Қате: " + exception.getMessage());
            } catch (IOException exception) {
                System.out.println("Файл қатесі: " + exception.getMessage());
            } catch (RuntimeException exception) {
                System.out.println("Жүйелік қате: " + exception.getMessage());
            }
            System.out.println();
        }
        System.out.println("Бағдарлама аяқталды.");
    }

    private void printMenu() {
        System.out.println("""
                
                -------- НЕГІЗГІ МӘЗІР --------
                1. Сәлемдемені тіркеу
                2. Трек-нөмір арқылы іздеу
                3. Барлық сәлемдемелерді көру
                4. Сәлемдеме статусын жаңарту
                5. Статистика
                6. CSV есебін экспорттау
                7. Жеткізу түрін таңдау
                8. CSV есебін фондық ағынмен экспорттау
                9. OOP қағидаларын демонстрациялау
                0. Шығу
                -------------------------------
                Таңдау:
                """);
    }

    private void registerParcel() {
        System.out.println("\n--- Жіберуші мәліметтері ---");
        Sender sender = new Sender(readRequired("Аты-жөні: "), readRequired("Телефон: "),
                readRequired("Мекенжай: "));
        System.out.println("\n--- Алушы мәліметтері ---");
        Recipient recipient = new Recipient(readRequired("Аты-жөні: "), readRequired("Телефон: "),
                readRequired("Мекенжай: "));
        System.out.println("\n--- Пошта бөлімшесі ---");
        PostOffice office = new PostOffice(readRequired("Бөлімше коды: "),
                readRequired("Мекенжайы: "), readRequired("Қала: "));
        double weight = readPositiveDouble("Сәлемдеме салмағы (кг): ");

        Parcel parcel = parcelService.registerParcel(sender, recipient, office, weight);
        System.out.println("\nСәлемдеме сәтті тіркелді!");
        System.out.println("Трек-нөмір: " + parcel.getTrackingNumber());
        System.out.printf("Жеткізу түрі: %s%n", parcelService.getDeliveryStrategyName());
        System.out.printf("Жеткізу құны: %.2f теңге%n", parcelService.calculateDeliveryCost(parcel));
    }

    private void findParcel() throws TrackingNumberNotFoundException {
        String trackingNumber = readRequired("Трек-нөмірді енгізіңіз: ");
        Parcel parcel = parcelService.findParcel(trackingNumber);
        printParcel(parcel);
    }

    private void listParcels() {
        List<Parcel> parcels = parcelService.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("Әзірге сәлемдемелер тіркелмеген.");
            return;
        }
        parcels.forEach(this::printParcel);
    }

    private void updateStatus() throws TrackingNumberNotFoundException {
        String trackingNumber = readRequired("Трек-нөмір: ");
        Parcel parcel = parcelService.findParcel(trackingNumber);
        System.out.println("Қазіргі статус: " + parcel.getStatus().getKazakhLabel());
        for (DeliveryStatus status : DeliveryStatus.values()) {
            System.out.println(status.ordinal() + 1 + ". " + status.name() + " — " + status.getKazakhLabel());
        }
        int selected = readInt("Жаңа статус нөмірі: ");
        if (selected < 1 || selected > DeliveryStatus.values().length) {
            throw new IllegalArgumentException("Статус нөмірі дұрыс емес.");
        }
        DeliveryStatus newStatus = DeliveryStatus.values()[selected - 1];
        String description = readRequired("Оқиға сипаттамасы: ");
        parcelService.updateStatus(trackingNumber, newStatus, description);
        System.out.println("Статус және оқиғалар тарихы сақталды.");
    }

    private void showStatistics() {
        Map<DeliveryStatus, Long> statistics = parcelService.getStatistics();
        System.out.println("--- Сәлемдемелер статистикасы ---");
        for (DeliveryStatus status : DeliveryStatus.values()) {
            System.out.printf("%-22s : %d%n", status.getKazakhLabel(),
                    statistics.getOrDefault(status, 0L));
        }
        System.out.println("Жалпы саны: " + parcelService.getAllParcels().size());
    }

    private void exportCsv() throws IOException {
        String path = readOptional("CSV файлының жолы (әдепкі: data/parcels-report.csv): ");
        Path target = path.isBlank() ? Path.of("data", "parcels-report.csv") : Path.of(path);
        parcelService.exportReport(target);
        System.out.println("CSV есебі сақталды: " + target.toAbsolutePath());
    }

    /**
     * Starts file export on a worker thread; the menu remains responsive.
     */
    private void exportCsvAsync() {
        String path = readOptional("CSV файлының жолы (әдепкі: data/parcels-async-report.csv): ");
        Path target = path.isBlank()
                ? Path.of("data", "parcels-async-report.csv")
                : Path.of(path);

        asyncReportService.exportAsync(parcelService.getAllParcels(), target)
                .whenComplete((savedPath, error) -> {
                    if (error != null) {
                        Throwable cause = error instanceof CompletionException && error.getCause() != null
                                ? error.getCause() : error;
                        System.err.println("Фондық CSV экспорт қатесі: " + cause.getMessage());
                    } else {
                        System.out.println("Фондық CSV есеп сақталды: " + savedPath);
                    }
                });

        System.out.println("CSV экспорты фондық ағынға жіберілді. Мәзірді қолдануды жалғастыра аласыз.");
    }

    private void chooseDeliveryStrategy() {
        System.out.println("1. Стандартты жеткізу");
        System.out.println("2. Жедел жеткізу");
        String choice = readRequired("Таңдау: ");
        switch (choice) {
            case "1" -> parcelService.setDeliveryStrategy(new StandardDeliveryStrategy());
            case "2" -> parcelService.setDeliveryStrategy(new ExpressDeliveryStrategy());
            default -> throw new IllegalArgumentException("Жеткізу түрі дұрыс емес.");
        }
        System.out.println("Таңдалды: " + parcelService.getDeliveryStrategyName());
    }

    private void printParcel(Parcel parcel) {
        System.out.println("\n==============================================");
        System.out.println("Трек-нөмір: " + parcel.getTrackingNumber());
        System.out.println("Жіберуші: " + parcel.getSender().getFullName()
                + " (" + parcel.getSender().getPhone() + ")");
        System.out.println("Алушы: " + parcel.getRecipient().getFullName()
                + " (" + parcel.getRecipient().getPhone() + ")");
        System.out.println("Бөлімше: " + parcel.getPostOffice().getOfficeInfo());
        System.out.println("Салмағы: " + parcel.getWeight() + " кг");
        System.out.println("Статус: " + parcel.getStatus().getKazakhLabel());
        System.out.println("Тіркелген уақыты: " + parcel.getCreatedAt());
        System.out.println("--- Статус тарихы ---");
        parcel.getTrackingHistory().forEach(event -> System.out.println(event.getEventInfo()));
        System.out.println("==============================================");
    }

    private String readRequired(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isBlank()) throw new IllegalArgumentException("Бұл өріс бос болмауы керек.");
        return value;
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Бүтін сан енгізіңіз.");
        }
    }

    private double readPositiveDouble(String prompt) {
        System.out.print(prompt);
        try {
            double value = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            if (!Double.isFinite(value) || value <= 0) {
                throw new IllegalArgumentException("Салмақ нөлден үлкен болуы керек.");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Сандық салмақ енгізіңіз.");
        }
    }
}