
package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoreInfo {

    @SerializedName("song_count")
    @Expose
    private String songCount;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("artistMap")
    @Expose
    private ArtistMap artistMap;

    public String getSongCount() {
        return songCount;
    }

    public void setSongCount(String songCount) {
        this.songCount = songCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArtistMap getArtistMap() {
        return artistMap;
    }

    public void setArtistMap(ArtistMap artistMap) {
        this.artistMap = artistMap;
    }

}
