package com.arutech.sargam.saavn.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class SaavnApi {

    protected static final String BASE_URL = "http://www.saavn.com";

    /**
     * The directory name to place cache files from OkHttp. This directory will be placed in the
     * app's internal cache directory (or external if this is a debug build)
     */
    private static final String CACHE_DIR = "saavn";

    /**
     * The overridden cache duration to keep data from GET requests. By default, Last.fm's API
     * returns 1 day, but its API policy requires that items be cached for a week.
     */
    private static final long CACHE_DURATION_SEC = 7 * 24 * 60 * 60;

    /**
     * The maximum size of the cache. This is currently set at 10 MiB
     */
    private static final int CACHE_SIZE = 10 * 1024 * 1024;

    /**
     * This class is never instantiated
     */
    private SaavnApi() {

    }

    public static SaavnService getService(Context context) {
	    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

	    Gson gson = new GsonBuilder().setLenient()
			    .serializeNulls().disableHtmlEscaping()
			    .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        return retrofit.create(SaavnService.class);
    }


}
