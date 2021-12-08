package com.vovmusic.uit.activities;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.adapters.SongAdapter;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPlaylistActivity extends AppCompat {
    private static final String TAG = "PPActivity";

    private ImageView ivPersonalPlaylistBack;
    private TextView tvPersonalPlaylistTitle;
    private ImageView ivPersonalPlaylistMore;
    private Button btnPersonalPlayAll;
    private TextView tvEmptySong;

    private ShimmerFrameLayout sflItemSong;
    private RecyclerView rvPersonalPlaylist;

    private ScaleAnimation scaleAnimation;

    private ArrayList<Song> songArrayList;
    private UserPlaylist userPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_playlist);

        DataLocalManager.init(this); // Khởi tạo

        Mapping();
        Get_Data_Intent();
        Event();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Get_Data_Intent();
    }

    private void Mapping() {
        this.ivPersonalPlaylistBack = findViewById(R.id.ivPersonalPlaylistBack);

        this.tvPersonalPlaylistTitle = findViewById(R.id.tvPersonalPlaylistTitle);
        this.tvPersonalPlaylistTitle.setSelected(true); // Text will be moved

        this.ivPersonalPlaylistMore = findViewById(R.id.ivPersonalPlaylistMore);

        this.btnPersonalPlayAll = findViewById(R.id.btnPersonalPlayAll);
        this.btnPersonalPlayAll.setEnabled(false); // Set false để cho nó không hoạt động trước đã, sau khi load xong hết các bài hát thì gọi hàm Play_All_Song();

        this.tvEmptySong = findViewById(R.id.tvEmptySong);
        this.tvEmptySong.setSelected(true); // Text will be moved

        this.sflItemSong = findViewById(R.id.sflItemSong);
        this.rvPersonalPlaylist = findViewById(R.id.rvPersonalPlaylist);
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(this, this.ivPersonalPlaylistBack);
        this.scaleAnimation.Event_ImageView();
        this.ivPersonalPlaylistBack.setOnClickListener(v -> {
            finish();
        });

        this.scaleAnimation = new ScaleAnimation(this, this.ivPersonalPlaylistMore);
        this.scaleAnimation.Event_ImageView();

        this.scaleAnimation = new ScaleAnimation(this, this.btnPersonalPlayAll);
        this.scaleAnimation.Event_Button();
    }

    private void Get_Data_Intent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("FAVORITESONG")) {
                String titlePlaylist = intent.getStringExtra("FAVORITESONG");
                if (!titlePlaylist.isEmpty()) {
                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);

                    Handle_Favorite_Song();
                }
            } else if (intent.hasExtra("SONGPLAYLIST")) {
                this.userPlaylist = (UserPlaylist) intent.getParcelableExtra("SONGPLAYLIST");
                if (this.userPlaylist != null) {
                    int playlistID = this.userPlaylist.getYouID();
                    String titlePlaylist = this.userPlaylist.getName();

                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);
                    Handle_UserPlaylist_Song(playlistID);
                }
            } else if (intent.hasExtra("DOWNLOADSONG")) {
                String titlePlaylist = intent.getStringExtra("DOWNLOADSONG");
                if (!titlePlaylist.isEmpty()) {
                    this.tvPersonalPlaylistTitle.setText(titlePlaylist);

                    Handle_Download_Song();
                }
            }
        }
    }

    private void Handle_Download_Song() {
        songArrayList = (ArrayList<Song>) DataLocalManager.getListSongDownloaded();
        if (songArrayList != null && songArrayList.size() > 0) {

            rvPersonalPlaylist.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(PersonalPlaylistActivity.this);
            layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
            rvPersonalPlaylist.setLayoutManager(layoutManager);
            rvPersonalPlaylist.setAdapter(new SongAdapter(PersonalPlaylistActivity.this, songArrayList, "DOWNLOADSONG"));
            //set ItemAnimator for RecyclerView
            rvPersonalPlaylist.setItemAnimator(new DefaultItemAnimator());

            sflItemSong.setVisibility(View.GONE); // Load biến mất
            rvPersonalPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin

            Play_All_Song();
            Log.d(TAG, songArrayList.get(0).getName());
        } else {
            sflItemSong.setVisibility(View.GONE); // Load biến mất
            tvEmptySong.setVisibility(View.VISIBLE); // Hiện thông báo chưa có bài hát nào
        }
    }

    private void Handle_UserPlaylist_Song(int playlistID) {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getSongUserPlaylist(DataLocalManager.getUserID(), playlistID);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) { // Trường hợp người dùng đã có bài hát
                    rvPersonalPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(PersonalPlaylistActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvPersonalPlaylist.setLayoutManager(layoutManager);
                    rvPersonalPlaylist.setAdapter(new SongAdapter(PersonalPlaylistActivity.this, songArrayList, playlistID, "PLAYLISTSONG"));
                    //set ItemAnimator for RecyclerView
                    rvPersonalPlaylist.setItemAnimator(new DefaultItemAnimator());

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvPersonalPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin
                    Play_All_Song();

                    Log.d(TAG, songArrayList.get(0).getName());
                } else { // Trường hợp người dùng chưa có bài hát yêu thích
                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    tvEmptySong.setVisibility(View.VISIBLE); // Hiện thông báo chưa có bài hát nào
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_UserPlaylist_Song(Error): " + t.getMessage());
            }
        });
    }

    private void Handle_Favorite_Song() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) { // Trường hợp người dùng đã có bài hát yêu thích
                    rvPersonalPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(PersonalPlaylistActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvPersonalPlaylist.setLayoutManager(layoutManager);
                    rvPersonalPlaylist.setAdapter(new SongAdapter(PersonalPlaylistActivity.this, songArrayList, "FAVORITESONG"));
                    //set ItemAnimator for RecyclerView
                    rvPersonalPlaylist.setItemAnimator(new DefaultItemAnimator());

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvPersonalPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin
                    Play_All_Song();

                    Log.d(TAG, songArrayList.get(0).getName());
                } else { // Trường hợp người dùng chưa có bài hát yêu thích
                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    tvEmptySong.setVisibility(View.VISIBLE); // Hiện thông báo chưa có bài hát nào
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_Favorite_Song(Error): " + t.getMessage());
            }
        });
    }

    private void Play_All_Song() { // Hàm này sẽ đảm bảo khi các bài hát load xong về giao diện thì button này mới hoạt động
        this.btnPersonalPlayAll.setEnabled(true);
        this.btnPersonalPlayAll.setOnClickListener(v -> {
            if (songArrayList.size() > 0) {
                Intent intent = new Intent(this, FullPlayerActivity.class);
                intent.putExtra("ALLFAVORITESONGS", songArrayList);
                startActivity(intent);
            } else {
                tvEmptySong.setVisibility(View.VISIBLE); // Hiện thông báo chưa có bài hát nào
            }
        });
    }
}