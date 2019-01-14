package com.intkhabahmed.solarcalculator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intkhabahmed.solarcalculator.R;
import com.intkhabahmed.solarcalculator.model.PinInfo;

import java.util.List;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.PinViewHolder> {

    private List<PinInfo> mPins;
    private OnItemClickListener mOnItemClickListener;

    public PinAdapter(OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PinViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pin_item, viewGroup, false);
        return new PinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PinViewHolder pinViewHolder, int i) {
        PinInfo pinInfo = mPins.get(pinViewHolder.getAdapterPosition());
        pinViewHolder.placeNameTv.setText(pinInfo.getPlaceName() == null ? "Unknown" : pinInfo.getPlaceName());
        pinViewHolder.latitudeTv.setText(String.valueOf(pinInfo.getLatitude()));
        pinViewHolder.longitudeTv.setText(String.valueOf(pinInfo.getLongitude()));
    }

    @Override
    public int getItemCount() {
        if (mPins == null) {
            return 0;
        }
        return mPins.size();
    }

    public void setPins(List<PinInfo> pins) {
        mPins = pins;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(PinInfo pinInfo);
    }

    class PinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView placeNameTv;
        private TextView latitudeTv;
        private TextView longitudeTv;

        PinViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTv = itemView.findViewById(R.id.place_name_tv);
            latitudeTv = itemView.findViewById(R.id.latitude_tv);
            longitudeTv = itemView.findViewById(R.id.longitude_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(mPins.get(getAdapterPosition()));
        }
    }
}
