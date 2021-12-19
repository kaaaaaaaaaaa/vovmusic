package com.vovmusic.uit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("img")
    @Expose
    private String img;

    @SerializedName("isDark")
    @Expose
    private String isDark;

    @SerializedName("isEnglish")
    @Expose
    private String isEnglish;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIsDark() {
        return isDark;
    }

    public void setIsDark(String isDark) {
        this.isDark = isDark;
    }

    public String getIsEnglish() {
        return isEnglish;
    }

    public void setIsEnglish(String isEnglish) {
        this.isEnglish = isEnglish;
    }
}