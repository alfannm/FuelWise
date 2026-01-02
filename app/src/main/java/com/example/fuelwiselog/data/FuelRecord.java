package com.example.fuelwiselog.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "fuel_records",
        foreignKeys = @ForeignKey(
                entity = Vehicle.class,
                parentColumns = "id",
                childColumns = "vehicleId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index("vehicleId"),
                @Index(value = {"vehicleId", "mileageKm"})
        }
)
public class FuelRecord {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long vehicleId;

    // ISO date "yyyy-MM-dd" so ordering works lexicographically
    private String dateIso;

    private double volumeLiters;
    private double costRm;
    private double mileageKm;

    public FuelRecord(long vehicleId, String dateIso, double volumeLiters, double costRm, double mileageKm) {
        this.vehicleId = vehicleId;
        this.dateIso = dateIso;
        this.volumeLiters = volumeLiters;
        this.costRm = costRm;
        this.mileageKm = mileageKm;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }

    public String getDateIso() { return dateIso; }
    public void setDateIso(String dateIso) { this.dateIso = dateIso; }

    public double getVolumeLiters() { return volumeLiters; }
    public void setVolumeLiters(double volumeLiters) { this.volumeLiters = volumeLiters; }

    public double getCostRm() { return costRm; }
    public void setCostRm(double costRm) { this.costRm = costRm; }

    public double getMileageKm() { return mileageKm; }
    public void setMileageKm(double mileageKm) { this.mileageKm = mileageKm; }
}
