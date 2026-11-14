package kz.kazpost.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrackingNumberGeneratorTest {
    @Test
    void generatedNumberHasKzPrefixAndTenDigits() {
        TrackingNumberGenerator generator = new TrackingNumberGenerator();
        String number = generator.generate();
        assertTrue(number.matches("KZ\\d{10}"), number);
    }

    @Test
    void uniqueGeneratorSkipsExistingNumbers() {
        TrackingNumberGenerator generator = new TrackingNumberGenerator();
        Set<String> existing = new HashSet<>();
        String first = generator.generateUnique(existing::contains);
        existing.add(first);
        String second = generator.generateUnique(existing::contains);
        assertNotEquals(first, second);
    }
}