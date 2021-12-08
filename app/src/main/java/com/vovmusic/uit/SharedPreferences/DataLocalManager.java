package com.vovmusic.uit.SharedPreferences;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.vovmusic.uit.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalManager {
    private static DataLocalManager instance;
    private SharedPreferencesManager sharedPreferencesManager;

    private static final String USER_ID = "USER_ID";
    private static final String USER_AVATAR = "USER_AVATAR";
    private static final String THEME = "THEME";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String SONG_DOWNLOADED = "SONG_DOWNLOADED";

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void deleteAllData() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteAllData();
    }

    public static void deleteUserID() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteUserID(USER_ID);
    }

    public static void deleteUserAvatar() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteUserAvatar(USER_AVATAR);
    }

    public static void setUserID(String userID) {
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(USER_ID, userID);
    }

    public static String getUserID() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(USER_ID);
    }


    public static void setUserAvatar(String userAvatar) {
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(USER_AVATAR, userAvatar);
    }

    public static String getUserAvatar() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(USER_AVATAR);
    }


    public static void setTheme(boolean theme) {
        DataLocalManager.getInstance().sharedPreferencesManager.putBooleanValue(THEME, theme);
    }

    public static boolean getTheme() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getBooleanValue(THEME);
    }


    public static void setLanguage(String language) {
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(LANGUAGE, language);
    }

    public static String getLanguage() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(LANGUAGE);
    }

    public static void setListSongDownloaded(List<Song> song) {
        Gson gs = new Gson();
        JsonArray jsonArray = gs.toJsonTree(song).getAsJsonArray();
        String strJsArraySong = jsonArray.toString();
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(SONG_DOWNLOADED, strJsArraySong);
    }

    public static List<Song> getListSongDownloaded() {
        String jsonSong = DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(SONG_DOWNLOADED);
        List<Song> listSong = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonSong);
            JSONObject jsonObject;
            Song song;
            Gson gs = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                song = gs.fromJson(jsonObject.toString(), Song.class);
                listSong.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listSong;
    }
}
