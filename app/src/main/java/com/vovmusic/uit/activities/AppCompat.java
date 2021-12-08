package com.vovmusic.uit.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.services.SettingLanguage;

public class AppCompat extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataLocalManager.init(this);

        if (DataLocalManager.getTheme()) { // Chế độ sáng/tối
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Ngôn ngữ ứng dụng
        SettingLanguage settingLanguage = new SettingLanguage(this);
        settingLanguage.Update_Language(DataLocalManager.getLanguage());
    }
}
