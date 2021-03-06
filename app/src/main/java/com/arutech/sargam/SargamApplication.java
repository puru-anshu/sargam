package com.arutech.sargam;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.activeandroid.ActiveAndroid;
import com.arutech.sargam.data.inject.SargamComponentFactory;
import com.arutech.sargam.data.inject.SargamGraph;
import com.arutech.sargam.utils.CrashlyticsTree;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class SargamApplication extends Application {

	private SargamGraph mComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		ActiveAndroid.initialize(this);

		setupCrashlytics();
		setupTimber();
//		setupStrictMode();

		mComponent = createDaggerComponent();

	}

	@NonNull
	protected SargamGraph createDaggerComponent() {
		return SargamComponentFactory.create(this);
	}

	private void setupStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads()
					.detectDiskWrites()
					.detectAll()
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects()
					.penaltyLog()
					.build());
		}
	}

	private void setupCrashlytics() {
		Fabric.with(this, new Crashlytics());
	}

	private void setupTimber() {
		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		} else {
			Timber.plant(new CrashlyticsTree());
		}
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
//		Glide.with(this).onTrimMemory(level);
	}

	public static SargamGraph getComponent(Fragment fragment) {
		return getComponent(fragment.getContext());
	}

	public static SargamGraph getComponent(Context context) {
		return ((SargamApplication) context.getApplicationContext()).mComponent;
	}
}
