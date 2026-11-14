package kz.kazpost.strategy;

import kz.kazpost.model.Parcel;

public interface DeliveryStrategy {
    double calculateDeliveryCost(Parcel parcel);
    String getName();
}