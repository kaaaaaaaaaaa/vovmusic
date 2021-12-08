package com.vovmusic.uit.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.activities.AddUpdateActivity;
import com.vovmusic.uit.activities.PersonalPlaylistActivity;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Status;
import com.vovmusic.uit.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.ViewHolder> {
    private static final String TAG = "UserPlaylistAdapter";

    private Dialog dialog_1;
    private Dialog dialog_2;

    private Context context;
    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private int songID = -1;

    private ArrayList<UserPlaylist> userPlaylistArrayLists; // Dữ liệu API trả về
    private ArrayList<Status> statusArrayList;

    private TextView tvNumberPlaylist;

    private ScaleAnimation scaleAnimation;
    private AlertDialog alertDialog;

    private final String ACTION_DELETE_PLAYLIST = "delete";
    private final String ACTION_DELETEALL_PLAYLIST = "deleteall";

    private final String ACTION_INSERT_SONG_PLAYLIST = "insert";

    public UserPlaylistAdapter(Context context, ArrayList<UserPlaylist> userPlaylistArrayList, TextView tvNumberPlaylist) {
        this.context = context;
        this.userPlaylistArrayList = userPlaylistArrayList;
        this.tvNumberPlaylist = tvNumberPlaylist;
    }

    public UserPlaylistAdapter(Context context, ArrayList<UserPlaylist> userPlaylistArrayList, int songID) {
        this.context = context;
        this.userPlaylistArrayList = userPlaylistArrayList;
        this.songID = songID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPlaylistAdapter.ViewHolder holder, int position) {
        DataLocalManager.init(context);

        holder.tvPlaylistName.setText(this.userPlaylistArrayList.get(position).getName().trim());
        holder.tvNumberSongPlaylist.setText(String.valueOf(this.userPlaylistArrayList.get(position).getTotalSong()));
        holder.ivPlaylistMore.setOnClickListener(v -> Open_Info_Playlist_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));

        if (this.songID > -1) {
            holder.itemView.setOnClickListener(v -> {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.WrapContentDialog);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
                alertBuilder.setView(view);
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();

                Handle_Add_Song_Playlist(ACTION_INSERT_SONG_PLAYLIST, DataLocalManager.getUserID(), this.userPlaylistArrayList.get(holder.getLayoutPosition()).getYouID(), songID, holder.getLayoutPosition());
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PersonalPlaylistActivity.class);
                intent.putExtra("SONGPLAYLIST", this.userPlaylistArrayList.get(holder.getLayoutPosition()));
                context.startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                Open_Info_Playlist_Dialog(Gravity.BOTTOM, holder.getLayoutPosition());
                return false;
            });
        }
    }

    private void Open_Info_Playlist_Dialog(int gravity, int position) {
        this.dialog_1 = new Dialog(this.context);

        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_1.setContentView(R.layout.layout_userplaylist_more);

        Window window = dialog_1.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần activity

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_1.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        TextView tvInfoPlaylistName = dialog_1.findViewById(R.id.tvInfoPlaylistName);
        tvInfoPlaylistName.setSelected(true); // Text will be moved
        tvInfoPlaylistName.setText(String.valueOf(userPlaylistArrayList.get(position).getName()));

//        RelativeLayout rlAddPlaylist = dialog.findViewById(R.id.rlAddPlaylist);
        RelativeLayout rlEditPlaylist = dialog_1.findViewById(R.id.rlEditPlaylist);
        TextView tvEditPlaylist = dialog_1.findViewById(R.id.tvEditPlaylist);
        tvEditPlaylist.setSelected(true);

        RelativeLayout rlDeletePlaylist = dialog_1.findViewById(R.id.rlDeletePlaylist);
        TextView tvDeletePlaylist = dialog_1.findViewById(R.id.tvDeletePlaylist);
        tvDeletePlaylist.setSelected(true);

        RelativeLayout rlDeleteAllPlaylist = dialog_1.findViewById(R.id.rlDeleteAllPlaylist);
        TextView tvDeleteAllPlaylist = dialog_1.findViewById(R.id.tvDeleteAllPlaylist);
        tvDeleteAllPlaylist.setSelected(true);

        RelativeLayout rlCloseInfoPlaylist = dialog_1.findViewById(R.id.rlCloseInfoPlaylist);
        TextView tvCloseInfoPlaylist = dialog_1.findViewById(R.id.tvCloseInfoPlaylist);
        tvCloseInfoPlaylist.setSelected(true);

//        this.scaleAnimation = new ScaleAnimation(context, rlAddPlaylist);
//        this.scaleAnimation.Event_RelativeLayout();

        if (songID > -1) {
            rlEditPlaylist.setVisibility(View.GONE);
        }

        this.scaleAnimation = new ScaleAnimation(context, rlEditPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlEditPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddUpdateActivity.class);
            intent.putExtra("UPDATEPLAYLIST", this.userPlaylistArrayList.get(position));
            context.startActivity(intent);

            dialog_1.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(context, rlDeletePlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeletePlaylist.setOnClickListener(v -> { // Xóa một playlist
            Open_Delete_Playlist_Dialog(ACTION_DELETE_PLAYLIST, userPlaylistArrayList.get(position).getYouID(), DataLocalManager.getUserID(), userPlaylistArrayList.get(position).getName(), position);
        });

        this.scaleAnimation = new ScaleAnimation(context, rlDeleteAllPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlDeleteAllPlaylist.setOnClickListener(v -> { // Xóa toàn bộ playlist
            Open_Delete_Playlist_Dialog(ACTION_DELETEALL_PLAYLIST, userPlaylistArrayList.get(position).getYouID(), DataLocalManager.getUserID(), userPlaylistArrayList.get(position).getName(), position);
        });

        this.scaleAnimation = new ScaleAnimation(context, rlCloseInfoPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlCloseInfoPlaylist.setOnClickListener(v -> {
            dialog_1.dismiss();
        });

        dialog_1.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Open_Delete_Playlist_Dialog(String action, int playlistID, String userID, String playlistName, int position) {
        this.dialog_2 = new Dialog(this.context);

        dialog_2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_2.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog_2.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_2.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        // Ánh xạ các view trong dialog
        TextView tvDialogTitle = dialog_2.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setSelected(true); // Text will be moved
        TextView tvDialogContent = dialog_2.findViewById(R.id.tvDialogContent);
        Button btnDialogCancel = dialog_2.findViewById(R.id.btnDialogCancel);
        Button btnDialogAction = dialog_2.findViewById(R.id.btnDialogAction);

        // Xét xem người dùng nhấn chức năng xóa hay xóa toàn bộ thì setText tương ứng
        if (action.equals(ACTION_DELETE_PLAYLIST)) {
            tvDialogTitle.setText(R.string.tvDialogTitle2);
            tvDialogContent.setText(R.string.tvDialogContent2);
            btnDialogCancel.setText(R.string.btnDialogCancel2);
            btnDialogAction.setText(R.string.btnDialogAction2);
        } else if (action.equals(ACTION_DELETEALL_PLAYLIST)) {
            tvDialogTitle.setText(R.string.tvDialogTitle3);
            tvDialogContent.setText(R.string.tvDialogContent3);
            btnDialogCancel.setText(R.string.btnDialogCancel3);
            btnDialogAction.setText(R.string.btnDialogAction3);
        }

        this.scaleAnimation = new ScaleAnimation(context, btnDialogCancel);
        this.scaleAnimation.Event_Button();
        btnDialogCancel.setOnClickListener(v -> {
            dialog_2.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(context, btnDialogAction);
        this.scaleAnimation.Event_Button();
        btnDialogAction.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, R.style.WrapContentDialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
            alertBuilder.setView(view);
            alertBuilder.setCancelable(false);
            this.alertDialog = alertBuilder.create();
            this.alertDialog.show();

            Handle_Add_Update_Delete_DeleteAll_UserPlaylist(action, playlistID, userID, playlistName, position);
        });

        dialog_2.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handle_Add_Update_Delete_DeleteAll_UserPlaylist(String action, int playlistID, String userID, String playlistName, int position) {
        DataService dataService = APIService.getService();
        Call<List<UserPlaylist>> callBack = dataService.addUpdateDeleteUserPlaylist(action, playlistID, userID, playlistName);
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayLists = new ArrayList<>();
                userPlaylistArrayLists = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayLists != null) {
                    if (action.equals(ACTION_DELETE_PLAYLIST)) { // Xóa một playlist
                        if (userPlaylistArrayLists.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.remove(position);
                            notifyItemRemoved(position);
//                            notifyDataSetChanged();

                            if (tvNumberPlaylist != null) {
                                tvNumberPlaylist.setText(String.valueOf(userPlaylistArrayList.size()));
                            }
                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast19, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayLists.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast20, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    } else if (action.equals(ACTION_DELETEALL_PLAYLIST)) { // Xóa toàn bộ playlist
                        if (userPlaylistArrayLists.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.clear();
                            notifyDataSetChanged();
                            if (tvNumberPlaylist != null) {
                                tvNumberPlaylist.setText(String.valueOf(userPlaylistArrayList.size()));
                            }
                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast19, Toast.LENGTH_SHORT).show();
                        } else if (userPlaylistArrayLists.get(0).getStatus() == 2) {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast20, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            dialog_2.dismiss();
                            dialog_1.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                alertDialog.dismiss();

                dialog_2.dismiss();
                dialog_1.dismiss();
                Log.d(TAG, "Handle_Add_Update_Delete_DeleteAll_UserPlaylist(Error): " + t.getMessage());
            }
        });
    }

    private void Handle_Add_Song_Playlist(String action, String userID, int playlistID, int songID, int position) {
        DataService dataService = APIService.getService();
        Call<List<Status>> callBack = dataService.addDeleteUserPlayListSong(action, userID, playlistID, songID);
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();

                if (statusArrayList != null) {
                    if (action.equals(ACTION_INSERT_SONG_PLAYLIST)) {
                        if (statusArrayList.get(0).getStatus() == 1) {
                            alertDialog.dismiss();

                            userPlaylistArrayList.remove(position);
                            notifyItemRemoved(position);

                            Toast.makeText(context, R.string.toast23, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 2) {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast24, Toast.LENGTH_SHORT).show();
                        } else if (statusArrayList.get(0).getStatus() == 3) {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast25, Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, R.string.toast11, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                alertDialog.dismiss();
                Log.d(TAG, "Handle_Add_Song_Playlist(Error): " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.userPlaylistArrayList != null) {
            return this.userPlaylistArrayList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cvPlaylistCover;
        private TextView tvPlaylistName;
        private TextView tvNumberSongPlaylist;
        private ImageView ivPlaylistMore;

        private ScaleAnimation scaleAnimation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cvPlaylistCover = itemView.findViewById(R.id.cvPlaylistCover);

            this.tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            this.tvPlaylistName.setSelected(true);

            this.tvNumberSongPlaylist = itemView.findViewById(R.id.tvNumberSongPlaylist);

            this.ivPlaylistMore = itemView.findViewById(R.id.ivPlaylistMore);
            this.scaleAnimation = new ScaleAnimation(itemView.getContext(), this.ivPlaylistMore);
            this.scaleAnimation.Event_ImageView();
        }
    }
}