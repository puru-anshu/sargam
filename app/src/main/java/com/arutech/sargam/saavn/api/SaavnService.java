package com.arutech.sargam.saavn.api;

import com.arutech.sargam.saavn.api.model.ArtistBio;
import com.arutech.sargam.saavn.api.model.ArtistSearch;
import com.arutech.sargam.saavn.api.model.Autocomplete;
import com.arutech.sargam.saavn.api.model.SearchGroup;
import com.arutech.sargam.saavn.api.model.Track;
import com.arutech.sargam.saavn.api.model.TrackGroup;
import com.arutech.sargam.saavn.api.model.TrackGroupDetail;
import com.arutech.sargam.saavn.api.model.TrackSearch;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface  SaavnService {

	@Headers({
			"Content-Type: application/json;charset=utf-8",
			"Accept: application/json"
	})
	@GET("/api.php?__call=reco.getreco&p=1&n=30&_format=json&_marker=0")
	Observable<LinkedTreeMap> getRecommendation(@Query("pid") String songId);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=search.getAlbumResults")
	Observable<SearchGroup> getAlbumSearchResult(@Query("q") String searchString);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=search.getPlaylistResults")
	Observable<SearchGroup> getPlaylistSearchResult(@Query("q") String searchString);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=search.getArtistResults")
	Observable<ArtistSearch> getArtistSearchResult(@Query("q") String searchString);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=search.getResults")
	Observable<TrackSearch> geSongSearchResult(@Query("q") String searchString);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=content.getAlbumDetails")
	Observable<TrackGroupDetail> getAlbumDetail(@Query("albumid") String albumId);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=playlist.getDetails")
	Observable<TrackGroupDetail> getPlaylistDetail(@Query("listid") String albumId);


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=content.getFeaturedPlaylists")
	Observable<List<TrackGroup>> getFeaturedPlaylists();


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=content.getAlbums")
	Observable<List<TrackGroup>> getFeaturedAlbums();

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0" +
			"&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=content.getCharts")
	Observable<List<TrackGroup>> getShows();


	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=artist.getArtistPageDetails")
	Observable<ArtistBio> getArtistBio(@Query("artistId") String artistId);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=autocomplete.get")
	Observable<Autocomplete> autocomplete(@Query("query") String query);

	@GET("/api.php?cc=in&session_device_id=ORy8Mq5D.1494219534123&app_version=5.6&_marker=0&api_version=4&manufacturer=Google&network_operator=Reliance&readable_version=5.6&build=2&v=61" +
			"&_format=json&model=Pixel&network_subtype=&state=logout&network_type=WIFI" +
			"&__call=song.getDetails")
	Observable<Map<String, Track>> getSongDetail(@Query("pids") String songId);
}
