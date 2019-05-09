package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.data.store.SharedPreferenceStore;

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


}
