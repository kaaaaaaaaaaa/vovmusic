package com.vovmusic.uit.services;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FullPlayerManagerService {
    private static final String TAG = "FullPlayerManagerService";
    private static FullPlayerManagerService instance;

    public static boolean repeat = false;
    public static boolean checkRandom = false;
    public static boolean isRegister = false;
    public static MediaPlayer mediaPlayer;
    public static Song currentSong;
    public static List<Song> listCurrentSong;
    public static int position = 0;


    public static class PlayMP3 extends AsyncTask<String, Void, String> {
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
                mediaPlayer.prepare();*/


                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }


                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });
                currentSong = FullPlayerActivity.dataSongArrayList.get(position);
                mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                mediaPlayer.prepare();
                mediaPlayer.start();
                listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongArrayList);

                //mediaPlayer = mediaPlayer;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mediaPlayer.start();
            //mediaPlayer.start();
        }
    }

    public static void CreateNotification(String action, Activity activity) {
        Intent intent = new Intent(activity, MiniPlayerOnLockScreenService.class);
        intent.setAction(action);
        activity.startService(intent);
        //NotificationService.NotificationService(getContext(),FullPlayerActivity.dataSongArrayList.get(position),R.drawable.ic_pause,position,FullPlayerActivity.dataSongArrayList.size());
    }
}
