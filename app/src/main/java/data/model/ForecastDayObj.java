package data.model;

public class ForecastDayObj {

    private String date;
    private long date_epoch;
    private DayWeatherObj day;
    private AstroObj astro;
    private HourWeatherObj[] hour;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDate_epoch() {
        return date_epoch;
    }

    public void setDate_epoch(long date_epoch) {
        this.date_epoch = date_epoch;
    }

    public DayWeatherObj getDay() {
        return day;
    }

    public void setDay(DayWeatherObj day) {
        this.day = day;
    }

    public AstroObj getAstro() {
        return astro;
    }

    public void setAstro(AstroObj astro) {
        this.astro = astro;
    }


    public HourWeatherObj[] getHour() {
        return hour;
    }

    public void setHour(HourWeatherObj[] hour) {
        this.hour = hour;
    }

}