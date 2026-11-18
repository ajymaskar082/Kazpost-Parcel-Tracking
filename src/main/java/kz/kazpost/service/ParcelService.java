package kz.kazpost.service;

import kz.kazpost.exception.TrackingNumberNotFoundException;
import kz.kazpost.factory.ParcelFactory;
import kz.kazpost.model.DeliveryStatus;
import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import kz.kazpost.repository.ParcelDao;
import kz.kazpost.storage.CsvStorage;
import kz.kazpost.strategy.DeliveryStrategy;
import kz.kazpost.strategy.StandardDeliveryStrategy;
import kz.kazpost.util.TrackingNumberGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Coordinates parcel registration, tracking, status updates and reports.
 */
public class ParcelService {
    private final ParcelDao repository;
    private final ParcelFactory parcelFactory;
    private final TrackingNumberGenerator numberGenerator;
    private final CsvStorage csvStorage;
    private final Set<String> knownTrackingNumbers = new HashSet<>();
    private DeliveryStrategy deliveryStrategy;

    public ParcelService(ParcelDao repository) {
        this(repository, new ParcelFactory(), new TrackingNumberGenerator(),
                new CsvStorage(), new StandardDeliveryStrategy());
    }

    public ParcelService(ParcelDao repository, ParcelFactory parcelFactory,
                         TrackingNumberGenerator numberGenerator, CsvStorage csvStorage,
                         DeliveryStrategy deliveryStrategy) {
        this.repository = repository;
        this.parcelFactory = parcelFactory;
        this.numberGenerator = numberGenerator;
        this.csvStorage = csvStorage;
        this.deliveryStrategy = deliveryStrategy;
        repository.findAll().forEach(parcel -> knownTrackingNumbers.add(parcel.getTrackingNumber()));
    }

    public Parcel registerParcel(Sender sender, Recipient recipient, PostOffice office, double weight) {
        String trackingNumber = numberGenerator.generateUnique(knownTrackingNumbers::contains);
        Parcel parcel = parcelFactory.create(trackingNumber, sender, recipient, office, weight);
        repository.save(parcel);
        knownTrackingNumbers.add(trackingNumber);
        return parcel;
    }

    public Parcel findParcel(String trackingNumber) throws TrackingNumberNotFoundException {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new TrackingNumberNotFoundException(String.valueOf(trackingNumber));
        }
        return repository.findById(trackingNumber.trim())
                .orElseThrow(() -> new TrackingNumberNotFoundException(trackingNumber));
    }

    public List<Parcel> getAllParcels() {
        return repository.findAll();
    }

    /**
     * Demonstrates the Stream API map operation for report preparation.
     */
    public List<String> getParcelTrackingNumbers() {
        return repository.findAll().stream()
                .map(Parcel::getTrackingNumber)
                .sorted()
                .toList();
    }

    public void updateStatus(String trackingNumber, DeliveryStatus status, String description)
            throws TrackingNumberNotFoundException {
        Parcel parcel = findParcel(trackingNumber);
        parcel.updateStatus(status, description);
        repository.updateStatus(parcel);
    }

    public Map<DeliveryStatus, Long> getStatistics() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Parcel::getStatus,
                        () -> new HashMap<>(), Collectors.counting()));
    }

    public Map<DeliveryStatus, List<Parcel>> groupParcelsByStatus() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Parcel::getStatus));
    }

    public List<Parcel> searchByRecipientName(String name) {
        String query = name == null ? "" : name.trim().toLowerCase();
        return repository.findAll().stream()
                .filter(parcel -> parcel.getRecipient().getFullName().toLowerCase().contains(query))
                .toList();
    }

    public double calculateDeliveryCost(Parcel parcel) {
        return deliveryStrategy.calculateDeliveryCost(parcel);
    }

    public String getDeliveryStrategyName() {
        return deliveryStrategy.getName();
    }

    public void setDeliveryStrategy(DeliveryStrategy deliveryStrategy) {
        if (deliveryStrategy == null) throw new IllegalArgumentException("Жеткізу стратегиясы қажет.");
        this.deliveryStrategy = deliveryStrategy;
    }

    public void exportReport(Path target) throws IOException {
        csvStorage.exportToCsv(repository.findAll(), target);
    }
}