package kz.kazpost.model;

public final class PostOffice {
    private final String officeCode;
    private final String address;
    private final String city;

    public PostOffice(String officeCode, String address, String city) {
        this.officeCode = requireText(officeCode, "Бөлімше коды");
        this.address = requireText(address, "Бөлімше мекенжайы");
        this.city = requireText(city, "Қала");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " бос болмауы керек.");
        }
        return value.trim();
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getOfficeInfo() {
        return officeCode + " — " + city + ", " + address;
    }
}