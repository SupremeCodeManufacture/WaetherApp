package logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.supreme.manufacture.weather.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import data.App;
import data.model.CurrentWeatherObj;
import data.model.ForecastDayObj;
import data.model.LocationObj;
import logic.helpers.MyLogs;
import logic.listeners.OnLocationSelectedListener;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private Context mActivityCtx;
    private List<LocationObj> mData;
    private OnLocationSelectedListener mOnLocationSelectedListener;


    public LocationsAdapter(Context context, List<LocationObj> list, OnLocationSelectedListener listener) {
        this.mActivityCtx = context;
        this.mData = list;
        this.mOnLocationSelectedListener = listener;
    }

    public void onDeleteLoc(int pos) {
        mData.remove(pos);
        this.notifyItemRemoved(pos);

        if (getItemCount() == 0)
            mOnLocationSelectedListener.onEmptyLocations();
    }

    public void onAddItem(LocationObj locationObj) {
        this.mData.add(0, locationObj);
        this.notifyItemInserted(0);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_place_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        LocationObj locationObj = mData.get(position);

        if (locationObj != null) {
            viewHolder.tvName.setText(locationObj.getName());
            viewHolder.tvDescr.setText(locationObj.getCountry());

            CurrentWeatherObj currentWeatherObj = locationObj.getCurrentWeatherObj();
            if (currentWeatherObj != null) {
                viewHolder.tvCurTemp.setText(String.valueOf(currentWeatherObj.getTemp_c()));
                viewHolder.tvHumidity.setText(App.getAppCtx().getResources().getString(R.string.txt_hum) + ": " + currentWeatherObj.getHumidity() + "%  |  ");
                viewHolder.tvWind.setText(App.getAppCtx().getResources().getString(R.string.txt_wind) + ": " + currentWeatherObj.getWind_kph() + " km/h · " + currentWeatherObj.getWind_dir());

                Picasso.with(mActivityCtx)
                        .load("http://" + locationObj.getCurrentWeatherObj().getCondition().getIcon())
                        .fit()
                        .centerCrop()
                        .into(viewHolder.ivMood);

            } else {
                viewHolder.tvCurTemp.setText("-");
                viewHolder.tvHumidity.setText(App.getAppCtx().getResources().getString(R.string.txt_hum) + ": -  |  ");
                viewHolder.tvWind.setText(App.getAppCtx().getResources().getString(R.string.txt_wind) + ": -");

                viewHolder.ivMood.setImageResource(R.color.white);
            }

            ForecastDayObj forecastDayObj = locationObj.getForecastObj() != null ? locationObj.getForecastObj().getForecastday()[0] : null;
            if (forecastDayObj != null) {
                viewHolder.tvTmepMin.setText(String.valueOf(forecastDayObj.getDay().getMintemp_c()) + " °C");
                viewHolder.tvTempMax.setText(String.valueOf(forecastDayObj.getDay().getMaxtemp_c()) + " °C");

            } else {
                viewHolder.tvTmepMin.setText("-");
                viewHolder.tvTempMax.setText("-");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        MaterialCardView rvItm;
        TextView tvName, tvDescr, tvCurTemp, tvHumidity, tvWind, tvTmepMin, tvTempMax;
        ImageView ivMood;

        ViewHolder(final View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_title);
            tvDescr = (TextView) itemView.findViewById(R.id.tv_descr);
            tvCurTemp = (TextView) itemView.findViewById(R.id.tv_temp);
            tvHumidity = (TextView) itemView.findViewById(R.id.tv_humidity);
            tvWind = (TextView) itemView.findViewById(R.id.tv_wind);
            tvTmepMin = (TextView) itemView.findViewById(R.id.tv_temp_min);
            tvTempMax = (TextView) itemView.findViewById(R.id.tv_temp_max);
            ivMood = (ImageView) itemView.findViewById(R.id.iv_mood);

            rvItm = (MaterialCardView) itemView.findViewById(R.id.whole_loc_itm);
            rvItm.setOnClickListener(this);
            rvItm.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            final int clickedPosition = getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && mOnLocationSelectedListener != null) {
                switch (v.getId()) {
                    case R.id.whole_loc_itm:
                        mOnLocationSelectedListener.onLocationSelectedListener(mData.get(clickedPosition));
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            final int clickedPosition = getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && mOnLocationSelectedListener != null) {
                switch (v.getId()) {
                    case R.id.whole_loc_itm:
                        mOnLocationSelectedListener.onLocDeletedListener(mData.get(clickedPosition), clickedPosition);
                        break;
                }
            }

            return false;
        }
    }
}