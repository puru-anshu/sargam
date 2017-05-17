package com.arutech.sargam.data.store;

import android.media.audiofx.Equalizer;

import com.arutech.sargam.data.annotations.AccentTheme;
import com.arutech.sargam.data.annotations.BaseTheme;
import com.arutech.sargam.data.annotations.PrimaryTheme;
import com.arutech.sargam.data.annotations.StartPage;

import java.util.Set;

public interface ReadOnlyPreferenceStore {

    boolean showFirstStart();
    boolean allowLogging();
    boolean useMobileNetwork();

    boolean openNowPlayingOnNewQueue();
    boolean enableNowPlayingGestures();
    @StartPage
    int getDefaultPage();
    @PrimaryTheme
    int getPrimaryColor();
    @AccentTheme
    int getAccentColor();
    @BaseTheme
    int getBaseColor();
    @PrimaryTheme int getIconColor();

    boolean isShuffled();
    int getRepeatMode();

    long getLastSleepTimerDuration();

    int getEqualizerPresetId();
    boolean getEqualizerEnabled();
    Equalizer.Settings getEqualizerSettings();

    Set<String> getIncludedDirectories();
    Set<String> getExcludedDirectories();

}
