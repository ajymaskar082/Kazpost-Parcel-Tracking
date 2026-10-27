
package kz.kazpost.service;

import kz.kazpost.factory.ParcelFactory;
import kz.kazpost.model.Parcel;
import kz.kazpost.observer.ParcelObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParcelServiceTest {

    private ParcelService parcelService;

    @BeforeEach
    void setUp() {
        ParcelFactory factory = mock(ParcelFactory.class);

        when(factory.createParcel(anyString(), anyString(),
                anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    Parcel parcel = mock(Parcel.class);
                    String trackingNumber = "KZ"
                            + Math.abs(invocation.getArgument(0).hashCode());
                    when(parcel.getTrackingNumber())
                            .thenReturn(trackingNumber);
                    when(parcel.getStatus()).thenReturn("Қабылданды");
                    return parcel;
                });

        parcelService = new ParcelService(factory, List.of());
    }

    @Test
    void shouldReturnThreeSampleParcels() {
        assertEquals(3, parcelService.getParcelCount());
        assertEquals(3, parcelService.getAllParcels().size());
    }

    @Test
    void shouldFindParcelByTrackingNumber() {
        String trackingNumber = parcelService.getAllParcels()
                .get(0).getTrackingNumber();

        assertTrue(parcelService
                .findByTrackingNumber(trackingNumber).isPresent());
    }

    @Test
    void shouldReturnEmptyForUnknownTrackingNumber() {
        assertTrue(parcelService
                .findByTrackingNumber("UNKNOWN").isEmpty());
    }

    @Test
    void shouldUpdateParcelStatus() {
        String trackingNumber = parcelService.getAllParcels()
                .get(0).getTrackingNumber();

        assertTrue(parcelService.updateStatus(
                trackingNumber, "Жеткізілді"));
    }

    @Test
    void shouldReturnFalseForUnknownParcelStatusUpdate() {
        assertFalse(parcelService.updateStatus(
                "UNKNOWN", "Жеткізілді"));
    }

    @Test
    void shouldDeleteParcel() {
        String trackingNumber = parcelService.getAllParcels()
                .get(0).getTrackingNumber();

        assertTrue(parcelService.deleteParcel(trackingNumber));
        assertEquals(2, parcelService.getParcelCount());
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownParcel() {
        assertFalse(parcelService.deleteParcel("UNKNOWN"));
    }
}
