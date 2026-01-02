package com.example.fuelwiselog.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fuelwiselog.util.Prefs;
import com.example.fuelwiselog.data.Vehicle;
import com.example.fuelwiselog.databinding.ActivityAddRecordBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddRecordActivity extends AppCompatActivity {

    private ActivityAddRecordBinding binding;
    private FuelViewModel viewModel;

    private final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");

    private List<Vehicle> vehicles = new ArrayList<>();
    private long selectedVehicleId = -1;
    private long lastMileage = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FuelViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Date default = today
        Calendar cal = Calendar.getInstance();
        binding.etDate.setText(iso.format(cal.getTime()));

        binding.etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    AddRecordActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar picked = Calendar.getInstance();
                        picked.set(year, month, dayOfMonth);
                        binding.etDate.setText(iso.format(picked.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        viewModel.getVehicles().observe(this, list -> {
            vehicles = list;

            if (vehicles.isEmpty()) {
                Toast.makeText(this, "No vehicles. Add a vehicle first.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            setupVehicleDropdown();

            long prefId = Prefs.getSelectedVehicleId(this);
            if (prefId > 0) {
                setSelectedVehicle(prefId);
            } else {
                setSelectedVehicle(vehicles.get(0).getId());
            }
        });

        binding.btnSave.setOnClickListener(v -> saveRecord());
    }

    private void setupVehicleDropdown() {
        List<String> labels = new ArrayList<>();
        for (Vehicle v : vehicles) labels.add(v.getName() + " (" + v.getType() + ")");

        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        binding.actVehicle.setAdapter(a);

        binding.actVehicle.setOnItemClickListener((parent, view, position, id) -> {
            Vehicle v = vehicles.get(position);
            setSelectedVehicle(v.getId());
        });
    }

    private void setSelectedVehicle(long vehicleId) {
        selectedVehicleId = vehicleId;

        Vehicle v = null;
        for (Vehicle vv : vehicles) if (vv.getId() == vehicleId) { v = vv; break; }

        if (v != null) {
            binding.layoutVehiclePreview.setVisibility(android.view.View.VISIBLE);
            binding.tvPreviewName.setText(v.getName());
            binding.tvPreviewType.setText(v.getType());
            try {
                binding.cardPreviewColor.setCardBackgroundColor(Color.parseColor(v.getColorHex()));
            } catch (Exception ignored) {}

            // update dropdown text to match
            binding.actVehicle.setText(v.getName() + " (" + v.getType() + ")", false);
        }

        // load last mileage for this vehicle
        viewModel.getLastMileage(vehicleId).observe(this, last -> {
            if (last == null) {
                lastMileage = -1;
                binding.tvLastMileage.setText("Last recorded: — km");
            } else {
                lastMileage = last;
                binding.tvLastMileage.setText("Last recorded: " + lastMileage + " km");
            }
        });
    }

    private void saveRecord() {
        clearErrors();

        String dateIso = binding.etDate.getText() == null ? "" : binding.etDate.getText().toString().trim();
        Double liters = parseDouble(binding.etLiters.getText());
        Double cost = parseDouble(binding.etCost.getText());
        Long mileage = parseLong(binding.etMileage.getText());

        boolean ok = true;

        if (selectedVehicleId <= 0) {
            binding.tilVehicle.setError("Select a vehicle");
            ok = false;
        }
        if (dateIso.isEmpty()) {
            binding.tilDate.setError("Pick a date");
            ok = false;
        }
        if (liters == null || liters <= 0) {
            binding.tilLiters.setError("Invalid liters");
            ok = false;
        }
        if (cost == null || cost <= 0) {
            binding.tilCost.setError("Invalid cost");
            ok = false;
        }
        if (mileage == null || mileage <= 0) {
            binding.tilMileage.setError("Invalid mileage");
            ok = false;
        } else if (lastMileage > 0 && mileage <= lastMileage) {
            binding.tilMileage.setError("Mileage must be greater than " + lastMileage);
            ok = false;
        }

        if (!ok) return;

        // Save selected vehicle for Home / Fuel Log
        Prefs.setSelectedVehicleId(this, selectedVehicleId);

        // Insert
        viewModel.insertFuelRecord(selectedVehicleId, dateIso, liters, cost, mileage);

        Toast.makeText(this, "Record added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearErrors() {
        binding.tilVehicle.setError(null);
        binding.tilDate.setError(null);
        binding.tilLiters.setError(null);
        binding.tilCost.setError(null);
        binding.tilMileage.setError(null);
    }

    private Double parseDouble(CharSequence cs) {
        try {
            if (cs == null) return null;
            String s = cs.toString().trim();
            if (s.isEmpty()) return null;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(CharSequence cs) {
        try {
            if (cs == null) return null;
            String s = cs.toString().trim();
            if (s.isEmpty()) return null;
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}
