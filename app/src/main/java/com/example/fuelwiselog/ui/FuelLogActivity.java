package com.example.fuelwiselog.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fuelwiselog.data.FuelRecord;
import com.example.fuelwiselog.util.Prefs;
import com.example.fuelwiselog.data.Vehicle;
import com.example.fuelwiselog.databinding.ActivityFuelLogBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuelLogActivity extends AppCompatActivity {

    private ActivityFuelLogBinding binding;
    private FuelViewModel viewModel;

    private FuelLogAdapter adapter;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<FuelRecord> recordsMileageOrdered = new ArrayList<>();

    private long filterVehicleId = -1; // -1 = All vehicles
    private final DecimalFormat df2 = new DecimalFormat("0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFuelLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FuelViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new FuelLogAdapter(recordId -> {
            new AlertDialog.Builder(FuelLogActivity.this)
                    .setTitle("Delete record?")
                    .setMessage("Delete this fuel record?")
                    .setPositiveButton("Delete", (d, which) -> viewModel.deleteFuelRecordById(recordId))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.rvRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecords.setAdapter(adapter);

        viewModel.getVehicles().observe(this, list -> {
            vehicles = list;
            setupFilterDropdown();
            recomputeUi();
        });

        viewModel.getAllRecordsOrderByVehicleMileageAsc().observe(this, list -> {
            recordsMileageOrdered = list;
            recomputeUi();
        });
    }

    private void setupFilterDropdown() {
        List<String> labels = new ArrayList<>();
        labels.add("All Vehicles");
        for (Vehicle v : vehicles) labels.add(v.getName() + " (" + v.getType() + ")");

        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        binding.actFilter.setAdapter(a);

        // default: All
        binding.actFilter.setText("All Vehicles", false);
        filterVehicleId = -1;

        binding.actFilter.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                filterVehicleId = -1;
            } else {
                Vehicle v = vehicles.get(position - 1);
                filterVehicleId = v.getId();
            }
            recomputeUi();
        });
    }

    private void recomputeUi() {
        if (vehicles == null || recordsMileageOrdered == null) return;

        Map<Long, Vehicle> vehicleMap = new HashMap<>();
        for (Vehicle v : vehicles) vehicleMap.put(v.getId(), v);

        // Compute efficiency per record using mileage-ascending list
        Map<Long, FuelLogItem> itemByRecordId = new HashMap<>();

        FuelRecord prev = null;
        long prevVehicleId = -1;

        for (FuelRecord r : recordsMileageOrdered) {
            FuelLogItem item = new FuelLogItem();
            item.recordId = r.getId();
            item.vehicleId = r.getVehicleId();
            item.dateIso = r.getDateIso();
            item.liters = r.getVolumeLiters();
            item.costRm = r.getCostRm();
            item.mileageKm = r.getMileageKm();

            Vehicle v = vehicleMap.get(r.getVehicleId());
            if (v != null) {
                item.vehicleName = v.getName();
                item.vehicleColorHex = v.getColorHex();
            } else {
                item.vehicleName = "Vehicle";
                item.vehicleColorHex = "#B4A7D6";
            }

            item.hasEfficiency = false;

            if (prev != null && prevVehicleId == r.getVehicleId()) {
                double distance = r.getMileageKm() - prev.getMileageKm();
                if (distance > 0) {
                    item.hasEfficiency = true;
                    item.distanceKm = distance;
                    item.rmPerKm = r.getCostRm() / distance;
                    item.litersPer100Km = (r.getVolumeLiters() / distance) * 100.0;
                }
            }

            itemByRecordId.put(item.recordId, item);

            prev = r;
            prevVehicleId = r.getVehicleId();
        }

        // Build display list (we sort by date desc; ISO date sorts well)
        List<FuelLogItem> display = new ArrayList<>(itemByRecordId.values());
        display.sort((a, b) -> {
            String da = a.dateIso == null ? "" : a.dateIso;
            String db = b.dateIso == null ? "" : b.dateIso;
            return db.compareTo(da); // desc
        });

        // Apply filter
        if (filterVehicleId > 0) {
            List<FuelLogItem> filtered = new ArrayList<>();
            for (FuelLogItem i : display) if (i.vehicleId == filterVehicleId) filtered.add(i);
            display = filtered;
        }

        adapter.submitList(display);

        binding.tvCount.setText(display.size() + " record" + (display.size() == 1 ? "" : "s"));
        binding.layoutEmpty.setVisibility(display.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);

        // Average efficiency card like Figma:
        // - If filter = All, use selected vehicle
        long avgVehicleId = (filterVehicleId == -1) ? Prefs.getSelectedVehicleId(this) : filterVehicleId;
        updateAverageCard(avgVehicleId);
    }

    private void updateAverageCard(long vehicleId) {
        if (vehicleId <= 0) {
            binding.cardAverage.setVisibility(android.view.View.GONE);
            return;
        }

        // compute averages from mileage-ordered list for that vehicle
        List<FuelRecord> list = new ArrayList<>();
        for (FuelRecord r : recordsMileageOrdered) if (r.getVehicleId() == vehicleId) list.add(r);

        if (list.size() < 2) {
            binding.cardAverage.setVisibility(android.view.View.GONE);
            return;
        }

        double sumRmPerKm = 0;
        double sumLPer100 = 0;
        int count = 0;

        for (int i = 1; i < list.size(); i++) {
            FuelRecord cur = list.get(i);
            FuelRecord prev = list.get(i - 1);

            double dist = cur.getMileageKm() - prev.getMileageKm();
            if (dist <= 0) continue;

            double rmPerKm = cur.getCostRm() / dist;
            double lPer100 = (cur.getVolumeLiters() / dist) * 100.0;

            sumRmPerKm += rmPerKm;
            sumLPer100 += lPer100;
            count++;
        }

        if (count == 0) {
            binding.cardAverage.setVisibility(android.view.View.GONE);
            return;
        }

        binding.cardAverage.setVisibility(android.view.View.VISIBLE);
        binding.tvAvgRm.setText("RM " + df2.format(sumRmPerKm / count));
        binding.tvAvgL.setText(df2.format(sumLPer100 / count) + " L");
    }
}
