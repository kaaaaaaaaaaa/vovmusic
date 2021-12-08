package com.vovmusic.uit.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SharedPreferencesManager = "SharedPreferencesManager";
    private Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public void deleteAllData() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xoá tất cả data SharedPreferences
        editor.apply(); // Cập nhật dữ liệu mà không cần trả về kết quả thực thi lệnh thành công hay thất bại.
    }

    public void deleteUserID(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply(); // Cập nhật dữ liệu mà không cần trả về kết quả thực thi lệnh thành công hay thất bại.
    }

    public void deleteUserAvatar(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply(); // Cập nhật dữ liệu mà không cần trả về kết quả thực thi lệnh thành công hay thất bại.
    }

    public void putBooleanValue(String key, boolean value) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply(); // Cập nhật dữ liệu mà không cần trả về kết quả thực thi lệnh thành công hay thất bại.
    }

    public boolean getBooleanValue(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public void putStringValue(String key, String value) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // Cập nhật dữ liệu mà không cần trả về kết quả thực thi lệnh thành công hay thất bại.
    }

    public String getStringValue(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
