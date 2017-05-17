package com.arutech.sargam.lastfm.data.inject;

import android.content.Context;

import com.arutech.sargam.lastfm.api.LastFmApi;
import com.arutech.sargam.lastfm.api.LastFmService;
import com.arutech.sargam.lastfm.data.store.LastFmStore;
import com.arutech.sargam.lastfm.data.store.NetworkLastFmStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LastFmModule {

    @Provides
    @Singleton
    public LastFmService provideLastFmService(Context context) {
        return LastFmApi.getService(context);
    }

    @Provides
    @Singleton
    public LastFmStore provideLastFmStore(LastFmService service) {
        return new NetworkLastFmStore(service);
    }

}
