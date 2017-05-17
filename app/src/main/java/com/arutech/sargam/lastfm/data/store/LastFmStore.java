package com.arutech.sargam.lastfm.data.store;

import com.arutech.sargam.lastfm.model.LfmArtist;

import rx.Observable;

public interface LastFmStore {

    Observable<LfmArtist> getArtistInfo(String artistName);

}
