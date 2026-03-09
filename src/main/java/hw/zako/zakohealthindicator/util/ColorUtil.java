package hw.zako.zakohealthindicator.util;

import net.minecraft.util.text.TextFormatting;

public class ColorUtil {
    // pct = hp / maxHp (0.0 .. 1.0)
    public static TextFormatting getColor(float pct) {
        if (pct > 0.75f) return TextFormatting.GREEN;
        if (pct > 0.50f) return TextFormatting.YELLOW;
        if (pct > 0.25f) return TextFormatting.GOLD;
        return TextFormatting.RED;
    }

    /**
     * Animated HSV color: oscillates around HP-based hue.
     * Full HP → green area, low HP → red area, faster + wider oscillation.
     * Returns packed 0xRRGGBB.
     */
    public static int animatedColor(float pct) {
        long time = System.currentTimeMillis();
        float baseHue  = pct * 120f;
        float amplitude = (1f - pct) * 25f + 8f;
        float speed     = (1f - pct) * 3f  + 1f;
        float hue = baseHue + (float)(amplitude * Math.sin(time * 0.001 * speed));
        return hsvToRgb(hue / 360f, 0.95f, 0.95f);
    }

    public static int hsvToRgb(float h, float s, float v) {
        h = h - (float) Math.floor(h);
        int i = (int)(h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        float r, g, b;
        switch (i % 6) {
            case 0:  r = v; g = t; b = p; break;
            case 1:  r = q; g = v; b = p; break;
            case 2:  r = p; g = v; b = t; break;
            case 3:  r = p; g = q; b = v; break;
            case 4:  r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    /**
     * Smooth RGB gradient: green (full) → yellow → orange → red (empty).
     * Returns a packed 0xRRGGBB int for use with Color.fromRgb().
     */
    public static int getRgbColor(float pct) {
        // clamp
        pct = Math.max(0f, Math.min(1f, pct));

        int r, g, b = 50;
        if (pct > 0.5f) {
            // green (#40E040) → yellow (#FFE040)
            float t = (pct - 0.5f) * 2f;   // 1 = full green, 0 = yellow
            r = (int)(255 * (1f - t) + 64  * t);   // 255 → 64
            g = (int)(224 * (1f - t) + 224 * t);   // stays ~224
        } else {
            // yellow (#FFE040) → red (#FF3232)
            float t = pct * 2f;             // 1 = yellow, 0 = red
            r = 255;
            g = (int)(50  * (1f - t) + 224 * t);   // 50 → 224
        }
        return (r << 16) | (g << 8) | b;
    }
}
