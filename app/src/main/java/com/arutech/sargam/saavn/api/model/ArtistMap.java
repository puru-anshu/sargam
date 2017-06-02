
package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class ArtistMap {

	@SerializedName("primary_artists")
	@Expose
	private List<Artist> primaryArtists = null;
	@SerializedName("featured_artists")
	@Expose
	private List<Artist> featuredArtists = null;
	@SerializedName("artists")
	@Expose
	private List<Artist> artists = null;

	public List<Artist> getPrimaryArtists() {
		return primaryArtists;
	}

	public void setPrimaryArtists(List<Artist> primaryArtists) {
		this.primaryArtists = primaryArtists;
	}

	public List<Artist> getFeaturedArtists() {
		return featuredArtists;
	}

	public void setFeaturedArtists(List<Artist> featuredArtists) {
		this.featuredArtists = featuredArtists;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	@Override
	public String toString() {
		if (null != primaryArtists && !primaryArtists.isEmpty()) {
			String[] artists = new String[primaryArtists.size()];
			for (int i = 0; i < primaryArtists.size(); i++) {
				artists[i] = primaryArtists.get(i).getName();
			}
			return Arrays.toString(artists);
		} else {
			return "";
		}
	}
}
