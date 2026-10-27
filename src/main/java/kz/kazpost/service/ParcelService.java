
package kz.kazpost.service;

import kz.kazpost.model.Parcel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParcelService {

    private final Map<String, Parcel> parcels = new LinkedHashMap<>();

    public ParcelService() {
        addParcel(new Parcel(
                "KZ100001",
                "Айжан",
                "Аружан",
                "Алматы",
                "Астана",
                "recipient1@example.com"
        ));

        addParcel(new Parcel(
                "KZ100002",
                "Нұрлан",
                "Данияр",
                "Шымкент",
                "Алматы",
                "recipient2@example.com"
        ));

        addParcel(new Parcel(
                "KZ100003",
                "Әлия",
                "Мадина",
                "Астана",
                "Қарағанды",
                "recipient3@example.com"
        ));
    }

    public Parcel addParcel(Parcel parcel) {
        parcels.put(parcel.getTrackingNumber(), parcel);
        return parcel;
    }

    public Optional<Parcel> findByTrackingNumber(String trackingNumber) {
        return Optional.ofNullable(parcels.get(trackingNumber));
    }

    public List<Parcel> getAllParcels() {
        return new ArrayList<>(parcels.values());
    }

    public boolean updateStatus(String trackingNumber, String status) {
        Optional<Parcel> result = findByTrackingNumber(trackingNumber);

        if (result.isEmpty()) {
            return false;
        }

        result.get().setStatus(status);
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
