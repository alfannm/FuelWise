package com.example.fuelwiselog.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuelwiselog.data.FuelRecord;
import com.example.fuelwiselog.databinding.ItemFuelRecordBinding;

import java.text.DecimalFormat;

/**
 * Legacy adapter (safe to keep even if unused). Updated to match the current FuelRecord fields:
 * dateIso, mileageKm, volumeLiters, costRm.
 */
public class FuelRecordAdapter extends ListAdapter<FuelRecordDisplay, FuelRecordAdapter.RecordVH> {

    public interface OnRecordLongClickListener {
        void onLongClick(FuelRecord record);
    }

    private OnRecordLongClickListener longClickListener;

    public void setOnRecordLongClickListener(OnRecordLongClickListener listener) {
        this.longClickListener = listener;
    }

    private static final DiffUtil.ItemCallback<FuelRecordDisplay> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull FuelRecordDisplay oldItem, @NonNull FuelRecordDisplay newItem) {
                    return oldItem.record.getId() == newItem.record.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull FuelRecordDisplay oldItem, @NonNull FuelRecordDisplay newItem) {
                    FuelRecord o = oldItem.record;
                    FuelRecord n = newItem.record;

                    return safeEq(o.getDateIso(), n.getDateIso())
                            && Double.compare(o.getMileageKm(), n.getMileageKm()) == 0
                            && Double.compare(o.getVolumeLiters(), n.getVolumeLiters()) == 0
                            && Double.compare(o.getCostRm(), n.getCostRm()) == 0;
                }

                private boolean safeEq(String a, String b) {
                    if (a == null && b == null) return true;
                    if (a == null) return false;
                    return a.equals(b);
                }
            };

    private final DecimalFormat df2 = new DecimalFormat("0.00");
    private final DecimalFormat df0 = new DecimalFormat("0");

    public FuelRecordAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecordVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFuelRecordBinding binding = ItemFuelRecordBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new RecordVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordVH holder, int position) {
        FuelRecordDisplay item = getItem(position);
        holder.bind(item);
    }

    class RecordVH extends RecyclerView.ViewHolder {

        private final ItemFuelRecordBinding b;

        RecordVH(ItemFuelRecordBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(FuelRecordDisplay item) {
            FuelRecord r = item.record;

            b.tvDate.setText(r.getDateIso() == null ? "" : r.getDateIso());
            b.tvMileage.setText(df0.format(r.getMileageKm()) + "km");
            b.tvVolume.setText(df2.format(r.getVolumeLiters()) + "L");
            b.tvCost.setText("RM" + df2.format(r.getCostRm()));

            if (item.distanceKm == null) {
                b.layoutEfficiency.setVisibility(View.GONE);
                b.tvNoEfficiency.setVisibility(View.VISIBLE);
            } else {
                b.layoutEfficiency.setVisibility(View.VISIBLE);
                b.tvNoEfficiency.setVisibility(View.GONE);
                b.tvSince.setText("Since last fill-up (" + df0.format(item.distanceKm) + " km)");
                b.tvLPer100.setText(df2.format(item.litersPer100Km) + " L");
                b.tvRmPerKm.setText("RM " + df2.format(item.rmPerKm));
            }

            View root = b.getRoot();
            root.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onLongClick(r);
                    return true;
                }
                return false;
            });
        }
    }
}
