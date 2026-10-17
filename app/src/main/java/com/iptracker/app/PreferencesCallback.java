package com.iptracker.app;

public interface PreferencesCallback {
    void onThemeChanged(boolean isDarkTheme);
    void onLanguageChanged(String language);
    void onVisualStyleChanged(int style);
    void onTextSizeChanged(float size);
}