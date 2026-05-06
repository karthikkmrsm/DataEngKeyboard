package com.dataeng.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class ThemeManager {

    public static final String PREFS       = "dataeng_prefs";
    public static final String KEY_THEME   = "theme";
    public static final String KEY_SIZE    = "key_size";
    public static final String KEY_DEFMODE = "default_mode";

    public static final int THEME_DARK   = 0;
    public static final int THEME_LIGHT  = 1;
    public static final int THEME_AMOLED = 2;
    public static final int THEME_OCEAN  = 3;
    public static final int THEME_FOREST = 4;
    public static final int THEME_SUNSET = 5;
    public static final int THEME_PURPLE = 6;

    public static final String[] THEME_NAMES = {
        "Dark","Light","AMOLED","Ocean","Forest","Sunset","Purple"
    };

    public static final int SIZE_SMALL  = 0;
    public static final int SIZE_NORMAL = 1;
    public static final int SIZE_LARGE  = 2;

    public static final String MODE_ABC   = "abc";
    public static final String MODE_SQL   = "sql";
    public static final String MODE_SPARK = "spark";
    public static final String MODE_DB    = "db";

    // Palettes: bg, surface1, surface2, keyBg, keyPress,
    //           textPrimary, textSecondary, accent,
    //           sqlColor, sparkColor, dbColor, border
    private static final int[][] PALETTES = {
      {0xFF0D0F16,0xFF161925,0xFF1E2235,0xFF1C2030,0xFF2C3352,
       0xFFDDE2F0,0xFF7A8499,0xFF4F9CF9,0xFF2DD4BF,0xFFA78BFA,0xFFFB923C,0x14FFFFFF},
      {0xFFF0F2F8,0xFFFFFFFF,0xFFE8EBF5,0xFFDDE2F0,0xFFCDD2E8,
       0xFF1A1D2E,0xFF5A6080,0xFF2563EB,0xFF0D9488,0xFF7C3AED,0xFFEA580C,0x18000000},
      {0xFF000000,0xFF080808,0xFF101010,0xFF161616,0xFF222222,
       0xFFE8EAF0,0xFF606878,0xFF4F9CF9,0xFF2DD4BF,0xFFA78BFA,0xFFFB923C,0x1EFFFFFF},
      {0xFF040E1F,0xFF071628,0xFF0D2040,0xFF102550,0xFF1A3A6E,
       0xFFCDE8FF,0xFF5A8AAA,0xFF38BDF8,0xFF06B6D4,0xFF818CF8,0xFFF97316,0x18FFFFFF},
      {0xFF071410,0xFF0E1F18,0xFF152B22,0xFF1A3428,0xFF224A38,
       0xFFD4EDE6,0xFF5A8070,0xFF4ADE80,0xFF34D399,0xFFA3E635,0xFFFBBF24,0x18FFFFFF},
      {0xFF180A04,0xFF251208,0xFF341A0C,0xFF3D1E10,0xFF5C2E18,
       0xFFFFEDE0,0xFFAA7060,0xFFFB923C,0xFFF87171,0xFFFBBF24,0xFFA78BFA,0x18FFFFFF},
      {0xFF0F0818,0xFF180D28,0xFF231240,0xFF2A1650,0xFF3D2070,
       0xFFEDE8FF,0xFF8878AA,0xFFB78BFA,0xFFA78BFA,0xFF818CF8,0xFFF472B6,0x18FFFFFF},
    };

    public static int[] getPalette(int theme) {
        if (theme < 0 || theme >= PALETTES.length) return PALETTES[0];
        return PALETTES[theme];
    }

    public static int bg(int t)          { return getPalette(t)[0]; }
    public static int surface1(int t)    { return getPalette(t)[1]; }
    public static int surface2(int t)    { return getPalette(t)[2]; }
    public static int keyBg(int t)       { return getPalette(t)[3]; }
    public static int keyPress(int t)    { return getPalette(t)[4]; }
    public static int textPrimary(int t) { return getPalette(t)[5]; }
    public static int textSec(int t)     { return getPalette(t)[6]; }
    public static int accent(int t)      { return getPalette(t)[7]; }
    public static int sql(int t)         { return getPalette(t)[8]; }
    public static int spark(int t)       { return getPalette(t)[9]; }
    public static int db(int t)          { return getPalette(t)[10]; }
    public static int border(int t)      { return getPalette(t)[11]; }

    public static int keyHeightDp(int size) {
        return size == SIZE_SMALL ? 38 : size == SIZE_LARGE ? 54 : 46;
    }
    public static int keyTextSp(int size) {
        return size == SIZE_SMALL ? 12 : size == SIZE_LARGE ? 18 : 15;
    }
    public static int kwHeightDp(int size) {
        return size == SIZE_SMALL ? 34 : size == SIZE_LARGE ? 46 : 40;
    }
    public static int numHeightDp(int size) {
        return size == SIZE_SMALL ? 30 : size == SIZE_LARGE ? 40 : 34;
    }

    public static GradientDrawable roundRect(int color, int radiusDp, Context ctx) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(radiusDp, ctx));
        return gd;
    }

    public static GradientDrawable roundRectStroke(int fill, int stroke,
                                                    int radiusDp, int strokeW, Context ctx) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fill);
        gd.setCornerRadius(dp(radiusDp, ctx));
        gd.setStroke((int) dp(strokeW, ctx), stroke);
        return gd;
    }

    public static int tint(int color, float alpha) {
        return Color.argb((int)(alpha * 255),
               Color.red(color), Color.green(color), Color.blue(color));
    }

    private static float dp(int v, Context ctx) {
        return v * ctx.getResources().getDisplayMetrics().density;
    }

    public static int loadTheme(Context c) {
        return c.getSharedPreferences(PREFS, 0).getInt(KEY_THEME, THEME_DARK);
    }
    public static void saveTheme(Context c, int t) {
        c.getSharedPreferences(PREFS, 0).edit().putInt(KEY_THEME, t).apply();
    }
    public static int loadSize(Context c) {
        return c.getSharedPreferences(PREFS, 0).getInt(KEY_SIZE, SIZE_NORMAL);
    }
    public static void saveSize(Context c, int s) {
        c.getSharedPreferences(PREFS, 0).edit().putInt(KEY_SIZE, s).apply();
    }
    public static String loadDefaultMode(Context c) {
        return c.getSharedPreferences(PREFS, 0).getString(KEY_DEFMODE, MODE_ABC);
    }
    public static void saveDefaultMode(Context c, String m) {
        c.getSharedPreferences(PREFS, 0).edit().putString(KEY_DEFMODE, m).apply();
    }
}
