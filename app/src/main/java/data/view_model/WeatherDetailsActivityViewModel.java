package data.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import data.model.ForecastDayObj;

public class WeatherDetailsActivityViewModel extends ViewModel {

    private MutableLiveData<ForecastDayObj[]> myDayForecasts = new MutableLiveData<ForecastDayObj[]>();

    public void setForecasts(ForecastDayObj[] data) {
        myDayForecasts.setValue(data);
    }

    public LiveData<ForecastDayObj[]> getForecasts() {
        return myDayForecasts;
    }

}