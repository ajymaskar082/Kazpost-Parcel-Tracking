
package kz.kazpost.service;

import kz.kazpost.factory.ParcelFactory;
import kz.kazpost.model.Parcel;
import kz.kazpost.observer.ParcelObserver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParcelService {

    private final Map<String, Parcel> parcels = new LinkedHashMap<>();
    private final List<ParcelObserver> observers = new ArrayList<>();
    private final ParcelFactory parcelFactory;

    public ParcelService(
            ParcelFactory parcelFactory,
            List<ParcelObserver> observers) {

        this.parcelFactory = parcelFactory;
        this.observers.addAll(observers);

        addParcel(parcelFactory.createParcel(
                "Айжан", "Аружан", "Алматы",
                "Астана", "recipient1@example.com"));

        addParcel(parcelFactory.createParcel(
                "Нұрлан", "Данияр", "Шымкент",
                "Алматы", "recipient2@example.com"));

        addParcel(parcelFactory.createParcel(
                "Әлия", "Мадина", "Астана",
                "Қарағанды", "recipient3@example.com"));
    }

    public Parcel addParcel(Parcel parcel) {
        parcels.put(parcel.getTrackingNumber(), parcel);
        return parcel;
    }

    public Parcel createParcel(
            String senderName,
            String recipientName,
            String senderCity,
            String recipientCity,
            String recipientEmail) {

        Parcel parcel = parcelFactory.createParcel(
                senderName, recipientName, senderCity,
                recipientCity, recipientEmail);

        return addParcel(parcel);
    }

    public Optional<Parcel> findByTrackingNumber(String trackingNumber) {
        return Optional.ofNullable(parcels.get(trackingNumber));
    }

    public List<Parcel> getAllParcels() {
        return new ArrayList<>(parcels.values());
    }

    public boolean updateStatus(String trackingNumber, String newStatus) {
        Optional<Parcel> result = findByTrackingNumber(trackingNumber);

        if (result.isEmpty()) {
            return false;
        }

        Parcel parcel = result.get();
        String oldStatus = parcel.getStatus();
        parcel.setStatus(newStatus);

        for (ParcelObserver observer : observers) {
            observer.onStatusChanged(parcel, oldStatus, newStatus);
        }

        return true;
    }

    public boolean deleteParcel(String trackingNumber) {
        return parcels.remove(trackingNumber) != null;
    }

    public int getParcelCount() {
        return parcels.size();
    }

    public Map<String, Parcel> getParcelMap() {
        return new LinkedHashMap<>(parcels);
    }
}
