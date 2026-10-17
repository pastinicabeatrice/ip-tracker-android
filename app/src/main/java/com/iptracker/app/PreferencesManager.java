package com.iptracker.app;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFS_NAME = "user_preferences";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String KEY_VISUAL_STYLE = "visual_style";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_LAST_MODIFIED = "last_modified";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setDarkTheme(boolean isDarkTheme) {
        editor.putBoolean(KEY_DARK_THEME, isDarkTheme);
        editor.apply();
    }

    public boolean isDarkTheme() {
        return preferences.getBoolean(KEY_DARK_THEME, false);
    }

    public void setLanguage(String language) {
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "Română");
    }

    public void setVisualStyle(int style) {
        editor.putInt(KEY_VISUAL_STYLE, style);
        editor.apply();
    }

    public int getVisualStyle() {
        return preferences.getInt(KEY_VISUAL_STYLE, 0);
    }

    public void setTextSize(float size) {
        editor.putFloat(KEY_TEXT_SIZE, size);
        editor.apply();
    }

    public float getTextSize() {
        return preferences.getFloat(KEY_TEXT_SIZE, 1.0f);
    }

    public void setLastModified(long timestamp) {
        editor.putLong(KEY_LAST_MODIFIED, timestamp);
        editor.apply();
    }

    public long getLastModified() {
        return preferences.getLong(KEY_LAST_MODIFIED, System.currentTimeMillis());
    }

    public void clearAllPreferences() {
        editor.clear();
        editor.apply();
    }
}