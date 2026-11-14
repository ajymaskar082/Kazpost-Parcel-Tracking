package kz.kazpost.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class TrackingEvent {
    private final long eventId;
    private final DeliveryStatus status;
    private final LocalDateTime eventDate;
    private final String description;

    public TrackingEvent(long eventId, DeliveryStatus status, LocalDateTime eventDate, String description) {
        if (status == null) {
            throw new IllegalArgumentException("Статус көрсетілуі керек.");
        }
        this.eventId = eventId;
        this.status = status;
        this.eventDate = Objects.requireNonNull(eventDate, "Оқиға уақыты көрсетілуі керек.");
        this.description = description == null ? "" : description.trim();
    }

    public static TrackingEvent create(DeliveryStatus status, String description) {
        return new TrackingEvent(0, status, LocalDateTime.now(), description);
    }

    public long getEventId() {
        return eventId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getDescription() {
        return description;
    }

    public String getEventInfo() {
        return eventDate + " | " + status.getKazakhLabel() + " | " + description;
    }
}