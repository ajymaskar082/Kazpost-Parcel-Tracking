package kz.kazpost.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Parcel {
    private final String trackingNumber;
    private final Sender sender;
    private final Recipient recipient;
    private final PostOffice postOffice;
    private final double weight;
    private final LocalDateTime createdAt;
    private DeliveryStatus status;
    private final List<TrackingEvent> trackingHistory;

    public Parcel(String trackingNumber, Sender sender, Recipient recipient,
                  PostOffice postOffice, double weight) {
        this(trackingNumber, sender, recipient, postOffice, weight,
                DeliveryStatus.REGISTERED, LocalDateTime.now(),
                List.of(TrackingEvent.create(DeliveryStatus.REGISTERED, "Сәлемдеме жүйеге тіркелді")));
    }

    public Parcel(String trackingNumber, Sender sender, Recipient recipient, PostOffice postOffice,
                  double weight, DeliveryStatus status, LocalDateTime createdAt,
                  List<TrackingEvent> trackingHistory) {
        this.trackingNumber = requireText(trackingNumber, "Трек-нөмір");
        this.sender = Objects.requireNonNull(sender, "Жіберуші көрсетілуі керек.");
        this.recipient = Objects.requireNonNull(recipient, "Алушы көрсетілуі керек.");
        this.postOffice = Objects.requireNonNull(postOffice, "Пошта бөлімшесі көрсетілуі керек.");
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException("Салмақ нөлден үлкен болуы керек.");
        }
        this.weight = weight;
        this.status = Objects.requireNonNull(status, "Статус көрсетілуі керек.");
        this.createdAt = Objects.requireNonNull(createdAt, "Тіркелу уақыты көрсетілуі керек.");
        this.trackingHistory = new ArrayList<>(trackingHistory == null ? List.of() : trackingHistory);
        if (this.trackingHistory.isEmpty()) {
            this.trackingHistory.add(TrackingEvent.create(status, "Бастапқы статус"));
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " бос болмауы керек.");
        }
        return value.trim();
    }

    public void updateStatus(DeliveryStatus newStatus, String description) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Статусты " + status + " күйінен " + newStatus + " күйіне ауыстыруға болмайды.");
        }
        status = newStatus;
        trackingHistory.add(TrackingEvent.create(newStatus, description));
    }

    public String getTrackingNumber() { return trackingNumber; }
    public Sender getSender() { return sender; }
    public Recipient getRecipient() { return recipient; }
    public PostOffice getPostOffice() { return postOffice; }
    public double getWeight() { return weight; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public DeliveryStatus getStatus() { return status; }

    public List<TrackingEvent> getTrackingHistory() {
        return Collections.unmodifiableList(trackingHistory);
    }
}