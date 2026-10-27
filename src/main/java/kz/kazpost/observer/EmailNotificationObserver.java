
package kz.kazpost.observer;

import kz.kazpost.model.Parcel;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class EmailNotificationObserver implements ParcelObserver {

    private static final Logger LOGGER =
            Logger.getLogger(EmailNotificationObserver.class.getName());

    @Override
    public void onStatusChanged(
            Parcel parcel,
            String oldStatus,
            String newStatus) {

        String message = "Сәлемдеме: " + parcel.getTrackingNumber()
                + "\nАлушы: " + parcel.getRecipientName()
                + "\nБұрынғы мәртебесі: " + oldStatus
                + "\nЖаңа мәртебесі: " + newStatus;

        LOGGER.info("Электрондық хабарлама дайындалды:\n" + message);
    }
}
