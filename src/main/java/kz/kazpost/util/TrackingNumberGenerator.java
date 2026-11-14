package kz.kazpost.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.function.Predicate;

public class TrackingNumberGenerator {
    private static final int MAX_ATTEMPTS = 20;
    private final SecureRandom random;

    public TrackingNumberGenerator() {
        this(new SecureRandom());
    }

    public TrackingNumberGenerator(SecureRandom random) {
        this.random = random;
    }

    public String generate() {
        return "KZ" + String.format(Locale.ROOT, "%010d", random.nextInt(10_000_000_000L > Integer.MAX_VALUE
                ? Integer.MAX_VALUE : Integer.MAX_VALUE));
    }

    public String generateUnique(Predicate<String> alreadyExists) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String candidate = generate();
            if (!alreadyExists.test(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Бірегей трек-нөмірді жасау мүмкін болмады.");
    }
}