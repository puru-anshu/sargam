package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.data.store.DemoMusicStore;
import com.arutech.sargam.data.store.DemoPlaylistStore;
import com.arutech.sargam.data.store.LocalPlayCountStore;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.data.store.PlayCountStore;
import com.arutech.sargam.data.store.PlaylistStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DemoModule {

    @Provides
    @Singleton
    public MusicStore provideMusicStore(Context context) {
        return new DemoMusicStore(context);
    }

    @Provides
    @Singleton
    public PlaylistStore providePlaylistStore(Context context) {
        return new DemoPlaylistStore(context);
    }

    @Provides
    @Singleton
    public PlayCountStore providePlayCountStore(Context context) {
        return new LocalPlayCountStore(context);
    }

}
