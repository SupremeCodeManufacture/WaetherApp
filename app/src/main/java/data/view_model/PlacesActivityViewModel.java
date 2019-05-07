package data.view_model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import data.model.LocationObj;

public class PlacesActivityViewModel extends ViewModel {

    private MutableLiveData<List<LocationObj>> myLocations = new MutableLiveData<List<LocationObj>>();


    public void setMyLocations(List<LocationObj> data) {
        myLocations.setValue(data);
    }

    public LiveData<List<LocationObj>> getMyLocations() {
        return myLocations;
    }

}