package data.model;

public class CurrentWeatherObj {

    private float temperature;
    private float feelslike;
    private float weather_code;
    private float wind_speed;
    private float wind_degree;
    private String wind_dir;
    private String pressure;
    private String precip;
    private int humidity;
    private int cloudcover;
    private float uv_index;
    private float visibility;
    private String[] weather_icons;
    private String[] weather_descriptions;

    public float getTemperature() {
        return temperature;
    }

    public float getFeelslike() {
        return feelslike;
    }

    public float getWeather_code() {
        return weather_code;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public float getWind_degree() {
        return wind_degree;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public String getPressure() {
        return pressure;
    }

    public String getPrecip() {
        return precip;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public float getUv_index() {
        return uv_index;
    }

    public float getVisibility() {
        return visibility;
    }

    public String[] getWeather_icons() {
        return weather_icons;
    }

    public String[] getWeather_descriptions() {
        return weather_descriptions;
    }
}