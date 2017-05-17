package com.arutech.sargam.data.inject;


import com.arutech.sargam.player.MockPlayerController;
import com.arutech.sargam.player.PlayerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestPlayerModule {

    @Provides
    @Singleton
    public PlayerController providePlayerController() {
        return new MockPlayerController();
    }

}
