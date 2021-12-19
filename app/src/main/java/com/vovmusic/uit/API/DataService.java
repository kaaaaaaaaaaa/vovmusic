package com.vovmusic.uit.API;

import com.vovmusic.uit.models.Album;
import com.vovmusic.uit.models.Comment;
import com.vovmusic.uit.models.Genre;
import com.vovmusic.uit.models.Playlist;
import com.vovmusic.uit.models.Slider;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.models.Status;
import com.vovmusic.uit.models.Theme;
import com.vovmusic.uit.models.User;
import com.vovmusic.uit.models.UserPlaylist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService { // Interface này dùng để định nghĩa các API dùng trong ứng dụng
    // Gửi phương thức và nhận dữ liệu từ server về

    // Lưu ý @FormUrlEncoded không thể sư dụng cho GET request
    // Yêu cầu form encoded chỉ sử dụng khi chúng ta muốn gửi dữ liệu lên server.
    // Bên cạnh đó bạn cần sử dụng chú thích @Field cho đối số truyền vào cái mà sẽ được chuyển lên server

    @GET("slider.php")
    Call<List<Slider>> getSlider();

    @FormUrlEncoded
    @POST("getsongslider.php")
    Call<List<Song>> getSongFromSlider(@Field("id") int id);

    @GET("playlistforcurrentday.php")
    Call<List<Playlist>> getPlaylistCurrentDay();

    @GET("themecurrentday.php")
    Call<List<Theme>> getThemeCurrentDay();

    @GET("albumcurrentday.php")
    Call<List<Album>> getAlbumCurrentDay();

    @FormUrlEncoded
    @POST("getsongalbum.php")
    Call<List<Song>> getSongAlbum(@Field("id") int id);

    @FormUrlEncoded
    @POST("getgenre.php")
    Call<List<Genre>> getGenre(@Field("id") int id);

    @FormUrlEncoded
    @POST("getsonggenre.php")
    Call<List<Song>> getSongGenre(@Field("id") int id);

    @GET("songlove.php")
    Call<List<Song>> getSongChart();

    @FormUrlEncoded
    @POST("getsongplaylist.php")
    Call<List<Song>> getSongPlaylist(@Field("id") int id);

    @FormUrlEncoded
    @POST("searchsong.php")
    Call<List<Song>> getSongSearch(@Field("keyword") String keyword);

    @FormUrlEncoded
    @POST("getuserfromid.php")
    Call<List<User>> getUserFromID(@Field("id") String id);

    @FormUrlEncoded
    @POST("addnewuser.php")
    Call<List<User>> addNewUser(@Field("id") String id, @Field("name") String name, @Field("email") String email, @Field("img") String img, @Field("isDark") String isDark, @Field("isEnglish") String isEnglish);

    @FormUrlEncoded
    @POST("getfavoritesongfromid.php")
    Call<List<Song>> getFavoriteSongUser(@Field("id") String id);

    @FormUrlEncoded
    @POST("adddeletefavoritesong.php")
    Call<List<Status>> addDeleteFavoriteSong(@Field("userID") String userID, @Field("songID") int songID);

    @FormUrlEncoded
    @POST("getuserplaylist.php")
    Call<List<UserPlaylist>> getUserPlaylist(@Field("userID") String userID);

    @FormUrlEncoded
    @POST("addupdatedeleteuserplaylist.php")
    Call<List<UserPlaylist>> addUpdateDeleteUserPlaylist(@Field("action") String action, @Field("playlistID") int playlistID, @Field("userID") String userID, @Field("playlistName") String playlistName);

    @FormUrlEncoded
    @POST("getsonguserplaylist.php")
    Call<List<Song>> getSongUserPlaylist(@Field("userID") String userID, @Field("playlistID") int playlistID);

    @FormUrlEncoded
    @POST("adddeleteuserplaylistsong.php")
    Call<List<Status>> addDeleteUserPlayListSong(@Field("action") String action, @Field("userID") String userID, @Field("playlistID") int playlistID, @Field("songID") int songID);

    @FormUrlEncoded
    @POST("getcommentsong.php")
    Call<List<Comment>> getCommentSong(@Field("id") int id);

    @FormUrlEncoded
    @POST("addupdatedeletecommentsong.php")
    Call<List<Status>> addUpdateDeleteCommentSong(@Field("action") String action, @Field("commentID") int commentID, @Field("songID") int songID, @Field("userID") String userID, @Field("content") String content, @Field("date") String date);

    @FormUrlEncoded
    @POST("addfeedback.php")
    Call<List<Status>> addFeedback(@Field("userID") String userID, @Field("star") float star, @Field("content") String content, @Field("date") String date);
}