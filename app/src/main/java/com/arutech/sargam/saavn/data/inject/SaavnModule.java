package com.arutech.sargam.saavn.data.inject;

import android.content.Context;

import com.arutech.sargam.saavn.api.SaavnApi;
import com.arutech.sargam.saavn.api.SaavnService;
import com.arutech.sargam.saavn.data.store.NetworkSaavnStore;
import com.arutech.sargam.saavn.data.store.SaavnStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SaavnModule {

	@Provides
	@Singleton
	public SaavnService provideSaavnService(Context context) {
		return SaavnApi.getService(context);
	}

	@Provides
	@Singleton
	public SaavnStore provideSaavnStore(SaavnService service) {
		return new NetworkSaavnStore(service);
	}

}
