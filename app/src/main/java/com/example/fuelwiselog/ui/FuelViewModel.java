package com.example.fuelwiselog.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.fuelwiselog.data.FuelRecord;
import com.example.fuelwiselog.data.FuelRepository;
import com.example.fuelwiselog.data.Vehicle;

import java.util.List;

public class FuelViewModel extends AndroidViewModel {

    private final FuelRepository repository;

    // Vehicles
    private final LiveData<List<Vehicle>> vehicles;

    // Fuel Log (needs mileage-ascending list across all vehicles)
    private final LiveData<List<FuelRecord>> allRecordsOrderByVehicleMileageAsc;

    public FuelViewModel(@NonNull Application application) {
        super(application);
        // Repository owns all data operations.
        repository = new FuelRepository(application);

        // Cache LiveData streams used by UI screens.
        vehicles = repository.getVehicles();
        allRecordsOrderByVehicleMileageAsc = repository.getAllRecordsOrderByVehicleMileageAsc();
    }

    // ---------------- Vehicles ----------------

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
        // Create a record object for persistence.
        FuelRecord record = new FuelRecord(vehicleId, dateIso, volumeLiters, costRm, mileageKm);
        repository.insert(record);
    }

    public void delete(FuelRecord record) {
        repository.delete(record);
    }

    public void deleteFuelRecordById(long id) {
        repository.deleteFuelRecordById(id);
    }

    // For Main/Home summary
    public LiveData<List<FuelRecord>> getRecordsByVehicleMileageAsc(long vehicleId) {
        // Used by dashboard summaries and efficiency calculations.
        return repository.getRecordsByVehicleMileageAsc(vehicleId);
    }

    public LiveData<List<FuelRecord>> getAllRecordsOrderByVehicleMileageAsc() {
        return allRecordsOrderByVehicleMileageAsc;
    }

    // For AddRecord validation (per vehicle)
    public LiveData<Double> getLastMileage(long vehicleId) {
        // Used for AddRecord validation.
        return repository.getLastMileage(vehicleId);
    }
}
