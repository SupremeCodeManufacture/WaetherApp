package logic.listeners;

import data.model.LocationObj;

public interface OnLocationSelectedListener {

    void onLocationSelectedListener(LocationObj locationObj);

    void onLocDeletedListener(LocationObj locationObj, int pos);

    void onEmptyLocations();
}