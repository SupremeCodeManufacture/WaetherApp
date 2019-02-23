package data.view_model;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import data.App;
import data.model.LocationObj;
import logic.async_await.AsyncTaskWorker;
import logic.async_await.CallableObj;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.DataFormatConverter;
import logic.helpers.MyLogs;

public class PlacesActivityViewModel extends ViewModel {

    private MutableLiveData<List<LocationObj>> myLocations = new MutableLiveData<List<LocationObj>>();


    public void setMyLocations(List<LocationObj> data) {
        myLocations.setValue(data);
    }

    public void addLocation(final LocationObj location) {
        MyLogs.LOG("PlacesActivityViewModel", "addLocation", "location: " + location.getName());

        new AsyncTaskWorker(
                new CallableObj<List<LocationObj>>() {
                    public List<LocationObj> call() {
                        List<LocationObj> list = getMyLocations().getValue();

                        if (list != null) {
                            MyLogs.LOG("PlacesActivityViewModel", "addLocation", "====> 1");

                            for (LocationObj locationObj : list) {
                                MyLogs.LOG("PlacesActivityViewModel", "addLocation", "====> 2");
                                if (!locationObj.getCountry().equals(location.getCountry()) || !locationObj.getName().equals(location.getName())) {
                                    MyLogs.LOG("PlacesActivityViewModel", "addLocation", "====> 3");
                                    list.add(location);
                                }
                            }

                        } else {
                            MyLogs.LOG("PlacesActivityViewModel", "addLocation", "====> 4");
                            list = new ArrayList<>();
                            list.add(location);
                        }

                        App.setStorePlaces(new Gson().toJson(list));

                        return list;
                    }
                },
                new OnAsyncDoneRsObjListener() {
                    @Override
                    public <T> void onDone(T t) {
                        if (t != null) {
                            myLocations.setValue((List<LocationObj>) t);
                        }
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public LiveData<List<LocationObj>> getMyLocations() {
        if (myLocations.getValue() == null) {
            setMyLocations(DataFormatConverter.getMyStoredLocations());
        }

        return myLocations;
    }

}