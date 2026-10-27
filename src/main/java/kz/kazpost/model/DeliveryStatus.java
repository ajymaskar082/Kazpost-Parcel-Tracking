
package kz.kazpost.model;

public enum DeliveryStatus {

    ACCEPTED("Қабылданды"),
    IN_TRANSIT("Жолда"),
    AT_POST_OFFICE("Бөлімшеде"),
    OUT_FOR_DELIVERY("Жеткізуге шықты"),
    DELIVERED("Жеткізілді"),
    RETURNED("Жіберушіге қайтарылды");

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
