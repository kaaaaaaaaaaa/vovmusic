package com.vovmusic.uit.services;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.models.Song;

import java.io.File;
import java.util.List;

public class DownloadService {
    private static DownloadService instance;
    public DownloadManager downloadManager;

    public static void init(Context context) {
        instance = new DownloadService();
    }

    public static DownloadService getInstance() {
        if (instance == null) {
            instance = new DownloadService();
        }
        return instance;
    }

    public static void setListSongDownloaded(List<Song> song) {
        DataLocalManager.setListSongDownloaded(song);
    }

    public static List<Song> getListSongDownloaded() {
        return DataLocalManager.getListSongDownloaded();
    }

    public static boolean isSongDownloaded(int id) {
        for (Song s : getListSongDownloaded()) {
            if (s.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static void DownloadSong(Context context, Song song) {
        if (!isSongDownloaded(song.getId())) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song.getLink()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Download");
            request.setDescription("Dowloading....");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "" + song.getName() + ".mp3");
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File uri = new File(file, song.getName() + ".mp3");
            song.setLink(uri.toString());
            List<Song> listSong = getListSongDownloaded();
            listSong.add(song);
            DataLocalManager.setListSongDownloaded(listSong);

            Log.d("Alone", uri.toString());
        } else {
            Toast.makeText(context, "Bạn đã tải bài hát này rồi!", Toast.LENGTH_SHORT).show();
        }

    }

    public static int GetDownloadSongIndex(int id) {
        List<Song> listSong = getListSongDownloaded();
        for (int i = 0; i < listSong.size(); i++) {
            if (listSong.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public static Song GetDownloadSongById(int id) {
        List<Song> listSong = getListSongDownloaded();
        for (int i = 0; i < listSong.size(); i++) {
            if (listSong.get(i).getId() == id) {
                return listSong.get(i);
            }
        }
        return null;
    }

    public static void RemoveDownloadSongById(int id) {
        List<Song> listSong = getListSongDownloaded();
        listSong.remove(GetDownloadSongIndex(id));
        setListSongDownloaded(listSong);
    }

    public static void DeleteDownloadedSong(Context context, Song song) {
        File newFile = new File(song.getLink());
        if (newFile.exists()) {
            Toast.makeText(context, "Đã xoá file nhạc!", Toast.LENGTH_SHORT).show();
        }
        if (isSongDownloaded(song.getId())) {
            try {
                if (newFile.delete()) {
                    Toast.makeText(context, "Đã xoá file nhạc!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("CheckError", "Xoá file thất bại!");
            }

            RemoveDownloadSongById(song.getId());
        } else {
            Toast.makeText(context, "Bạn chưa tải bài hát này !", Toast.LENGTH_SHORT).show();
        }
    }
}
