package com.vovmusic.uit.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.vovmusic.uit.R;
import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.services.FullPlayerManagerService;
import com.vovmusic.uit.services.MiniPlayerOnLockScreenService;

import java.io.IOException;
import java.util.ArrayList;

public class MiniPlayerFragment extends Fragment {
    private ImageView ivListItemSong;
    private ImageView ivPlay;
    private ImageView ivListItemSongLove;
    private ImageView ivListItemSongNext;
    private TextView tvListItemSongName;
    private TextView tvListItemSongSinger;
    private MediaPlayer mediaPlayer = FullPlayerManagerService.mediaPlayer;
    private View miniplayer;
    private int position = FullPlayerManagerService.position;
    public static boolean repeat = false;
    public static boolean checkRandom = false;
    public static final String MINI_PLAYER_CLICK = "MINI_PLAYER_CLICK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_miniplayer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Mapping(view);
            Event();
/*            if(!FullPlayerManagerService.isRegister){
                getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACkS"));
                FullPlayerManagerService.isRegister = true;
            }*/
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACkS"));

            if (FullPlayerManagerService.mediaPlayer != null) {
                if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                } else {
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
                }
            } else {
                CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!FullPlayerManagerService.isRegister) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACkS"));
            FullPlayerManagerService.isRegister = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
            FullPlayerManagerService.isRegister = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void Mapping(View view) {
        ivListItemSong = view.findViewById(R.id.ivListItemSong);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivListItemSongLove = view.findViewById(R.id.ivListItemSongLove);
        ivListItemSongNext = view.findViewById(R.id.ivListItemSongNext);
        tvListItemSongName = view.findViewById(R.id.tvListItemSongName);
        tvListItemSongSinger = view.findViewById(R.id.tvListItemSongSinger);
        miniplayer = view.findViewById(R.id.miniplayer);
        mediaPlayer = FullPlayerManagerService.mediaPlayer;

        if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
            this.ivPlay.setImageResource(R.drawable.ic_pause);
        } else {
            this.ivPlay.setImageResource(R.drawable.ic_play_2);
        }
    }

    private void Event() {
        Picasso.get()
                .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(this.ivListItemSong);
        this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName().trim());
        this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger().trim());

        this.ivPlay.setOnClickListener(v -> {
            onSongPlay();
        });
        this.ivListItemSongNext.setOnClickListener(v -> {
            onSongNext();
        });

        miniplayer.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), FullPlayerActivity.class);
            intent.putExtra("MINI_PLAYER_CLICK", MINI_PLAYER_CLICK);
            startActivity(intent);
        });
    }

    public class PlayMP3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }

        @Override
        protected void onPostExecute(String song) {
            super.onPostExecute(song);
            try {
/*                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });

                mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                mediaPlayer.prepare(); */


                if (FullPlayerManagerService.mediaPlayer != null) {
                    try {
                        FullPlayerManagerService.mediaPlayer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                FullPlayerManagerService.mediaPlayer = new MediaPlayer();
                FullPlayerManagerService.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                FullPlayerManagerService.mediaPlayer.setOnCompletionListener(mp -> {
                    FullPlayerManagerService.mediaPlayer.stop();
                    FullPlayerManagerService.mediaPlayer.reset();
                });

                FullPlayerManagerService.currentSong = FullPlayerActivity.dataSongArrayList.get(position);
                //FullPlayerManagerService.position = position;
                Log.d("CurrentSong", FullPlayerManagerService.currentSong.getName());
                FullPlayerManagerService.mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                FullPlayerManagerService.mediaPlayer.prepare();
                FullPlayerManagerService.mediaPlayer.start();
                FullPlayerManagerService.listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongArrayList);

                //FullPlayerManagerService.mediaPlayer = mediaPlayer;
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //mediaPlayer.start();
            //FullPlayerManagerService.mediaPlayer.start();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case MiniPlayerOnLockScreenService.ACTION_PLAY:
                case MiniPlayerOnLockScreenService.ACTION_PAUSE:
                    onSongPlay();
                    break;
                case MiniPlayerOnLockScreenService.ACTION_PRE:

                    try {
                        onSongPrev();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MiniPlayerOnLockScreenService.ACTION_NEXT:

                    try {
                        onSongNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }


    };

    private void onSongPlay() {
        if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
            FullPlayerManagerService.mediaPlayer.pause();
            this.ivPlay.setImageResource(R.drawable.ic_play_2);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
        } else {
            FullPlayerManagerService.mediaPlayer.start();
            this.ivPlay.setImageResource(R.drawable.ic_pause);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
        }
    }

    public void onSongNext() {
        this.ivListItemSongNext.setClickable(false);
        miniplayer.setClickable(false);
        new Handler().postDelayed(() -> {
            this.ivListItemSongNext.setClickable(true);
            miniplayer.setClickable(true);
        }, 2000);
        if (FullPlayerManagerService.listCurrentSong.size() > 0) {
            try {
                if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release(); // Đồng bộ
                    mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (FullPlayerManagerService.position < FullPlayerManagerService.listCurrentSong.size()) {
                ivPlay.setImageResource(R.drawable.ic_pause);
                FullPlayerManagerService.position++;
            }
            if (FullPlayerManagerService.position > FullPlayerManagerService.listCurrentSong.size() - 1) {
                FullPlayerManagerService.position = 0;
            }
            new PlayMP3().execute(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getLink());
            Picasso.get()
                    .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivListItemSong);
            this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName().trim());
            this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger().trim());
        }
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }

    public void onSongPrev() {
        this.ivListItemSongNext.setClickable(false);
        miniplayer.setClickable(false);
        new Handler().postDelayed(() -> {
            this.ivListItemSongNext.setClickable(true);
            miniplayer.setClickable(true);
        }, 2000);
        if (FullPlayerManagerService.listCurrentSong.size() > 0) {
            try {
                if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release(); // Đồng bộ
                    mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (FullPlayerManagerService.position < FullPlayerManagerService.listCurrentSong.size()) {
                ivPlay.setImageResource(R.drawable.ic_pause);
                FullPlayerManagerService.position--;
            }
            if (FullPlayerManagerService.position < 0) {
                FullPlayerManagerService.position = FullPlayerManagerService.listCurrentSong.size() - 1;
            }
            new PlayMP3().execute(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getLink());
            Picasso.get()
                    .load(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getImg())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(this.ivListItemSong);
            this.tvListItemSongName.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getName());
            this.tvListItemSongSinger.setText(FullPlayerManagerService.listCurrentSong.get(FullPlayerManagerService.position).getSinger());
        }
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }

    public void CreateNotification(String action) {
        Intent intent = new Intent(getActivity(), MiniPlayerOnLockScreenService.class);
        intent.setAction(action);
        getActivity().startService(intent);
        //NotificationService.NotificationService(getContext(),FullPlayerActivity.dataSongArrayList.get(position),R.drawable.ic_pause,position,FullPlayerActivity.dataSongArrayList.size());
    }
}

