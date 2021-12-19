package com.vovmusic.uit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdateActivity extends AppCompat {
    private static final String TAG = "AddUpdateActivity";

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ImageView ivClose;
    private TextView tvDialogTitlePlaylist;
    private EditText etDialogContentPlaylist;
    private Button btnDialogActionPlaylist;

    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private UserPlaylist userPlaylist;

    private final String ACTION_INSERT_PLAYLIST = "insert";
    private final String ACTION_UPDATE_PLAYLIST = "update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        Mapping();
        Get_Data_Intent();
        Event();
    }

    private void Mapping() {
        this.loadingDialog = new LoadingDialog(this);

        this.ivClose = findViewById(R.id.ivClose);

        this.tvDialogTitlePlaylist = findViewById(R.id.tvDialogTitlePlaylist);

        this.etDialogContentPlaylist = findViewById(R.id.etDialogContentPlaylist);
        this.etDialogContentPlaylist.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        this.btnDialogActionPlaylist = findViewById(R.id.btnDialogActionPlaylist);
    }

    private void Get_Data_Intent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("ADDPLAYLIST")) {
                this.scaleAnimation = new ScaleAnimation(this, this.btnDialogActionPlaylist);
                this.scaleAnimation.Event_Button();
                // Add new Playlist
                this.btnDialogActionPlaylist.setOnClickListener(v -> {
                    String playlistName = this.etDialogContentPlaylist.getText().toString().trim();
                    if (playlistName.isEmpty()) {
                        Toast.makeText(v.getContext(), R.string.toast12, Toast.LENGTH_SHORT).show();
                    } else {
//                        loadingDialog.Start_Loading();
                        Handle_Add_Update_UserPlaylist(ACTION_INSERT_PLAYLIST, 0, playlistName);
                    }
                });
            } else if (intent.hasExtra("UPDATEPLAYLIST")) {
                this.userPlaylist = (UserPlaylist) intent.getParcelableExtra("UPDATEPLAYLIST");
                if (this.userPlaylist != null) {
                    int playlistID = this.userPlaylist.getYouID();
                    String playlistName = this.userPlaylist.getName();

                    this.tvDialogTitlePlaylist.setText(R.string.tvDialogTitlePlaylist1);

                    this.etDialogContentPlaylist.setHint(R.string.etDialogContentPlaylist1);
                    this.etDialogContentPlaylist.setText(playlistName);

                    this.btnDialogActionPlaylist.setText(R.string.btnDialogActionPlaylist1);

                    this.scaleAnimation = new ScaleAnimation(this, this.btnDialogActionPlaylist);
                    this.scaleAnimation.Event_Button();
                    // Update Playlist
                    this.btnDialogActionPlaylist.setOnClickListener(v -> {
                        String newPlaylistName = this.etDialogContentPlaylist.getText().toString().trim();
                        if (newPlaylistName.isEmpty()) {
                            Toast.makeText(v.getContext(), R.string.toast12, Toast.LENGTH_SHORT).show();
                        } else {
//                            loadingDialog.Start_Loading();
                            Handle_Add_Update_UserPlaylist(ACTION_UPDATE_PLAYLIST, playlistID, newPlaylistName);
                        }
                    });
                }
            }
        }
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(this, this.ivClose);
        this.scaleAnimation.Event_ImageView();
        this.ivClose.setOnClickListener(v -> {
            finish();
        });
    }

    private void Handle_Add_Update_UserPlaylist(String action, int playlistID, String playlistName) {
        DataService dataService = APIService.getService();
        Call<List<UserPlaylist>> callBack = dataService.addUpdateDeleteUserPlaylist(action, playlistID, DataLocalManager.getUserID(), playlistName);
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>();
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null) {
                    if (action.equals(ACTION_INSERT_PLAYLIST)) { // Thêm một playlist
                        if (userPlaylistArrayList.get(0).getStatus() == 1) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast13, Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (userPlaylistArrayList.get(0).getStatus() == 2) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast14, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayList.get(0).getStatus() == 3) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast15, Toast.LENGTH_SHORT).show();
                        } else {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    } else if (action.equals(ACTION_UPDATE_PLAYLIST)) {// Chỉnh sửa tên một playlist)
                        if (userPlaylistArrayList.get(0).getStatus() == 1) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast16, Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (userPlaylistArrayList.get(0).getStatus() == 2) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast17, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayList.get(0).getStatus() == 3) {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast18, Toast.LENGTH_SHORT).show();
                        } else {
//                            loadingDialog.Cancel_Loading();
                            Toast.makeText(AddUpdateActivity.this, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
//                loadingDialog.Cancel_Loading();
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                loadingDialog.Cancel_Loading();
                Log.d(TAG, "Handle_Add_Update_Delete_DeleteAll_UserPlaylist(Error): " + t.getMessage());
            }
        });
    }
}