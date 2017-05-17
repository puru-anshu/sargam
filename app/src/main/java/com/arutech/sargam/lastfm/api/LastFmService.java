package com.arutech.sargam.lastfm.api;

import com.arutech.sargam.lastfm.api.model.LfmArtistResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface LastFmService {

    String CONSTANT_ARGS = "&api_key=" + LastFmApi.API_KEY + "&format=json";

    @GET("?method=artist.getinfo" + CONSTANT_ARGS)
    Observable<Response<LfmArtistResponse>> getArtistInfo(@Query("artist") String artistName);

}
