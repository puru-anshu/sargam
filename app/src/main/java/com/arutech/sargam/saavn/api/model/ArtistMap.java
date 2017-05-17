
package com.arutech.sargam.saavn.api.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistMap {

    @SerializedName("primary_artists")
    @Expose
    private List<Object> primaryArtists = null;
    @SerializedName("featured_artists")
    @Expose
    private List<Object> featuredArtists = null;
    @SerializedName("artists")
    @Expose
    private List<Artist> artists = null;

    public List<Object> getPrimaryArtists() {
        return primaryArtists;
    }

    public void setPrimaryArtists(List<Object> primaryArtists) {
        this.primaryArtists = primaryArtists;
    }

    public List<Object> getFeaturedArtists() {
        return featuredArtists;
    }

    public void setFeaturedArtists(List<Object> featuredArtists) {
        this.featuredArtists = featuredArtists;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

}
