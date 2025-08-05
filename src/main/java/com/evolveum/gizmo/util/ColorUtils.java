package com.evolveum.gizmo.util;

import java.util.List;
import java.util.Random;

public class ColorUtils {

    private static final List<String> COLOR_PALETTE = List.of(
            "#FF5733", "#33C1FF", "#33FF57", "#FFC300", "#9B59B6", "#E67E22", "#2ECC71", "#1ABC9C", "#3498DB", "#E74C3C"
    );

    private static final Random RANDOM = new Random();

    public static String generateRandomColorRGB() {
        int r = RANDOM.nextInt(256);
        int g = RANDOM.nextInt(256);
        int b = RANDOM.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static String getRandomFromPalette() {
        return COLOR_PALETTE.get(RANDOM.nextInt(COLOR_PALETTE.size()));
    }

    public static List<String> getColorPalette() {
        return COLOR_PALETTE;
    }
}