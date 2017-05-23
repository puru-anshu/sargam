package com.arutech.sargam.lastfm.data.store;

import android.support.v4.util.SimpleArrayMap;

import com.arutech.sargam.lastfm.api.LastFmService;
import com.arutech.sargam.lastfm.model.LfmArtist;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.exceptions.Exceptions;


public class NetworkLastFmStore implements LastFmStore {

    private LastFmService mService;
    private SimpleArrayMap<String, Observable<LfmArtist>> mCachedArtistInfo;

    public NetworkLastFmStore(LastFmService service) {
        mService = service;
        mCachedArtistInfo = new SimpleArrayMap<>();
    }

    @Override
    public Observable<LfmArtist> getArtistInfo(String artistName) {
        Observable<LfmArtist> result = mCachedArtistInfo.get(artistName);
        if (result == null) {
            result = mService.getArtistInfo(artistName)
                    .map(response -> {
                        if (!response.isSuccessful()) {
                            String message = "Call to getArtistInfo failed with response code "
                                    + response.code()
                                    + "\n" + response.message();

                            throw Exceptions.propagate(new IOException(message));
                        }

                        return response.body().getArtist();
                    })
                    .cache();

            mCachedArtistInfo.put(artistName, result);
        }

        return result;
    }
}
