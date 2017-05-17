package com.arutech.sargam.lastfm.data.inject;

import android.content.Context;

import com.arutech.sargam.lastfm.data.store.DemoLastFmStore;
import com.arutech.sargam.lastfm.data.store.LastFmStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DemoLastFmModule {

    @Provides
    @Singleton
    public LastFmStore provideLastFmStore(Context context) {
        return new DemoLastFmStore(context);
    }

}
