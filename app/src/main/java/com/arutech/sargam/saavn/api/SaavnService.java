package com.arutech.sargam.saavn.api;

import com.arutech.sargam.saavn.api.model.AlbumSearch;
import com.arutech.sargam.saavn.api.model.ArtistBio;
import com.arutech.sargam.saavn.api.model.ArtistSearch;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.api.model.TrackGroupDetail;
import com.arutech.sargam.saavn.api.model.TrackSearch;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface SaavnService {

	@Headers({
			"Content-Type: application/json;charset=utf-8",
			"Accept: application/json"
	})
	@GET("/api.php?__call=reco.getreco&p=1&n=30&ctx=android&_format=json&_marker=0")
	Observable<LinkedTreeMap> getRecommendation(@Query("pid") String songId);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=search.getArtistResults")
	Observable<AlbumSearch> getAlbumSearchResult(@Query("q") String searchString);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=search.getArtistResults")
	Observable<ArtistSearch> getArtistSearchResult(@Query("q") String searchString);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=search.getResults")
	Observable<TrackSearch> geSongSearchResult(@Query("q") String searchString);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=content.getAlbumDetails")
	Observable<TrackGroupDetail> getAlbumDetail(@Query("albumid") String albumId);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=playlist.getDetails")
	Observable<TrackGroupDetail> getPlaylistDetail(@Query("listid") String albumId);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=content.getFeaturedPlaylists")
	Observable<List<TrackGroup>> getFeaturedPlaylists();


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=content.getAlbums")
	Observable<List<TrackGroup>> getFeaturedAlbums();

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=content.getCharts")
	Observable<List<TrackGroup>> getShows();


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&ctx=android&api_version=4&manufacturer=Google&network_operator=Reliance&q=kishore&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFF" +
			"&__call=artist.getArtistPageDetails")
	Observable<ArtistBio> getArtistBio(@Query("artistId") String artistId);
}
