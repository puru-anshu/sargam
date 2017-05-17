package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anshuman on 08/05/17.
 */
public class ArtistBio {
    @SerializedName("artistId")
    @Expose
    private String artistId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("follower_count")
    @Expose
    private String followerCount;
    @SerializedName("is_followed")
    @Expose
    private Boolean isFollowed;
    @SerializedName("dominantLanguage")
    @Expose
    private String dominantLanguage;
    @SerializedName("dominantType")
    @Expose
    private String dominantType;
    @SerializedName("topSongs")
    @Expose
    private TopSongs topSongs;
    @SerializedName("topAlbums")
    @Expose
    private TopAlbums topAlbums;
    @SerializedName("similarArtists")
    @Expose
    private List<Artist> similarArtists = null;
    @SerializedName("isRadioPresent")
    @Expose
    private Boolean isRadioPresent;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("fb")
    @Expose
    private String fb;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("wiki")
    @Expose
    private String wiki;
    @SerializedName("availableLanguages")
    @Expose
    private List<String> availableLanguages = null;


    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(String followerCount) {
        this.followerCount = followerCount;
    }

    public Boolean getFollowed() {
        return isFollowed;
    }

    public void setFollowed(Boolean followed) {
        isFollowed = followed;
    }

    public String getDominantLanguage() {
        return dominantLanguage;
    }

    public void setDominantLanguage(String dominantLanguage) {
        this.dominantLanguage = dominantLanguage;
    }

    public String getDominantType() {
        return dominantType;
    }

    public void setDominantType(String dominantType) {
        this.dominantType = dominantType;
    }

    public List<Track> getTopSongs() {
        return topSongs.getSongs();
    }

    public void setTopSongs(TopSongs topSongs) {
        this.topSongs = topSongs;
    }

    public List<TrackGroup> getTopAlbums() {
        return topAlbums.getAlbums();
    }

    public void setTopAlbums(TopAlbums topAlbums) {
        this.topAlbums = topAlbums;
    }

    public List<Artist> getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(List<Artist> similarArtists) {
        this.similarArtists = similarArtists;
    }

    public Boolean getRadioPresent() {
        return isRadioPresent;
    }

    public void setRadioPresent(Boolean radioPresent) {
        isRadioPresent = radioPresent;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }



}

class TopSongs {

    @SerializedName("songs")
    @Expose
    private List<Track> songs = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Track> getSongs() {
        return songs;
    }

    public void setSongs(List<Track> songs) {
        this.songs = songs;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}

class TopAlbums {

    @SerializedName("albums")
    @Expose
    private List<TrackGroup> albums = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<TrackGroup> getAlbums() {
        return albums;
    }

    public void setAlbums(List<TrackGroup> albums) {
        this.albums = albums;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}