package com.arutech.sargam.saavn.api.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anshuman on 31/05/17.
 */

public class Autocomplete {
	@SerializedName("albums")
	@Expose
	private DataGroup albums;
	@SerializedName("songs")
	@Expose
	private DataGroup songs;
	@SerializedName("playlists")
	@Expose
	private DataGroup playlists;
	@SerializedName("artists")
	@Expose
	private DataGroup artists;

	public Autocomplete() {
	}

	public DataGroup getAlbums() {
		return albums;
	}

	public void setAlbums(DataGroup albums) {
		this.albums = albums;
	}

	public DataGroup getSongs() {
		return songs;
	}

	public void setSongs(DataGroup songs) {
		this.songs = songs;
	}

	public DataGroup getPlaylists() {
		return playlists;
	}

	public void setPlaylists(DataGroup playlists) {
		this.playlists = playlists;
	}

	public DataGroup getArtists() {
		return artists;
	}

	public void setArtists(DataGroup artists) {
		this.artists = artists;
	}

	public List<DataGroup> getAll() {
		List<DataGroup> toR = new ArrayList<>();
		if (null != playlists)
			toR.add(playlists);
		if (null != songs)
			toR.add(songs);
		if (null != playlists)
			toR.add(songs);
		Collections.sort(toR);

		return toR;

	}


	public class Data {
		@SerializedName("id")
		String id;
		@SerializedName("title")
		String title;
		@SerializedName("image")
		String image;
		@SerializedName("music")
		String music;
		@SerializedName("album")
		String album;
		@SerializedName("type")
		String type;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getMusic() {
			return music;
		}

		public void setMusic(String music) {
			this.music = music;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public class DataGroup implements Comparable<DataGroup> {
		@SerializedName("data")
		List<Data> data;
		@SerializedName("position")
		int position;

		public List<Data> getData() {
			return data;
		}

		public void setData(List<Data> data) {
			this.data = data;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}


		@Override
		public int compareTo(@NonNull DataGroup dataGroup) {
			return Integer.valueOf(position).compareTo(dataGroup.position);
		}
	}
}