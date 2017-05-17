package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anshuman on 20/04/17.
 */
public class TrackGroupDetail {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("perma_url")
    @Expose
    private String permaUrl;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("play_count")
    @Expose
    private String playCount;
    @SerializedName("explicit_content")
    @Expose
    private String explicitContent;
    @SerializedName("list_count")
    @Expose
    private String listCount;
    @SerializedName("list_type")
    @Expose
    private String listType;
    @SerializedName("list")
    @Expose
    private List<Track> list = null;
    @SerializedName("more_info")
    @Expose
    private MoreInfo moreInfo;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPermaUrl() {
        return permaUrl;
    }

    public void setPermaUrl(String permaUrl) {
        this.permaUrl = permaUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getExplicitContent() {
        return explicitContent;
    }

    public void setExplicitContent(String explicitContent) {
        this.explicitContent = explicitContent;
    }

    public String getListCount() {
        return listCount;
    }

    public void setListCount(String listCount) {
        this.listCount = listCount;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public List<Track> getList() {
        return list;
    }

    public void setList(List<Track> list) {
        this.list = list;
    }

    public MoreInfo getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(MoreInfo moreInfo) {
        this.moreInfo = moreInfo;
    }

    @Override
    public String toString() {
        return "TrackGroupDetail{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", permaUrl='" + permaUrl + '\'' +
                ", image='" + image + '\'' +
                ", language='" + language + '\'' +
                ", year='" + year + '\'' +
                ", playCount='" + playCount + '\'' +
                ", explicitContent='" + explicitContent + '\'' +
                ", listCount='" + listCount + '\'' +
                ", listType='" + listType + '\'' +
                ", list=" + list +
                ", moreInfo=" + moreInfo +
                '}';
    }
}
