package kz.kazpost.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusTest {
    @Test
    void registeredCanMoveToAccepted() {
        assertTrue(DeliveryStatus.REGISTERED.canTransitionTo(DeliveryStatus.ACCEPTED));
    }

    @Test
    void acceptedCanMoveToInTransit() {
        assertTrue(DeliveryStatus.ACCEPTED.canTransitionTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void inTransitCanArriveOrBeLost() {
        assertTrue(DeliveryStatus.IN_TRANSIT.canTransitionTo(DeliveryStatus.ARRIVED_AT_OFFICE));
        assertTrue(DeliveryStatus.IN_TRANSIT.canTransitionTo(DeliveryStatus.LOST));
    }

    @Test
    void deliveredIsTerminal() {
        assertFalse(DeliveryStatus.DELIVERED.canTransitionTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void sameStatusIsNotAValidTransition() {
        assertFalse(DeliveryStatus.ACCEPTED.canTransitionTo(DeliveryStatus.ACCEPTED));
    }
}