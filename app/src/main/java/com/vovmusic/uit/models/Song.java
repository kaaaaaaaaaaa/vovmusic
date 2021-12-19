package com.vovmusic.uit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable {
    // Model dùng để mapping với dữ liệu mà API trả về, để tiện thì các bạn có thể vào
    // trang http://www.jsonschema2pojo.org/ để convert dữ liệu sang các class model

    /* - @SerializedName cần thiết cho Gson để ánh xạ các khoá JSON với các trường dữ liệu của chúng ta.
    Để phù hợp với quy ước đặt tên kiểu camelCase của Java cho các thuộc tính thành viên của lớp,
    bạn không nên sử dụng dấu gạch dưới để tách các từ ngữ trong một biến. @SerializedName giúp chuyển đổi giữa hai cái này.

       - Chú thích @Expose chỉ ra rằng thành viên này nên được trình bày với JSON serialization hoặc deserialization.
    */

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("img")
    @Expose
    private String img;

    @SerializedName("singer")
    @Expose
    private String singer;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("like")
    @Expose
    private String like;

    @SerializedName("lyric")
    @Expose
    private String lyric;

    @SerializedName("mvcode")
    @Expose
    private String mvcode;

    protected Song(Parcel in) {
        id = in.readInt();
        name = in.readString();
        img = in.readString();
        singer = in.readString();
        link = in.readString();
        like = in.readString();
        lyric = in.readString();
        mvcode = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getMvcode() {
        return mvcode;
    }

    public void setMvcode(String mvcode) {
        this.mvcode = mvcode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(img);
        dest.writeString(singer);
        dest.writeString(link);
        dest.writeString(like);
        dest.writeString(lyric);
        dest.writeString(mvcode);
    }
}