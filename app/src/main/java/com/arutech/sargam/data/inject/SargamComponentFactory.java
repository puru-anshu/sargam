package com.arutech.sargam.data.inject;

import android.content.Context;

import com.arutech.sargam.saavn.data.inject.SaavnModule;

public class SargamComponentFactory {

    private SargamComponentFactory() {
        throw new RuntimeException("This class is not instantiable");
    }

    public static SargamGraph create(Context context) {
        return DaggerSargamComponent.builder()
                .contextModule(new ContextModule(context))
		        .mediaStoreModule(new MediaStoreModule())
		        .playerModule(new PlayerModule())
		        .saavnModule(new SaavnModule())
                .build();
    }

}