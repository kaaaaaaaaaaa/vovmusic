package com.vovmusic.uit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Theme implements Parcelable {
    @SerializedName("idTheme")
    @Expose
    private int idTheme;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("img")
    @Expose
    private String img;

    protected Theme(Parcel in) {
        idTheme = in.readInt();
        name = in.readString();
        img = in.readString();
    }

    public static final Creator<Theme> CREATOR = new Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };

    public int getIdTheme() {
        return idTheme;
    }

    public void setIdTheme(int idTheme) {
        this.idTheme = idTheme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idTheme);
        dest.writeString(name);
        dest.writeString(img);
    }
}