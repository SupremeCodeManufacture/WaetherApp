package logic.helpers;

import com.supreme.manufacture.weather.R;

public class ThemeColorsHelper {

    public static int getTheme(boolean isDay) {
        return isDay ? R.style.AppThemeDay : R.style.AppThemeNight;
    }

    public static int getTitleTheme(boolean isDay) {
        return isDay ? R.style.AppActionBarThemeDay : R.style.AppActionBarThemeNight;
    }


    public static int getColorPrimary(boolean isDay) {
        return isDay ? R.color.primary : R.color.primary_night;
    }

    public static int getColorPrimaryDark(boolean isDay) {
        return isDay ? R.color.primary_dark : R.color.primary_dark_night;
    }

    public static int getColorPrimaryLight(boolean isDay) {
        return isDay ? R.color.primary_light : R.color.primary_light_night;
    }
}