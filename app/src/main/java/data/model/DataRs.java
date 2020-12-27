package data.model;

public class DataRs {

    private LocationObj location;
    private CurrentWeatherObj current;
    private ForecastObj forecast;
    private ErrObj error;


    public LocationObj getLocation() {
        return location;
    }

    public CurrentWeatherObj getCurrent() {
        return current;
    }

    public ForecastObj getForecast() {
        return forecast;
    }

    public ErrObj getError() {
        return error;
    }
}