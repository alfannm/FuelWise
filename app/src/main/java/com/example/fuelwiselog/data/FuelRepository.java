package com.example.fuelwiselog.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Repository for Vehicles + Fuel Records.
 * All writes run on {@link FuelDatabase#DB_EXECUTOR}.
 */
public class FuelRepository {

    private final VehicleDao vehicleDao;
    private final FuelRecordDao fuelRecordDao;

    public FuelRepository(Application app) {
        // Repository owns DAO references and write thread.
        FuelDatabase db = FuelDatabase.getInstance(app);
        vehicleDao = db.vehicleDao();
        fuelRecordDao = db.fuelRecordDao();
    }

    // ---------------- Vehicles ----------------
    public LiveData<List<Vehicle>> getVehicles() {
        return vehicleDao.getAll();
    }

    public void insertVehicle(Vehicle v) {
        // Writes are executed on the DB executor.
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.insert(v));
    }

    public void updateVehicle(Vehicle v) {
        // Writes are executed on the DB executor.
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.update(v));
    }

    public void deleteVehicle(Vehicle v) {
        // Writes are executed on the DB executor.
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.delete(v));
    }

    // ---------------- Fuel Records ----------------
    public LiveData<List<FuelRecord>> getRecordsByVehicleMileageAsc(long vehicleId) {
        // LiveData stream for a vehicle's records ordered by mileage.
        return fuelRecordDao.getByVehicleMileageAsc(vehicleId);
    }

    public LiveData<Double> getLastMileage(long vehicleId) {
        // Latest mileage for validation in AddRecord.
        return fuelRecordDao.getLastMileage(vehicleId);
    }

    public LiveData<List<FuelRecord>> getAllRecordsOrderByVehicleMileageAsc() {
        // All records ordered by vehicle + mileage for efficiency math.
        return fuelRecordDao.getAllOrderByVehicleAndMileageAsc();
    }

    /** Alias used by FuelViewModel */
    public void insert(FuelRecord r) {
        FuelDatabase.DB_EXECUTOR.execute(() -> fuelRecordDao.insert(r));
    }

    /** Alias used by FuelViewModel */
    public void delete(FuelRecord r) {
        FuelDatabase.DB_EXECUTOR.execute(() -> fuelRecordDao.delete(r));
    }

    public void deleteFuelRecordById(long id) {
        // Used by log screen for quick deletion.
        FuelDatabase.DB_EXECUTOR.execute(() -> fuelRecordDao.deleteById(id));
    }
}
