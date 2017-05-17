package com.arutech.sargam.data.store;

import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;

public interface ThemeStore {

    @ColorInt int getPrimaryColor();
    @ColorInt int getAccentColor();

    void setTheme(AppCompatActivity activity);

    Bitmap getLargeAppIcon();
    void createThemedLauncherIcon();

}
