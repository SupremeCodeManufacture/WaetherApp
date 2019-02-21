package logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.supreme.manufacture.weather.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import data.model.HourWeatherObj;
import logic.helpers.DataFormatConverter;

public class HoursWeatherAdapter extends RecyclerView.Adapter<HoursWeatherAdapter.ViewHolder> {

    private HourWeatherObj[] mData;

    public HoursWeatherAdapter(HourWeatherObj[] array) {
        this.mData = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        HourWeatherObj hourWeatherObj = mData[position];

        if (hourWeatherObj != null) {
            viewHolder.tvTemp.setText(String.valueOf(hourWeatherObj.getTemp_c()) + " °C");
            viewHolder.tvWind.setText(String.valueOf(hourWeatherObj.getWind_kph()) + " km/h");
            viewHolder.tvHour.setText(DataFormatConverter.getPrettyHour(hourWeatherObj.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.length;
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