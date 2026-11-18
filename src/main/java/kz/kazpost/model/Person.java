package kz.kazpost.model;

import java.util.Objects;

/**
 * Common information shared by senders and recipients.
 * Private fields provide encapsulation; subclasses provide their role label.
 */
public abstract class Person {
    private final String fullName;
    private final String phone;
    private final String address;

    protected Person(String fullName, String phone, String address) {
        this.fullName = requireText(fullName, "Аты-жөні");
        this.phone = requireText(phone, "Телефон");
        this.address = requireText(address, "Мекенжай");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " бос болмауы керек.");
        }
        return value.trim();
    }

    public abstract String getRoleLabel();

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Person person)) return false;
        return phone.equals(person.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }
}