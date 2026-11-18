package kz.kazpost.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {
    @Test
    void subclassesProvideTheirRoleThroughAbstractPersonReference() {
        Person sender = new Sender("Айжан", "+77010000001", "Алматы");
        Person recipient = new Recipient("Марат", "+77010000002", "Астана");

        assertEquals("Жіберуші", sender.getRoleLabel());
        assertEquals("Алушы", recipient.getRoleLabel());
    }

    @Test
    void commonPersonFieldsAreTrimmedAndAvailableThroughGetters() {
        Person sender = new Sender("  Айжан  ", " +77010000001 ", " Алматы ");

        assertEquals("Айжан", sender.getFullName());
        assertEquals("+77010000001", sender.getPhone());
        assertEquals("Алматы", sender.getAddress());
    }
}