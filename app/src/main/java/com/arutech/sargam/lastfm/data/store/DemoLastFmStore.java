package com.arutech.sargam.lastfm.data.store;

import android.content.Context;

import com.arutech.sargam.lastfm.model.LfmArtist;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;

public class DemoLastFmStore implements LastFmStore {

    private Context mContext;

    public DemoLastFmStore(Context context) {
        mContext = context;
    }

    @Override
    public Observable<LfmArtist> getArtistInfo(String artistName) {
        return Observable.fromCallable(() -> {
            InputStream stream = null;
            InputStreamReader reader = null;

            try {
                File json = new File(mContext.getExternalCacheDir(), "lastfm/" + artistName);
                stream = new FileInputStream(json);
                reader = new InputStreamReader(stream);

                return new Gson().fromJson(reader, LfmArtist.class);
            } finally {
                if (stream != null) stream.close();
                if (reader != null) reader.close();
            }
        });
    }

}
