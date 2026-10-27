
package kz.kazpost.observer;

import kz.kazpost.model.Parcel;
import kz.kazpost.service.GmailService;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class EmailNotificationObserver implements ParcelObserver {

    private static final Logger LOGGER =
            Logger.getLogger(EmailNotificationObserver.class.getName());

    private final GmailService gmailService;

    public EmailNotificationObserver(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @Override
    public void onStatusChanged(
            Parcel parcel,
            String oldStatus,
            String newStatus) {

        String recipient = parcel.getRecipientEmail();

        if (recipient == null || recipient.isBlank()
                || recipient.endsWith("@example.com")) {
            LOGGER.info("Тесттік email қолданылғандықтан хат жіберілмеді: "
                    + parcel.getTrackingNumber());
            return;
        }

        String subject = "Қазпошта: сәлемдеме мәртебесі жаңарды";

        String body = "Сәлеметсіз бе, " + parcel.getRecipientName() + "!\n\n"
                + "Сіздің сәлемдемеңіздің мәртебесі өзгерді.\n"
                + "Трек-нөмір: " + parcel.getTrackingNumber() + "\n"
                + "Бұрынғы мәртебесі: " + oldStatus + "\n"
                + "Жаңа мәртебесі: " + newStatus + "\n"
                + "Бағыты: " + parcel.getSenderCity() + " → "
                + parcel.getRecipientCity() + "\n\n"
                + "Қазпошта сәлемдеме трекингі.";

        try {
            gmailService.sendEmail(recipient, subject, body);
            LOGGER.info("Хат жіберілді: " + parcel.getTrackingNumber());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING,
                    "Email жіберу сәтсіз аяқталды.", exception);
        }
    }
}
