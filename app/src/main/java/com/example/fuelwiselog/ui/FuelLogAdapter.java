package com.example.fuelwiselog.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuelwiselog.databinding.ItemFuelRecordBinding;

import java.text.DecimalFormat;

public class FuelLogAdapter extends ListAdapter<FuelLogItem, FuelLogAdapter.VH> {

    public interface Actions {
        void onDelete(long recordId);
    }

    private final Actions actions;
    private final DecimalFormat df2 = new DecimalFormat("0.00");

    public FuelLogAdapter(Actions actions) {
        super(DIFF);
        this.actions = actions;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFuelRecordBinding b = ItemFuelRecordBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    class VH extends RecyclerView.ViewHolder {
        private final ItemFuelRecordBinding b;

        VH(ItemFuelRecordBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(FuelLogItem item) {
            b.tvVehicleName.setText(item.vehicleName == null ? "Vehicle" : item.vehicleName);
            b.tvDate.setText(item.dateIso == null ? "" : item.dateIso);

            try {
                b.cardColor.setCardBackgroundColor(Color.parseColor(item.vehicleColorHex));
            } catch (Exception ignored) {}

            b.tvVolume.setText(df2.format(item.liters) + "L");
            b.tvCost.setText("RM" + df2.format(item.costRm));
            b.tvMileage.setText(item.mileageKm + "km");

            b.btnDelete.setOnClickListener(v -> actions.onDelete(item.recordId));

            if (item.hasEfficiency) {
                b.layoutEfficiency.setVisibility(android.view.View.VISIBLE);
                b.tvNoEfficiency.setVisibility(android.view.View.GONE);

                b.tvSince.setText("Since last fill-up (" + item.distanceKm + " km)");
                b.tvRmPerKm.setText("RM " + df2.format(item.rmPerKm));
                b.tvLPer100.setText(df2.format(item.litersPer100Km) + " L");
            } else {
                b.layoutEfficiency.setVisibility(android.view.View.GONE);
                b.tvNoEfficiency.setVisibility(android.view.View.VISIBLE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<FuelLogItem> DIFF = new DiffUtil.ItemCallback<FuelLogItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FuelLogItem oldItem, @NonNull FuelLogItem newItem) {
            return oldItem.recordId == newItem.recordId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FuelLogItem o, @NonNull FuelLogItem n) {
            return o.vehicleId == n.vehicleId
                    && safeEq(o.vehicleName, n.vehicleName)
                    && safeEq(o.vehicleColorHex, n.vehicleColorHex)
                    && safeEq(o.dateIso, n.dateIso)
                    && o.liters == n.liters
                    && o.costRm == n.costRm
                    && o.mileageKm == n.mileageKm
                    && o.hasEfficiency == n.hasEfficiency
                    && o.distanceKm == n.distanceKm
                    && o.rmPerKm == n.rmPerKm
                    && o.litersPer100Km == n.litersPer100Km;
        }

        private boolean safeEq(String a, String b) {
            if (a == null && b == null) return true;
            if (a == null) return false;
            return a.equals(b);
        }
    };
}
