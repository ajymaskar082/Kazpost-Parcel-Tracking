package kz.kazpost.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Generates a KZ-prefixed ten-digit tracking number.
 */
public class TrackingNumberGenerator {
    private static final int MAX_ATTEMPTS = 100;
    private static final int UPPER_BOUND = Integer.MAX_VALUE;
    private final SecureRandom random;

    public TrackingNumberGenerator() {
        this(new SecureRandom());
    }

    public TrackingNumberGenerator(SecureRandom random) {
        this.random = Objects.requireNonNull(random, "Random generator is required");
    }

    public String generate() {
        int number = random.nextInt(UPPER_BOUND);
        return "KZ" + String.format(Locale.ROOT, "%010d", number);
    }

    public String generateUnique(Predicate<String> alreadyExists) {
        Objects.requireNonNull(alreadyExists, "Existence checker is required");
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String candidate = generate();
            if (!alreadyExists.test(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Бірегей трек-нөмірді жасау мүмкін болмады.");
    }
}