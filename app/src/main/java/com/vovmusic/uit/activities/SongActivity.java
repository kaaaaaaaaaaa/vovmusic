package com.vovmusic.uit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.adapters.SongAdapter;
import com.vovmusic.uit.models.Album;
import com.vovmusic.uit.models.Genre;
import com.vovmusic.uit.models.Playlist;
import com.vovmusic.uit.models.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongActivity extends AppCompat {
    private static final String TAG = "SongActivity";

    private AdView avSongActivity;

    private CoordinatorLayout coordinatorlayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    private RecyclerView rvListSong;
    private ShimmerFrameLayout sflItemSong;

    private ArrayList<Song> songArrayList;

    private Playlist playlist;
    private Album album;
    private Genre genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

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
        this.coordinatorlayout = findViewById(R.id.cdlListSong);
        this.collapsingToolbarLayout = findViewById(R.id.ctlImage);
        this.toolbar = findViewById(R.id.tbListSong);

        this.floatingActionButton = findViewById(R.id.fabPlay);
        this.floatingActionButton.setEnabled(false); // Set false để cho nó không hoạt động trước đã, sau khi load xong hết các bài hát thì gọi hàm Play_All_Song();

        this.rvListSong = findViewById(R.id.rvListSong);
        this.sflItemSong = findViewById(R.id.sflItemSong);

        setSupportActionBar(this.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationIcon(R.drawable.ic_angle_left);

        this.collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorLight2)));
        this.collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorLight2));
    }

    private void Event() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void Get_Data_Intent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("PLAYLIST")) {
                this.playlist = (Playlist) intent.getParcelableExtra("PLAYLIST");
                if (this.playlist != null) {
                    Log.d(TAG, this.playlist.getName());

                    this.collapsingToolbarLayout.setTitle(this.playlist.getName());
                    Display_Song_Playlist(this.playlist.getId());
                }
            } else if (intent.hasExtra("ALBUM")) {
                this.album = (Album) intent.getParcelableExtra("ALBUM");
                if (this.album != null) {
                    Log.d(TAG, this.album.getName());

                    this.collapsingToolbarLayout.setTitle(this.album.getName());
                    Display_Song_Album(this.album.getId());
                }
            } else if (intent.hasExtra("GENRE")) {
                this.genre = (Genre) intent.getParcelableExtra("GENRE");
                if (this.genre != null) {
                    Log.d(TAG, this.genre.getName());

                    this.collapsingToolbarLayout.setTitle(this.genre.getName());
                    Display_Song_Genre(this.genre.getIdGenre());
                }
            }
        }
    }

    private void Display_Song_Genre(int id) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongGenre(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songArrayList, "SONG"));

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvListSong.setVisibility(View.VISIBLE); // Hiện thông tin

                    Play_All_Song();

                    Log.d(TAG, songArrayList.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Display_Song_Genre(Error)" + t.getMessage());
            }
        });
    }

    private void Display_Song_Playlist(int id) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongPlaylist(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songArrayList, "SONG"));

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvListSong.setVisibility(View.VISIBLE); // Hiện thông tin

                    Play_All_Song();

                    Log.d(TAG, songArrayList.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Display_Song_Playlist(Error)" + t.getMessage());
            }
        });
    }

    private void Display_Song_Album(int id) {
        DataService dataService = APIService.getService();
        Call<List<Song>> callBack = dataService.getSongAlbum(id);
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    rvListSong.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(SongActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvListSong.setLayoutManager(layoutManager);
                    rvListSong.setAdapter(new SongAdapter(SongActivity.this, songArrayList, "SONG"));

                    sflItemSong.setVisibility(View.GONE); // Load biến mất
                    rvListSong.setVisibility(View.VISIBLE); // Hiện thông tin

                    Play_All_Song();

                    Log.d(TAG, songArrayList.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Display_Song_Album(Error)" + t.getMessage());
            }
        });
    }

    private void Play_All_Song() { // Hàm này sẽ đảm bảo khi các bài hát load xong về giao diện thì button này mới hoạt động
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
//        this.avSongActivity = findViewById(R.id.avSongActivity);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        this.avSongActivity.loadAd(adRequest);


        this.floatingActionButton.setEnabled(true);
        this.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SongActivity.this, FullPlayerActivity.class);
            intent.putExtra("ALLSONGS", songArrayList);
            startActivity(intent);
        });
    }
}