package com.example.fuelwiselog.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Vehicle.class, FuelRecord.class}, version = 2, exportSchema = false)
public abstract class FuelDatabase extends RoomDatabase {

    public abstract VehicleDao vehicleDao();
    public abstract FuelRecordDao fuelRecordDao();

    public static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor();

    private static volatile FuelDatabase INSTANCE;

    public static FuelDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FuelDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FuelDatabase.class, "fuelwise_db")
                            // for student project/dev: easiest when schema changes
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
