package kz.kazpost.model;

import java.util.EnumSet;
import java.util.Set;

public enum DeliveryStatus {
    REGISTERED,
    ACCEPTED,
    IN_TRANSIT,
    ARRIVED_AT_OFFICE,
    DELIVERED,
    LOST;

    public boolean canTransitionTo(DeliveryStatus next) {
        if (next == null || next == this) {
            return false;
        }
        return switch (this) {
            case REGISTERED -> next == ACCEPTED;
            case ACCEPTED -> next == IN_TRANSIT;
            case IN_TRANSIT -> EnumSet.of(ARRIVED_AT_OFFICE, LOST).contains(next);
            case ARRIVED_AT_OFFICE -> EnumSet.of(IN_TRANSIT, DELIVERED, LOST).contains(next);
            case DELIVERED, LOST -> false;
        };
    }

    public static Set<DeliveryStatus> activeStatuses() {
        return EnumSet.of(REGISTERED, ACCEPTED, IN_TRANSIT, ARRIVED_AT_OFFICE);
    }

    public String getKazakhLabel() {
        return switch (this) {
            case REGISTERED -> "Тіркелді";
            case ACCEPTED -> "Қабылданды";
            case IN_TRANSIT -> "Жолда";
            case ARRIVED_AT_OFFICE -> "Бөлімшеге келді";
            case DELIVERED -> "Жеткізілді";
            case LOST -> "Жоғалды";
        };
    }
}