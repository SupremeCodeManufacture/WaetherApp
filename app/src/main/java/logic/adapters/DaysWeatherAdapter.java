package logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.supreme.manufacture.weather.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import data.App;
import data.model.DayWeatherObj;
import data.model.ForecastDayObj;
import logic.helpers.DataFormatConverter;
import view.activity.MainActivity;

public class DaysWeatherAdapter extends RecyclerView.Adapter<DaysWeatherAdapter.ViewHolder> {

    private Context mActivityCtx;
    private ForecastDayObj[] mData;
    private String mTodayDate;


    public DaysWeatherAdapter(Context context,ForecastDayObj[] array) {
        this.mActivityCtx = context;
        this.mData = array;
        this.mTodayDate = DataFormatConverter.getTodatDate();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_day_weather_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        ForecastDayObj forecastDayObj = mData[position];

        if (forecastDayObj != null) {
            DayWeatherObj dayWeatherObj = forecastDayObj.getDay();

            viewHolder.tvDay.setText(DataFormatConverter.getPrettyDay(forecastDayObj.getDate()));
            viewHolder.tvDate.setText(DataFormatConverter.getPrettyWeekDay(forecastDayObj.getDate()));
            viewHolder.tvTempDay.setText(dayWeatherObj.getMaxtemp_c() + " °C");
            viewHolder.tvTempNight.setText(dayWeatherObj.getMintemp_c() + " °C");
            viewHolder.tvMood.setText(dayWeatherObj.getCondition().getText());
            viewHolder.tvWindSpeed.setText(dayWeatherObj.getMaxwind_kph() + " km/h");

            Picasso.with(mActivityCtx)
                    .load("http://" + dayWeatherObj.getCondition().getIcon())
                    .fit()
                    .centerCrop()
                    .into(viewHolder.ivMood);

            if (forecastDayObj.getDate().equals(mTodayDate)) {
                viewHolder.llWholeItm.setBackgroundColor(App.getAppCtx().getResources().getColor(R.color.light_gray));
            } else {
                viewHolder.llWholeItm.setBackgroundColor(App.getAppCtx().getResources().getColor(android.R.color.transparent));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llWholeItm;
        TextView tvDay, tvDate, tvTempDay, tvTempNight, tvMood, tvWindSpeed;
        ImageView ivMood;

        ViewHolder(final View itemView) {
            super(itemView);

            llWholeItm = (LinearLayout) itemView.findViewById(R.id.tv_whole_itm);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTempDay = (TextView) itemView.findViewById(R.id.tv_temp_day);
            tvTempNight = (TextView) itemView.findViewById(R.id.tv_temp_night);
            tvMood = (TextView) itemView.findViewById(R.id.tv_mood);
            tvWindSpeed = (TextView) itemView.findViewById(R.id.tv_wind);
            ivMood = (ImageView) itemView.findViewById(R.id.iv_mood);
        }
    }
}