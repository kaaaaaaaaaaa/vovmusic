package com.vovmusic.uit.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.vovmusic.uit.R;
import com.vovmusic.uit.services.FullPlayerManagerService;

public class YoutubeActivity extends YouTubeBaseActivity {
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    //ImageView back;
    private Button back;
    private TextView tvSongName;
    private TextView tvArtist;
    private String MvCode;
    boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        this.back = findViewById(R.id.ivBack);
        this.back.setOnClickListener(v -> finish());

        this.youTubePlayerView = findViewById(R.id.youtubeView2);

        this.tvSongName = findViewById(R.id.tvSongName);
        this.tvSongName.setSelected(true); // Text will be moved
        this.tvSongName.setText(getIntent().getStringExtra("SongName").trim());

        this.tvArtist = findViewById(R.id.tvArtist);
        this.tvArtist.setSelected(true); // Text will be
        this.tvArtist.setText(getIntent().getStringExtra("Artist").trim());

        this.MvCode = getIntent().getStringExtra("MvCode");
        if (MvCode != null) {
            onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(MvCode); // Hiển thị Video của Youtube
                    try {
                        if (FullPlayerManagerService.mediaPlayer != null)
                            FullPlayerManagerService.mediaPlayer.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    error = true;
                }
            };
        }

        if (error) {
            Toast.makeText(this, R.string.toast31, Toast.LENGTH_SHORT).show();
        }
        ///btn = findViewById(R.id.btnPlay);
        //btn.setOnClickListener(v->{
        youTubePlayerView.initialize("AIzaSyCUyAkQaYLAsYcimtKB3nyCo3ow-pyuElM", onInitializedListener);
        //});
    }
}