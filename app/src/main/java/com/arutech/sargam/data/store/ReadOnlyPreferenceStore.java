package com.arutech.sargam.data.store;

import android.media.audiofx.Equalizer;

import com.arutech.sargam.data.annotations.StartPage;

public interface ReadOnlyPreferenceStore {

    boolean showFirstStart();
    boolean allowLogging();
    boolean useMobileNetwork();

    boolean openNowPlayingOnNewQueue();
    boolean enableNowPlayingGestures();
    @StartPage
    int getDefaultPage();
    boolean isShuffled();
    int getRepeatMode();

    long getLastSleepTimerDuration();

    int getEqualizerPresetId();
    boolean getEqualizerEnabled();
    Equalizer.Settings getEqualizerSettings();



}
