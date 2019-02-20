package data.model;

public class CurrentWeatherObj {

    private float temp_c;
    private int is_day;
    private ConditionObj condition;
    private float wind_kph;
    private float wind_degree;
    private String wind_dir;
    private float pressure_mb;
    private float precip_mm;
    private int humidity;
    private int cloud;
    private  float feelslike_c;
    private float vis_km;
    private  float uv;


    public float getTemp_c() {
        return temp_c;
    }

    public void setTemp_c(float temp_c) {
        this.temp_c = temp_c;
    }

    public int getIs_day() {
        return is_day;
    }

    public void setIs_day(int is_day) {
        this.is_day = is_day;
    }

    public ConditionObj getCondition() {
        return condition;
    }

    public void setCondition(ConditionObj condition) {
        this.condition = condition;
    }

    public float getWind_kph() {
        return wind_kph;
    }

    public void setWind_kph(float wind_kph) {
        this.wind_kph = wind_kph;
    }

    public float getWind_degree() {
        return wind_degree;
    }

    public void setWind_degree(float wind_degree) {
        this.wind_degree = wind_degree;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public float getPressure_mb() {
        return pressure_mb;
    }

    public void setPressure_mb(float pressure_mb) {
        this.pressure_mb = pressure_mb;
    }

    public float getPrecip_mm() {
        return precip_mm;
    }

    public void setPrecip_mm(float precip_mm) {
        this.precip_mm = precip_mm;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getCloud() {
        return cloud;
    }

    public void setCloud(int cloud) {
        this.cloud = cloud;
    }

    public float getFeelslike_c() {
        return feelslike_c;
    }

    public void setFeelslike_c(float feelslike_c) {
        this.feelslike_c = feelslike_c;
    }

    public float getVis_km() {
        return vis_km;
    }

    public void setVis_km(float vis_km) {
        this.vis_km = vis_km;
    }

    public float getUv() {
        return uv;
    }

    public void setUv(float uv) {
        this.uv = uv;
    }
}
