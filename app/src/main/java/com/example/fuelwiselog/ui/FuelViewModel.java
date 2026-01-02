package com.example.fuelwiselog.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.fuelwiselog.data.FuelRecord;
import com.example.fuelwiselog.data.FuelRepository;
import com.example.fuelwiselog.data.Vehicle;

import java.util.List;

public class FuelViewModel extends AndroidViewModel {

    private final FuelRepository repository;

    // Existing (your current pipeline)
    private final LiveData<List<FuelRecordDisplay>> displayRecords;
    private final LiveData<FuelSummary> summary;

    // New (needed for Vehicles + Fuel Log)
    private final LiveData<List<Vehicle>> vehicles;
    private final LiveData<List<FuelRecord>> allRecordsOrderByVehicleMileageAsc;

    public FuelViewModel(@NonNull Application application) {
        super(application);
        repository = new FuelRepository(application);

        // Existing (keep)
        displayRecords = Transformations.map(
                repository.getAllRecords(), // your repo already has this
                records -> FuelRecordDisplay.build(records, true)
        );

        summary = Transformations.map(
                repository.getAllRecords(),
                FuelSummary::from
        );

        // New (must exist in repository)
        vehicles = repository.getVehicles();
        allRecordsOrderByVehicleMileageAsc = repository.getAllRecordsOrderByVehicleMileageAsc();
    }

    // -----------------------------
    // Existing getters (keep)
    // -----------------------------
    public LiveData<List<FuelRecordDisplay>> getDisplayRecords() {
        return displayRecords;
    }

    public LiveData<FuelSummary> getSummary() {
        return summary;
    }

    // -----------------------------
    // Vehicles
    // -----------------------------
    public LiveData<List<Vehicle>> getVehicles() {
        return vehicles;
    }

    public void insertVehicle(Vehicle v) {
        repository.insertVehicle(v);
    }

    public void updateVehicle(Vehicle v) {
        repository.updateVehicle(v);
    }

    public void deleteVehicle(Vehicle v) {
        repository.deleteVehicle(v);
    }

    // -----------------------------
    // Records (new correct insert)
    // -----------------------------
    public void insertFuelRecord(long vehicleId, String dateIso, double volumeLiters, double costRm, double mileageKm) {
        FuelRecord record = new FuelRecord(vehicleId, dateIso, volumeLiters, costRm, mileageKm);
        repository.insert(record);
    }

    public void delete(FuelRecord record) {
        repository.delete(record);
    }

    public void deleteFuelRecordById(long id) {
        repository.deleteFuelRecordById(id);
    }

    public LiveData<List<FuelRecord>> getAllRecordsOrderByVehicleMileageAsc() {
        return allRecordsOrderByVehicleMileageAsc;
    }

    // For AddRecord validation (per vehicle)
    public LiveData<Double> getLastMileage(long vehicleId) {
        return repository.getLastMileage(vehicleId);
    }
}
