
package kz.kazpost.service;

import org.springframework.stereotype.Service;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class GmailService {

    public void sendEmail(
            String recipient,
            String subject,
            String body) {

        String username = System.getenv("GMAIL_USERNAME");
        String appPassword = System.getenv("GMAIL_APP_PASSWORD");

        if (username == null || appPassword == null
                || username.isBlank() || appPassword.isBlank()) {
            throw new IllegalStateException(
                    "GMAIL_USERNAME және GMAIL_APP_PASSWORD "
                    + "айнымалыларын баптау қажет.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                username, appPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Электрондық хат жіберілмеді.", exception);
        }
    }
}
