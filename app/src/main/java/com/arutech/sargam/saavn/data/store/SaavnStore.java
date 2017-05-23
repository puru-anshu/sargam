package com.arutech.sargam.saavn.data.store;

import com.arutech.sargam.saavn.api.model.ArtistBio;

import io.reactivex.Observable;


public interface SaavnStore {

    Observable<ArtistBio> getArtistInfo(String artistName);

}
