package data.model;

public class HourWeatherObj {

    private String time;
    private long time_epoch;
    private float temp_c;
    private float temp_f;
    private int is_day;
    private ConditionObj condition;
    private float wind_kph;
    private float wind_mph;
    private float wind_degree;
    private String wind_dir;
    private float pressure_mb;
    private float pressure_in;
    private float precip_mm;
    private int humidity;
    private int cloud;
    private float feelslike_c;
    private float feelslike_f;
    private float vis_km;

    private int heightValDp;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTime_epoch() {
        return time_epoch;
    }

    public void setTime_epoch(long time_epoch) {
        this.time_epoch = time_epoch;
    }

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

    public int getHeightValDp() {
        return heightValDp;
    }

    public void setHeightValDp(int heightValDp) {
        this.heightValDp = heightValDp;
    }

    public float getTemp_f() {
        return temp_f;
    }

    public void setTemp_f(float temp_f) {
        this.temp_f = temp_f;
    }

    public float getWind_mph() {
        return wind_mph;
    }

    public void setWind_mph(float wind_mph) {
        this.wind_mph = wind_mph;
    }

    public float getPressure_in() {
        return pressure_in;
    }

    public void setPressure_in(float pressure_in) {
        this.pressure_in = pressure_in;
    }

    public float getFeelslike_f() {
        return feelslike_f;
    }

    public void setFeelslike_f(float feelslike_f) {
        this.feelslike_f = feelslike_f;
    }
}
