package com.circularuins.animebroadcast.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natsuhikowake on 15/04/05.
 */
public class Chat {

    @SerializedName("id")
    private String userId;

    @SerializedName("data")
    private String chatText;

    @SerializedName("time")
    private String postTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
