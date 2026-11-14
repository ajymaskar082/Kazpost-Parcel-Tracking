package kz.kazpost.factory;

import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;

public class ParcelFactory {
    public Parcel create(String trackingNumber, Sender sender, Recipient recipient,
                         PostOffice postOffice, double weight) {
        return new Parcel(trackingNumber, sender, recipient, postOffice, weight);
    }
}