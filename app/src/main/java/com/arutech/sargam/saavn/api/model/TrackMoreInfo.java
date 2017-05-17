package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anshuman on 20/04/17.
 */
public class TrackMoreInfo {
    @SerializedName("music")
    @Expose
    private String music;
    @SerializedName("album_id")
    @Expose
    private String albumId;
    @SerializedName("album")
    @Expose
    private String album;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("320kbps")
    @Expose
    private String _320kbps;
    @SerializedName("encrypted_media_url")
    @Expose
    private String encryptedMediaUrl;
    @SerializedName("album_url")
    @Expose
    private String albumUrl;
    @SerializedName("duration")
    @Expose
    private String duration;
//    @SerializedName("rights")
//    @Expose
//    private Rights rights;
    @SerializedName("cache_state")
    @Expose
    private String cacheState;
    @SerializedName("has_lyrics")
    @Expose
    private String hasLyrics;
    @SerializedName("starred")
    @Expose
    private String starred;
    @SerializedName("artistMap")
    @Expose
    private ArtistMap artistMap;
    @SerializedName("lyrics_id")
    @Expose
    private String lyricsId;

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String get_320kbps() {
        return _320kbps;
    }

    public void set_320kbps(String _320kbps) {
        this._320kbps = _320kbps;
    }

    public String getEncryptedMediaUrl() {
        return encryptedMediaUrl;
    }

    public void setEncryptedMediaUrl(String encryptedMediaUrl) {
        this.encryptedMediaUrl = encryptedMediaUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCacheState() {
        return cacheState;
    }

    public void setCacheState(String cacheState) {
        this.cacheState = cacheState;
    }

    public String getHasLyrics() {
        return hasLyrics;
    }

    public void setHasLyrics(String hasLyrics) {
        this.hasLyrics = hasLyrics;
    }

    public String getStarred() {
        return starred;
    }

    public void setStarred(String starred) {
        this.starred = starred;
    }

    public ArtistMap getArtistMap() {
        return artistMap;
    }

    public void setArtistMap(ArtistMap artistMap) {
        this.artistMap = artistMap;
    }

    public String getLyricsId() {
        return lyricsId;
    }

    public void setLyricsId(String lyricsId) {
        this.lyricsId = lyricsId;
    }

    @Override
    public String toString() {
        return "TrackMoreInfo{" +
                "music='" + music + '\'' +
                ", albumId='" + albumId + '\'' +
                ", album='" + album + '\'' +
                ", label='" + label + '\'' +
                ", origin='" + origin + '\'' +
                ", _320kbps='" + _320kbps + '\'' +
                ", encryptedMediaUrl='" + encryptedMediaUrl + '\'' +
                ", albumUrl='" + albumUrl + '\'' +
                ", duration='" + duration + '\'' +
                ", cacheState='" + cacheState + '\'' +
                ", hasLyrics='" + hasLyrics + '\'' +
                ", starred='" + starred + '\'' +
                ", artistMap=" + artistMap +
                ", lyricsId='" + lyricsId + '\'' +
                '}';
    }
}
