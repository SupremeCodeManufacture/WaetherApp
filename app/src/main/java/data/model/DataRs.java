package data.model;

public class DataRs extends  ErrObj {

    private LocationObj location;
    private CurrentWeatherObj current;
    private ForecastObj forecast;


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
}
