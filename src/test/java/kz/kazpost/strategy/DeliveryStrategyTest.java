package kz.kazpost.strategy;

import kz.kazpost.model.Parcel;
import kz.kazpost.model.PostOffice;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStrategyTest {
    private Parcel parcel() {
        return new Parcel("KZ0000000201",
                new Sender("Асан", "+77110000001", "Алматы"),
                new Recipient("Үсен", "+77110000002", "Астана"),
                new PostOffice("STR-01", "Абай 1", "Алматы"),
                2.0);
    }

    @Test
    void standardStrategyCalculatesSamplePrice() {
        assertEquals(1300.0, new StandardDeliveryStrategy().calculateDeliveryCost(parcel()));
    }

    @Test
    void expressStrategyCalculatesSamplePrice() {
        assertEquals(2400.0, new ExpressDeliveryStrategy().calculateDeliveryCost(parcel()));
    }

    @Test
    void expressStrategyCostsMoreThanStandardStrategy() {
        Parcel parcel = parcel();
        double standard = new StandardDeliveryStrategy().calculateDeliveryCost(parcel);
        double express = new ExpressDeliveryStrategy().calculateDeliveryCost(parcel);
        assertTrue(express > standard);
    }
}