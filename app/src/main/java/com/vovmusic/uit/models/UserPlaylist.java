package com.vovmusic.uit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPlaylist implements Parcelable {
    @SerializedName("youID")
    @Expose
    private int youID;

    @SerializedName("useID")
    @Expose
    private String useID;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("totalSong")
    @Expose
    private int totalSong;

    @SerializedName("status")
    @Expose
    private int status;

    public UserPlaylist(int youID, String useID, String name, int totalSong, int status) {
        this.youID = youID;
        this.useID = useID;
        this.name = name;
        this.totalSong = totalSong;
        this.status = status;
    }

    public UserPlaylist(Parcel in) {
        youID = in.readInt();
        useID = in.readString();
        name = in.readString();
        totalSong = in.readInt();
        status = in.readInt();
    }

    public static final Creator<UserPlaylist> CREATOR = new Creator<UserPlaylist>() {
        @Override
        public UserPlaylist createFromParcel(Parcel in) {
            return new UserPlaylist(in);
        }

        @Override
        public UserPlaylist[] newArray(int size) {
            return new UserPlaylist[size];
        }
    };

    public int getYouID() {
        return youID;
    }

    public void setYouID(int youID) {
        this.youID = youID;
    }

    public String getUseID() {
        return useID;
    }

    public void setUseID(String useID) {
        this.useID = useID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSong() {
        return totalSong;
    }

    public void setTotalSong(int totalSong) {
        this.totalSong = totalSong;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(youID);
        dest.writeString(useID);
        dest.writeString(name);
        dest.writeInt(totalSong);
        dest.writeInt(status);
    }
}