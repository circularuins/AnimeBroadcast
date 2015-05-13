package com.circularuins.animebroadcast.Data;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natsuhikowake on 15/05/13.
 */
public class Article {

    @SerializedName("user-id")
    private String userId;

    @SerializedName("profile-url")
    private String profileUrl;

    @SerializedName("text")
    private String text;

    @SerializedName("date")
    private String date;

    public Bitmap image;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
