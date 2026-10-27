
package kz.kazpost.observer;

import kz.kazpost.model.Parcel;

public interface ParcelObserver {

    void onStatusChanged(Parcel parcel, String oldStatus, String newStatus);
}
