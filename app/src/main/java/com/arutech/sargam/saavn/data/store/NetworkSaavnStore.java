package com.arutech.sargam.saavn.data.store;

import com.arutech.sargam.saavn.api.SaavnService;
import com.arutech.sargam.saavn.api.model.ArtistBio;
import com.arutech.sargam.saavn.api.model.ArtistSearch;

import java.io.IOException;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import timber.log.Timber;

public class NetworkSaavnStore implements SaavnStore {

	private SaavnService mService;


	public NetworkSaavnStore(SaavnService service) {
		mService = service;

	}

	@Override
	public Observable<ArtistBio> getArtistInfo(String artistName) {
		artistName = artistName.split(",")[0];
		Timber.i("Searching for artist %s", artistName);
		Observable<ArtistBio> result = mService.getArtistSearchResult(artistName)
				.flatMap(new Func1<ArtistSearch, Observable<ArtistBio>>() {
					@Override
					public Observable<ArtistBio> call(ArtistSearch artistSearch) {
						if (artistSearch.getTotal() > 0) {
							String id = artistSearch.getResults().get(0).getId();
							Timber.i("Searching for artist id %s", id);
							return mService.getArtistBio(id);
						} else {
							String message = "Call to getArtistInfo failed";
							throw Exceptions.propagate(new IOException(message));
						}
					}
				});

		return result;
	}
}
