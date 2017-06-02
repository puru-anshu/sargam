package com.arutech.sargam.saavn.data.store;

import com.arutech.sargam.model.Artist;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.saavn.api.model.ArtistBio;
import com.arutech.sargam.saavn.api.model.Autocomplete;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.api.model.TrackGroupDetail;

import java.util.List;

import io.reactivex.Observable;


public interface SaavnStore {

	Observable<ArtistBio> getArtistInfo(String artistName);

	Observable<Boolean> refresh();

	Observable<Boolean> isLoading();

	Observable<List<Song>> getSongs();

	Observable<List<TrackGroup>> getAlbums();


	Observable<List<TrackGroup>> getPlaylist();

	Observable<List<Song>> getSongs(Artist artist);

	Observable<List<Song>> getSongs(TrackGroup album);

	Observable<List<TrackGroup>> getAlbums(Artist artist);

	Observable<Artist> findArtistById(long artistId);

	Observable<TrackGroupDetail> findAlbumById(long albumId);

	Observable<Artist> findArtistByName(String artistName);

	Observable<TrackGroupDetail> findPlaylistById(String playlistId);

	Observable<List<Song>> searchForSongs(String query);

	Observable<List<Artist>> searchForArtists(String query);

	Observable<List<TrackGroup>> searchForAlbums(String query);

	Observable<List<TrackGroup>> searchForPlaylist(String query);

	Observable<TrackGroupDetail> getTrackGroup(String groupId, String type);

	Observable<Autocomplete> findSearchSuggestion(String queryString);
 }
