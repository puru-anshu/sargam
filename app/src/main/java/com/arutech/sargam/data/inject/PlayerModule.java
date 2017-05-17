package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.player.ServicePlayerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlayerModule {

    @Provides
    @Singleton
    public PlayerController providePlayerController(Context context, PreferenceStore prefs) {
        return new ServicePlayerController(context, prefs);
    }

}
