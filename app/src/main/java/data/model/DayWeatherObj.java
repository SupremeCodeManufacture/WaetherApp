package data.model;

public class DayWeatherObj {

    private float maxtemp_c;
    private float mintemp_c;
    private float avgtemp_c;
    private float maxwind_kph;
    private float totalprecip_mm;
    private float avgvis_km;
    private float avghumidity;
    private ConditionObj condition;
    private float uv;

    public float getMaxtemp_c() {
        return maxtemp_c;
    }

    public void setMaxtemp_c(float maxtemp_c) {
        this.maxtemp_c = maxtemp_c;
    }

    public float getMintemp_c() {
        return mintemp_c;
    }

    public void setMintemp_c(float mintemp_c) {
        this.mintemp_c = mintemp_c;
    }

    public float getAvgtemp_c() {
        return avgtemp_c;
    }

    public void setAvgtemp_c(float avgtemp_c) {
        this.avgtemp_c = avgtemp_c;
    }

    public float getMaxwind_kph() {
        return maxwind_kph;
    }

    public void setMaxwind_kph(float maxwind_kph) {
        this.maxwind_kph = maxwind_kph;
    }

    public float getTotalprecip_mm() {
        return totalprecip_mm;
    }

    public void setTotalprecip_mm(float totalprecip_mm) {
        this.totalprecip_mm = totalprecip_mm;
    }

    public float getAvgvis_km() {
        return avgvis_km;
    }

    public void setAvgvis_km(float avgvis_km) {
        this.avgvis_km = avgvis_km;
    }

    public float getAvghumidity() {
        return avghumidity;
    }

    public void setAvghumidity(float avghumidity) {
        this.avghumidity = avghumidity;
    }

    public ConditionObj getCondition() {
        return condition;
    }

    public void setCondition(ConditionObj condition) {
        this.condition = condition;
    }

    public float getUv() {
        return uv;
    }

    public void setUv(float uv) {
        this.uv = uv;
    }
}
