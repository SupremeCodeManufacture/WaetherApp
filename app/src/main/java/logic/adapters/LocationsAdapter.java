package logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supreme.manufacture.weather.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import data.App;
import data.model.LocationObj;
import logic.listeners.OnLocationSelectedListener;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<LocationObj> mData;
    private OnLocationSelectedListener mOnLocationSelectedListener;


    public LocationsAdapter(List<LocationObj> list, OnLocationSelectedListener listener) {
        this.mData = list;
        this.mOnLocationSelectedListener = listener;
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

            if (position % 2 == 1) {
                viewHolder.rvItm.setBackgroundColor(App.getAppCtx().getResources().getColor(R.color.white));

            } else {
                viewHolder.rvItm.setBackgroundColor(App.getAppCtx().getResources().getColor(R.color.light_gray));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rvItm;
        TextView tvName, tvDescr;
        ImageButton btnDelete;

        ViewHolder(final View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_title);
            tvDescr = (TextView) itemView.findViewById(R.id.tv_descr);

            rvItm = (RelativeLayout) itemView.findViewById(R.id.whole_loc_itm);
            rvItm.setOnClickListener(this);

            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            final int clickedPosition = getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && mOnLocationSelectedListener != null) {
                mOnLocationSelectedListener.onLocationSelectedListener(mData.get(clickedPosition));
            }
        }
    }
}