package com.example.fuelwiselog.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles")
public class Vehicle {

    @PrimaryKey(autoGenerate = true)
    private long id;

    // Display name shown throughout the UI.
    @NonNull
    private String name;

    // Vehicle type used for emoji and filters.
    @NonNull
    private String type;

    // Hex color used for UI badges.
    @NonNull
    private String colorHex; // e.g. "#B4A7D6"

    // Optional license plate.
    @Nullable
    private String plateNumber;

    public Vehicle(@NonNull String name, @NonNull String type, @NonNull String colorHex, @Nullable String plateNumber) {
        this.name = name;
        this.type = type;
        this.colorHex = colorHex;
        this.plateNumber = plateNumber;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    @NonNull public String getColorHex() { return colorHex; }
    public void setColorHex(@NonNull String colorHex) { this.colorHex = colorHex; }

    @Nullable public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(@Nullable String plateNumber) { this.plateNumber = plateNumber; }
}
