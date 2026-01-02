package com.example.fuelwiselog.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FuelRepository {

    private final VehicleDao vehicleDao;
    private final FuelRecordDao fuelRecordDao;

    public FuelRepository(Application app) {
        FuelDatabase db = FuelDatabase.getInstance(app);
        vehicleDao = db.vehicleDao();
        fuelRecordDao = db.fuelRecordDao();
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return vehicleDao.getAll();
    }

    public LiveData<Vehicle> getVehicleById(long id) {
        return vehicleDao.getById(id);
    }

    public LiveData<List<FuelRecordWithVehicle>> getAllRecordsWithVehicle() {
        return fuelRecordDao.getAllWithVehicle();
    }

    public LiveData<List<FuelRecord>> getRecordsByVehicleMileageAsc(long vehicleId) {
        return fuelRecordDao.getByVehicleMileageAsc(vehicleId);
    }

    public void insertVehicle(Vehicle v) {
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.insert(v));
    }

    public void updateVehicle(Vehicle v) {
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.update(v));
    }

    public void deleteVehicle(Vehicle v) {
        FuelDatabase.DB_EXECUTOR.execute(() -> vehicleDao.delete(v));
    }

    public void insertRecord(FuelRecord r) {
        FuelDatabase.DB_EXECUTOR.execute(() -> fuelRecordDao.insert(r));
    }

    public void deleteRecord(FuelRecord r) {
        FuelDatabase.DB_EXECUTOR.execute(() -> fuelRecordDao.delete(r));
    }

    public void getLastMileage(long vehicleId, MileageCallback cb) {
        FuelDatabase.DB_EXECUTOR.execute(() -> {
            Double last = fuelRecordDao.getLastMileageBlocking(vehicleId);
            if (last == null) last = 0.0;
            cb.onResult(last);
        });
    }

    public LiveData<List<FuelRecord>> getAllRecordsOrderByVehicleMileageAsc() {
        return fuelRecordDao.getAllOrderByVehicleAndMileageAsc();
    }

    public void deleteRecordById(long id) {
        executor.execute(() -> fuelRecordDao.deleteById(id));
    }

    public interface MileageCallback {
        void onResult(double lastMileage);
    }
}
