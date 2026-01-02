package com.example.fuelwiselog.data;

import androidx.room.Embedded;
import androidx.room.Relation;

public class FuelRecordWithVehicle {
    @Embedded
    public FuelRecord record;

    @Relation(parentColumn = "vehicleId", entityColumn = "id")
    public Vehicle vehicle;
}
