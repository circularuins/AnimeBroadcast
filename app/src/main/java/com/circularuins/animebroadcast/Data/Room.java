package com.circularuins.animebroadcast.Data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by natsuhikowake on 15/04/05.
 */
public class Room implements Parcelable {

    @SerializedName("id")
    private String roomId;

    @SerializedName("name")
    private String roomName;

    @SerializedName("posts")
    private int countPosts;

    @SerializedName("created_on")
    private String createdOn;

    @SerializedName("updated_on")
    private String updatedOn;

    @SerializedName("image_url")
    private String imageUrl;

    public Bitmap image;

    public Room(String roomId, String roomName, int countPosts, String createdOn, String updatedOn, String imageUrl) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.countPosts = countPosts;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.imageUrl = imageUrl;
    }

    /**
     * Parcelableをこのクラスに実装する記述
     */
    @Override
    public int describeContents() {
        return 0;
    }

    private Room(final Parcel in) {
        roomId = in.readString();
        roomName = in.readString();
        countPosts = in.readInt();
        createdOn = in.readString();
        updatedOn = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomId);
        dest.writeString(roomName);
        dest.writeInt(countPosts);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
        dest.writeString(imageUrl);
    }

    //よくわからないが、staticで実装しないといけないらしい
    public static final Parcelable.Creator<Room> CREATOR
            = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    /**
     * getter, setter
     */
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCountPosts() {
        return countPosts;
    }

    public void setCountPosts(int countPosts) {
        this.countPosts = countPosts;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
