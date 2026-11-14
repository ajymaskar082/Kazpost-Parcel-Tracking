package kz.kazpost.model;

public final class Sender extends Person {
    public Sender(String fullName, String phone, String address) {
        super(fullName, phone, address);
    }

    public void sendParcel() {
        // Domain action: parcel registration is handled by ParcelService.
    }
}