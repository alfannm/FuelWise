package com.example.fuelwiselog.ui;

import java.util.Locale;

final class VehicleEmojiMapper {

    private VehicleEmojiMapper() {}

    static String getEmoji(String type) {
        if (type == null) {
            return "🛞";
        }

        String t = type.trim().toLowerCase(Locale.ROOT);
        if (t.contains("motor")) {
            return "🏍️";
        }
        if (t.contains("lorry") || t.contains("truck")) {
            return "🚛";
        }
        if (t.contains("van")) {
            return "🚐";
        }
        if (t.contains("car")) {
            return "🚗";
        }
        if (t.contains("other")) {
            return "🛞";
        }

        return "🛞";
    }
}
