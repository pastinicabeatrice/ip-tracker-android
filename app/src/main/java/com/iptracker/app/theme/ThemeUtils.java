package com.iptracker.app.theme;

import android.graphics.Color;

public class ThemeUtils {
    public static class Colors {
        public static final int Purple80 = Color.parseColor("#FFD0BCFF");
        public static final int PurpleGrey80 = Color.parseColor("#FFCCC2DC");
        public static final int Pink80 = Color.parseColor("#FFEFB8C8");

        public static final int Purple40 = Color.parseColor("#FF6650a4");
        public static final int PurpleGrey40 = Color.parseColor("#FF625b71");
        public static final int Pink40 = Color.parseColor("#FF7D5260");
    }

    public static class TextSize {
        public static final float BODY_LARGE = 16f;
        public static final float LINE_HEIGHT = 24f;
        public static final float LETTER_SPACING = 0.5f;
    }

    public static boolean isDarkTheme(android.content.Context context) {
        android.content.res.Configuration config = context.getResources().getConfiguration();
        return (config.uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
}