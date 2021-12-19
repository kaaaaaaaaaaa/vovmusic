package com.vovmusic.uit.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.activities.AddUpdateActivity;
import com.vovmusic.uit.activities.PersonalPlaylistActivity;
import com.vovmusic.uit.adapters.UserPlaylistAdapter;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPlaylistFragment extends Fragment {
    private static final String TAG = "PPFragment";

    private LinearLayout llFrameLoveSong;

    private TextView tvNumberPlaylist;
    private TextView tvNumberSongLove;
    private ImageView ivAddPlaylist;
    private TextView tvTitleLoveSong;
    private TextView tvEmptyPlaylist;

    private TextView tvNumberDownloadSong;
    private TextView tvTitleDownloadSong;
    private LinearLayout llFrameDownloadSong;

    private ShimmerFrameLayout sflItemUserPlaylist;
    private RecyclerView rvYourPlaylist;
    private UserPlaylistAdapter userPlaylistAdapter;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ArrayList<Song> songArrayList;
    private ArrayList<UserPlaylist> userPlaylistArrayList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_personal_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataLocalManager.init(getContext());

        Log.d(TAG, "onViewCreated");

        Mapping(view);
//        Handle_Number_Download_Song();
//        Handle_Number_Favorite_Song();
//        Handle_UserPlaylist();
        Event();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        Handle_Number_Download_Song();
        Handle_Number_Favorite_Song();
        Handle_UserPlaylist();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    private void Mapping(View view) {
        this.loadingDialog = new LoadingDialog(getActivity());
//        this.loadingDialog.Start_Loading();

        this.llFrameLoveSong = view.findViewById(R.id.llFrameLoveSong);
        this.tvNumberPlaylist = view.findViewById(R.id.tvNumberPlaylist);
        this.tvNumberSongLove = view.findViewById(R.id.tvNumberSongLove);
        this.ivAddPlaylist = view.findViewById(R.id.ivAddPlaylist);
        this.tvTitleLoveSong = view.findViewById(R.id.tvTitleLoveSong);

        this.tvEmptyPlaylist = view.findViewById(R.id.tvEmptyPlaylist);
        this.tvEmptyPlaylist.setSelected(true); // Text will be moved

        this.sflItemUserPlaylist = view.findViewById(R.id.sflItemUserPlaylist);
        this.rvYourPlaylist = view.findViewById(R.id.rvYourPlaylist);

        this.llFrameDownloadSong = view.findViewById(R.id.llFrameDownloadSong);
        this.tvNumberDownloadSong = view.findViewById(R.id.tvNumberSongDownload);
        this.tvTitleDownloadSong = view.findViewById(R.id.tvTitleDownloadSong);
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(getContext(), this.ivAddPlaylist);
        this.scaleAnimation.Event_ImageView();
        this.ivAddPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddUpdateActivity.class);
            intent.putExtra("ADDPLAYLIST", "ADDPLAYLIST");
            startActivity(intent);
        });

        this.llFrameDownloadSong.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PersonalPlaylistActivity.class);
            intent.putExtra("DOWNLOADSONG", this.tvTitleDownloadSong.getText());
            startActivity(intent);
        });

        this.llFrameLoveSong.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PersonalPlaylistActivity.class);
            intent.putExtra("FAVORITESONG", this.tvTitleLoveSong.getText());
            startActivity(intent);
        });
    }

    private void Open_Add_Playlist_Dialog(int gravity) {
        final Dialog dialog = new Dialog(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // ẩn thanh tiêu đề
        dialog.setContentView(R.layout.layout_edittext_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần activity

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        EditText etDialogContentPlaylist = dialog.findViewById(R.id.etDialogContentPlaylist);
        etDialogContentPlaylist.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button btnDialogCancelPlaylist = dialog.findViewById(R.id.btnDialogCancelPlaylist);
        Button btnDialogActionPlaylist = dialog.findViewById(R.id.btnDialogActionPlaylist);

        this.scaleAnimation = new ScaleAnimation(getContext(), btnDialogCancelPlaylist);
        this.scaleAnimation.Event_Button();
        btnDialogCancelPlaylist.setOnClickListener(v -> {
            dialog.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(getContext(), btnDialogActionPlaylist);
        this.scaleAnimation.Event_Button();
        btnDialogActionPlaylist.setOnClickListener(v -> {
        });

        dialog.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handle_Number_Download_Song() {
        List<Song> sonDownloadgArrayList = DataLocalManager.getListSongDownloaded();
        if (sonDownloadgArrayList != null) {
            tvNumberDownloadSong.setText(String.valueOf(sonDownloadgArrayList.size()));

            Log.d(TAG, "Number Download Song: " + sonDownloadgArrayList.size());
        } else {
            tvNumberDownloadSong.setText("0");
        }
    }

    private void Handle_Number_Favorite_Song() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getFavoriteSongUser(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null) {
                    tvNumberSongLove.setText(String.valueOf(songArrayList.size()));

                    Log.d(TAG, "Number Favorite Song: " + songArrayList.size());
                } else {
                    tvNumberSongLove.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, "Handle_Number_Favorite_Song(Error): " + t.getMessage());
            }
        });
    }

    private void Handle_UserPlaylist() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<UserPlaylist>> callBack = dataService.getUserPlaylist(DataLocalManager.getUserID());
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>();
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null && userPlaylistArrayList.size() > 0) {
                    rvYourPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvYourPlaylist.setLayoutManager(layoutManager);

                    userPlaylistAdapter = new UserPlaylistAdapter(getContext(), userPlaylistArrayList, tvNumberPlaylist);
                    rvYourPlaylist.setAdapter(userPlaylistAdapter);

                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvNumberPlaylist.setText(String.valueOf(userPlaylistAdapter.getItemCount())); // Hiển thị số lượng Playlist
                    rvYourPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin Playlist

//                    Log.d(TAG, "User Playlist: " + userPlaylistArrayList.get(0).getName());
                } else {
                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvEmptyPlaylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                Log.d(TAG, "Handle_User_Playlist(Error): " + t.getMessage());
            }
        });
    }
}