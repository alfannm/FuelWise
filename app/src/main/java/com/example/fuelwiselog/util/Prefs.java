package com.example.fuelwiselog.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class Prefs {

    private Prefs() {}

    private static final String FILE = "fuelwise_prefs";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_SELECTED_VEHICLE_ID = "selected_vehicle_id";

    public static void applySavedNightMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(getNightMode(context));
    }

    public static int getNightMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return sp.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setNightMode(Context context, int mode) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_NIGHT_MODE, mode)
                .apply();

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static long getSelectedVehicleId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return sp.getLong(KEY_SELECTED_VEHICLE_ID, -1L);
    }

    public static void setSelectedVehicleId(Context context, long vehicleId) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_SELECTED_VEHICLE_ID, vehicleId)
                .apply();
    }
}
