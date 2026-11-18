package kz.kazpost.model;

public final class Recipient extends Person {
    public Recipient(String fullName, String phone, String address) {
        super(fullName, phone, address);
    }

    @Override
    public String getRoleLabel() {
        return "Алушы";
    }
}