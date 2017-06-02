package com.arutech.sargam.saavn.data.store;

import com.arutech.sargam.model.Artist;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.saavn.api.SaavnService;
import com.arutech.sargam.saavn.api.model.ArtistBio;
import com.arutech.sargam.saavn.api.model.ArtistSearch;
import com.arutech.sargam.saavn.api.model.Autocomplete;
import com.arutech.sargam.saavn.api.model.SearchGroup;
import com.arutech.sargam.saavn.api.model.Track;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.api.model.TrackGroupDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

import static com.arutech.sargam.utils.Util.getSongsFromTracks;

public class NetworkSaavnStore implements SaavnStore {

	private SaavnService mService;
	private BehaviorSubject<Boolean> mSongLoadingSubject;
	private BehaviorSubject<Boolean> mAlbumLoadingSubject;
	private BehaviorSubject<Boolean> mPlaylistLoadingSubject;
	private BehaviorSubject<Boolean> mArtistLoadingSubject;

	private BehaviorSubject<List<Song>> mSongs;
	private BehaviorSubject<List<TrackGroup>> mPlaylist;
	private BehaviorSubject<List<TrackGroup>> mAlbums;


	public NetworkSaavnStore(SaavnService service) {
		mService = service;
		mSongLoadingSubject = BehaviorSubject.createDefault(false);
		mAlbumLoadingSubject = BehaviorSubject.createDefault(false);
		mPlaylistLoadingSubject = BehaviorSubject.createDefault(false);
		mArtistLoadingSubject = BehaviorSubject.createDefault(false);

	}

	@Override
	public Observable<ArtistBio> getArtistInfo(String artistName) {
		artistName = artistName.split(",")[0];
		Timber.i("Searching for artist %s", artistName);
		Observable<ArtistBio> result = mService
				.getArtistSearchResult(artistName).flatMap(new Function<ArtistSearch, Observable<ArtistBio>>() {
					@Override
					public Observable<ArtistBio> apply(@NonNull ArtistSearch artistSearch) throws Exception {
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

	@Override
	public Observable<Boolean> refresh() {
		mSongLoadingSubject.onNext(true);
		mAlbumLoadingSubject.onNext(true);
		mPlaylistLoadingSubject.onNext(true);
		if (mSongs != null)
			getAllSongs().subscribe(songs -> {
				mSongs.onNext(songs);
				mSongLoadingSubject.onNext(false);
			}, e -> mSongLoadingSubject.onNext(false));
		if (mPlaylist != null)
			getAllPlaylist().subscribe(playlists -> {
				mPlaylist.onNext(playlists);
				mPlaylistLoadingSubject.onNext(false);
			}, e -> {
				mPlaylistLoadingSubject.onNext(false);
			});
		if (mAlbums != null)
			getAllAlbums().subscribe(alba -> {
				mAlbums.onNext(alba);
				mAlbumLoadingSubject.onNext(false);
			}, e -> {
				mAlbumLoadingSubject.onNext(false);
			});


		return Observable.just(true);
	}


	@Override
	public Observable<Boolean> isLoading() {

		return Observable.combineLatest(mSongLoadingSubject,
				mAlbumLoadingSubject,
				mPlaylistLoadingSubject, mArtistLoadingSubject,
				(songState, albumState, playlistState, artistState) -> {
					return songState || albumState || playlistState || artistState;
				});
	}

	@Override
	public Observable<List<Song>> getSongs() {
		if (mSongs == null) {
			mSongLoadingSubject.onNext(true);
			mSongs = BehaviorSubject.createDefault(Collections.emptyList());
			getAllSongs().observeOn(Schedulers.io())
					.subscribe(songs -> {
								mSongs.onNext(songs);
								mSongLoadingSubject.onNext(false);
							}
							, ex -> {
								Timber.e(ex, "Error in loading songs");
								mSongLoadingSubject.onNext(false);
							});

			mSongLoadingSubject.onNext(false);
		}

		return mSongs.subscribeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<List<TrackGroup>> getAlbums() {
		if (mAlbums == null) {
			mAlbums = BehaviorSubject.createDefault(Collections.emptyList());
			mAlbumLoadingSubject.onNext(true);
			getAllAlbums()
					.observeOn(Schedulers.io()).subscribe(
					albums -> {
						mAlbums.onNext(albums);
						mAlbumLoadingSubject.onNext(false);
					}, e -> {
						Timber.e(e, "Error in loading Albums");
						mAlbumLoadingSubject.onNext(false);
					});
		}

		return mAlbums.subscribeOn(AndroidSchedulers.mainThread());
	}


	@Override
	public Observable<List<TrackGroup>> getPlaylist() {
		if (mPlaylist == null) {
			mPlaylist = BehaviorSubject.createDefault(Collections.emptyList());
			mPlaylistLoadingSubject.onNext(true);
			getAllPlaylist()
					.observeOn(Schedulers.io()).subscribe(
					playlists -> {
						mPlaylist.onNext(playlists);
						mPlaylistLoadingSubject.onNext(false);
					}, e -> {
						Timber.e(e, "Error in loading Playlist");
						mPlaylistLoadingSubject.onNext(false);
					});
		}

		return mPlaylist.subscribeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<List<Song>> getSongs(Artist artist) {
		return null;
	}

	@Override
	public Observable<List<Song>> getSongs(TrackGroup album) {
		Observable<TrackGroupDetail> trackObservable = album.getType().equals("album") ?
				mService.getAlbumDetail(album.getId()) : mService.getPlaylistDetail(album.getId());
		return trackObservable.map(trackGroupDetail -> getSongsFromDetail(trackGroupDetail));

	}


	@Override
	public Observable<List<TrackGroup>> getAlbums(Artist artist) {
		return null;
	}

	@Override
	public Observable<Artist> findArtistById(long artistId) {
		return null;
	}

	@Override
	public Observable<TrackGroupDetail> findAlbumById(long albumId) {
		return mService.getAlbumDetail(String.valueOf(albumId));
	}

	@Override
	public Observable<Artist> findArtistByName(String artistName) {
		return null;
	}

	@Override
	public Observable<TrackGroupDetail> findPlaylistById(String playlistId) {
		return mService.getPlaylistDetail(playlistId);
	}

	@Override
	public Observable<List<Song>> searchForSongs(String query) {
		return mService.geSongSearchResult(query)
				.map(trackSearch -> {
					if (null != trackSearch) {
						List<Track> results = trackSearch.getResults();
						return getSongsFromTracks(results);
					}
					return Collections.emptyList();
				});

	}

	@Override
	public Observable<List<Artist>> searchForArtists(String query) {
		return null;
	}

	@Override
	public Observable<List<TrackGroup>> searchForAlbums(String query) {
		return mService.getAlbumSearchResult(query).map(searchGroup -> {
			Timber.i("Total album found " + searchGroup.getTotal());
			return searchGroup.getResults();
		});
	}

	@Override
	public Observable<List<TrackGroup>> searchForPlaylist(String query) {
		return mService.getPlaylistSearchResult(query).map(SearchGroup::getResults);
	}

	@Override
	public Observable<TrackGroupDetail> getTrackGroup(String groupId, String type) {
		if ("album".equalsIgnoreCase(type))
			return mService.getAlbumDetail(groupId);
		else if ("song".equalsIgnoreCase(type)) {
			return mService.getSongDetail(groupId).flatMap(new Function<Map<String, Track>, ObservableSource<TrackGroupDetail>>() {
				@Override
				public ObservableSource<TrackGroupDetail> apply(@NonNull Map<String, Track> stringSongMap) throws Exception {
					Track track = stringSongMap.get(groupId);
					List<Track> tracks = new ArrayList<Track>(1);
					tracks.add(track);
					TrackGroupDetail detail = new TrackGroupDetail();
					detail.setTitle(track.getMoreInfo().getAlbum());
					detail.setType(type);
					detail.setId(track.getMoreInfo().getAlbumId());
					detail.setList(tracks);
					return Observable.just(detail);
				}
			});

		} else
			return mService.getPlaylistDetail(groupId);

	}

	@Override
	public Observable<Autocomplete> findSearchSuggestion(String queryString) {
		return mService.autocomplete(queryString);
	}


	private Observable<List<TrackGroup>> getAllAlbums() {
		return mService.getFeaturedAlbums().observeOn(Schedulers.io());


	}

	private Observable<List<Song>> getAllSongs() {
		return mService.getPlaylistDetail("49").map(trackGroupDetail -> {
			List<Song> songList = getSongsFromDetail(trackGroupDetail);
			return songList;
		}).observeOn(Schedulers.io());
	}

	@android.support.annotation.NonNull
	private List<Song> getSongsFromDetail(TrackGroupDetail trackGroupDetail) {
		List<Track> trackList = trackGroupDetail.getList();
		return getSongsFromTracks(trackList);

	}

	private Observable<List<TrackGroup>> getAllPlaylist() {
		return mService.getFeaturedPlaylists();

	}


}
