package kz.kazpost.exception;

public class TrackingNumberNotFoundException extends Exception {
    public TrackingNumberNotFoundException(String trackingNumber) {
        super("Сәлемдеме табылмады. Трек-нөмір: " + trackingNumber);
    }
}