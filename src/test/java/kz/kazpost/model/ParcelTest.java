package kz.kazpost.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParcelTest {
    private Parcel createParcel(double weight) {
        return new Parcel("KZ0000000001",
                new Sender("Асан", "+77000000001", "Алматы"),
                new Recipient("Үсен", "+77000000002", "Астана"),
                new PostOffice("A-01", "Абай 1", "Алматы"),
                weight);
    }

    @Test
    void newParcelStartsRegisteredWithInitialEvent() {
        Parcel parcel = createParcel(2.5);
        assertEquals(DeliveryStatus.REGISTERED, parcel.getStatus());
        assertEquals(1, parcel.getTrackingHistory().size());
    }

    @Test
    void validStatusUpdateAddsHistoryEvent() {
        Parcel parcel = createParcel(1.0);
        parcel.updateStatus(DeliveryStatus.ACCEPTED, "Бөлімше қабылдады");
        assertEquals(DeliveryStatus.ACCEPTED, parcel.getStatus());
        assertEquals(2, parcel.getTrackingHistory().size());
    }

    @Test
    void invalidStatusTransitionIsRejected() {
        Parcel parcel = createParcel(1.0);
        assertThrows(IllegalStateException.class,
                () -> parcel.updateStatus(DeliveryStatus.DELIVERED, "Бірден жеткізілді"));
    }

    @Test
    void nonPositiveWeightIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> createParcel(0));
        assertThrows(IllegalArgumentException.class, () -> createParcel(-1));
    }

    @Test
    void trackingHistoryCannotBeModifiedExternally() {
        Parcel parcel = createParcel(1.0);
        assertThrows(UnsupportedOperationException.class,
                () -> parcel.getTrackingHistory().clear());
    }
}