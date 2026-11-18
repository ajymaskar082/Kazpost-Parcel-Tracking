package kz.kazpost.model;

public final class Sender extends Person {
    public Sender(String fullName, String phone, String address) {
        super(fullName, phone, address);
    }

    @Override
    public String getRoleLabel() {
        return "Жіберуші";
    }
}