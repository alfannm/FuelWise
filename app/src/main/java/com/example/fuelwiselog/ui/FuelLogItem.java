package com.example.fuelwiselog.ui;

public class FuelLogItem {
    // Identity fields used for diffing and filtering.
    public long recordId;
    public long vehicleId;
    public String vehicleName;
    public String vehicleColorHex;
    public String vehicleType;

    // Record details displayed in the list row.
    public String dateIso;
    public double liters;
    public double costRm;
    public double mileageKm;

    // Efficiency metrics when a previous record is available.
    public boolean hasEfficiency;
    public double distanceKm;
    public double rmPerKm;
    public double litersPer100Km;
}
