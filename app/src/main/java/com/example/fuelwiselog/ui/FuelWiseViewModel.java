package com.example.fuelwiselog.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fuelwiselog.data.*;

import java.util.List;

public class FuelWiseViewModel extends AndroidViewModel {

    private final FuelRepository repo;

    public FuelWiseViewModel(@NonNull Application application) {
        super(application);
        repo = new FuelRepository(application);
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return repo.getVehicles();
    }

    public LiveData<Vehicle> getVehicleById(long id) {
        return repo.getVehicleById(id);
    }

    public LiveData<List<FuelRecordWithVehicle>> getAllRecordsWithVehicle() {
        return repo.getAllRecordsWithVehicle();
    }

    public LiveData<List<FuelRecord>> getRecordsByVehicleMileageAsc(long vehicleId) {
        return repo.getRecordsByVehicleMileageAsc(vehicleId);
    }

    public void insertVehicle(String name, String type, String colorHex, String plate) {
        repo.insertVehicle(new Vehicle(name, type, colorHex, plate == null || plate.trim().isEmpty() ? null : plate.trim()));
    }

    public void updateVehicle(Vehicle v) {
        repo.updateVehicle(v);
    }

    public void deleteVehicle(Vehicle v) {
        repo.deleteVehicle(v);
    }

    public void insertFuelRecord(long vehicleId, String dateIso, double liters, double costRm, double mileageKm) {
        repo.insertRecord(new FuelRecord(vehicleId, dateIso, liters, costRm, mileageKm));
    }

    public void deleteFuelRecord(FuelRecord r) {
        repo.deleteRecord(r);
    }

    public void getLastMileage(long vehicleId, FuelRepository.MileageCallback cb) {
        repo.getLastMileage(vehicleId, cb);
    }
}
