package logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.supreme.manufacture.weather.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import data.App;
import data.SettingsPreferences;
import data.model.HourWeatherObj;
import logic.helpers.DataFormatConverter;

public class HoursWeatherAdapter extends RecyclerView.Adapter<HoursWeatherAdapter.ViewHolder> {

    private List<HourWeatherObj> mData;

    public HoursWeatherAdapter(List<HourWeatherObj> list) {
        this.mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        HourWeatherObj hourWeatherObj = mData.get(position);

        if (hourWeatherObj != null) {
            //temp
            String degreeType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_temp), "°C");
            String temp = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(hourWeatherObj.getTemp_c()) : String.valueOf(hourWeatherObj.getTemp_f());
            viewHolder.tvTemp.setText(temp + degreeType);

            //wind
            String speedType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_wind_speed), "kph");
            String speed = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.wind_messures_values)[0]) ? String.valueOf(hourWeatherObj.getWind_kph()) : String.valueOf(hourWeatherObj.getWind_mph());
            viewHolder.tvWind.setText(speed + " " + speedType);

            viewHolder.tvHour.setText(DataFormatConverter.getPrettyHour(hourWeatherObj.getTime()));
            viewHolder.viewTemp.setLayoutParams(DataFormatConverter.getAudienceViewParams(hourWeatherObj.getHeightValDp()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemp, tvWind, tvHour;
        View viewTemp;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTemp = (TextView) itemView.findViewById(R.id.tv_temp);
            tvWind = (TextView) itemView.findViewById(R.id.tv_wind);
            tvHour = (TextView) itemView.findViewById(R.id.tv_time);

            viewTemp = (View) itemView.findViewById(R.id.view_temp);
        }
    }
}