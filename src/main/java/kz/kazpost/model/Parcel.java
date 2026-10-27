
package kz.kazpost.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Parcel {

    private String trackingNumber;
    private String senderName;
    private String recipientName;
    private String senderCity;
    private String recipientCity;
    private String recipientEmail;
    private String status;
    private LocalDateTime createdAt;
    private final List<String> trackingHistory = new ArrayList<>();

    public Parcel() {
        this.createdAt = LocalDateTime.now();
        this.status = "Қабылданды";
    }

    public Parcel(String trackingNumber, String senderName,
                  String recipientName, String senderCity,
                  String recipientCity, String recipientEmail) {
        this();
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.senderCity = senderCity;
        this.recipientCity = recipientCity;
        this.recipientEmail = recipientEmail;
        addTrackingEvent("Сәлемдеме қабылданды");
    }

    public void addTrackingEvent(String event) {
        trackingHistory.add(LocalDateTime.now() + " — " + event);
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }

    public String getRecipientCity() {
        return recipientCity;
    }

    public void setRecipientCity(String recipientCity) {
        this.recipientCity = recipientCity;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        addTrackingEvent("Мәртебесі өзгерді: " + status);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getTrackingHistory() {
        return new ArrayList<>(trackingHistory);
    }
}
