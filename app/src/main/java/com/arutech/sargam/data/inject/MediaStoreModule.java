package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.data.store.LocalMusicStore;
import com.arutech.sargam.data.store.LocalPlayCountStore;
import com.arutech.sargam.data.store.LocalPlaylistStore;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.data.store.PlayCountStore;
import com.arutech.sargam.data.store.PlaylistStore;
import com.arutech.sargam.data.store.PreferenceStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MediaStoreModule {

    @Provides
    @Singleton
    public MusicStore provideMusicStore(Context context, PreferenceStore preferenceStore) {
        return new LocalMusicStore(context, preferenceStore);
    }

    @Provides
    @Singleton
    public PlaylistStore providePlaylistStore(Context context, MusicStore musicStore,
                                              PlayCountStore playCountStore) {
        return new LocalPlaylistStore(context, musicStore, playCountStore);
    }

    @Provides
    @Singleton
    public PlayCountStore providePlayCountStore(Context context) {
        return new LocalPlayCountStore(context);
    }
}
