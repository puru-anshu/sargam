package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.data.store.PresetThemeStore;
import com.arutech.sargam.data.store.SharedPreferenceStore;
import com.arutech.sargam.data.store.ThemeStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    public PreferenceStore providePreferencesStore(Context context) {
        return new SharedPreferenceStore(context);
    }

    @Provides
    @Singleton
    public ThemeStore provideThemeStore(Context context, PreferenceStore preferenceStore) {
        return new PresetThemeStore(context, preferenceStore);
    }

}
