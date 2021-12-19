package com.vovmusic.uit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Genre implements Parcelable {
    @SerializedName("idGenre")
    @Expose
    private int idGenre;

    @SerializedName("idTheme")
    @Expose
    private int idTheme;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("img")
    @Expose
    private String img;

    protected Genre(Parcel in) {
        idGenre = in.readInt();
        idTheme = in.readInt();
        name = in.readString();
        img = in.readString();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    public int getIdGenre() {
        return idGenre;
    }

    public void setIdGenre(int idGenre) {
        this.idGenre = idGenre;
    }

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
        dest.writeInt(idGenre);
        dest.writeInt(idTheme);
        dest.writeString(name);
        dest.writeString(img);
    }
}