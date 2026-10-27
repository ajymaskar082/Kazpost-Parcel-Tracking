
package kz.kazpost.factory;

import kz.kazpost.model.Parcel;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ParcelFactory {

    public Parcel createParcel(
            String senderName,
            String recipientName,
            String senderCity,
            String recipientCity,
            String recipientEmail) {

        String trackingNumber = generateTrackingNumber();

        return new Parcel(
                trackingNumber,
                senderName,
                recipientName,
                senderCity,
                recipientCity,
                recipientEmail
        );
    }

    private String generateTrackingNumber() {
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        return "KZ" + randomPart;
    }
}
