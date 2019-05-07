package data.model;

public class DataRs {

    private LocationObj location;
    private CurrentWeatherObj current;
    private ForecastObj forecast;
    private ErrObj error;


    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public CurrentWeatherObj getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeatherObj current) {
        this.current = current;
    }

    public ForecastObj getForecast() {
        return forecast;
    }

    public void setForecast(ForecastObj forecast) {
        this.forecast = forecast;
    }

    public ErrObj getError() {
        return error;
    }

    public void setError(ErrObj error) {
        this.error = error;
    }
}