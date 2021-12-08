package com.vovmusic.uit.adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.activities.RadioActivity;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.models.UserPlaylist;
import com.vovmusic.uit.services.DownloadService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {
    private static final String TAG = "ChartAdapter";

    private Dialog dialog_1;
    private Dialog dialog_3;

    private ScaleAnimation scaleAnimation;

    int index = 0;

    private ArrayList<UserPlaylist> userPlaylistArrayList;
    private UserPlaylistAdapter userPlaylistAdapter;

    private Context context;
    private ArrayList<Song> songArrayList;

    private boolean isFromRadio = false;
    private List<Integer> listRequest;
    private Dialog dialogRequestFromRadio;
    public static List<Integer> listSongRequested = new ArrayList<>();

    public ChartAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    public ChartAdapter(Context context, ArrayList<Song> songArrayList, boolean o, List<Integer> requests, Dialog dialog) {
        isFromRadio = true;
        this.context = context;
        this.songArrayList = songArrayList;
        listRequest = requests;
        dialogRequestFromRadio = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartAdapter.ViewHolder holder, int position) {
        Picasso.get()
                .load(this.songArrayList.get(position).getImg())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(holder.ivSongChartImage);

        Handle_Position(holder.tvChartNumber, position);
        holder.tvChartSongName.setText(this.songArrayList.get(position).getName().trim());
        holder.tvChartSongSinger.setText(this.songArrayList.get(position).getSinger().trim());
        holder.tvChartLikeNumber.setText(this.songArrayList.get(position).getLike().trim());

        holder.ivChartSongMore.setOnClickListener(v -> Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            Open_Info_Song_Dialog(Gravity.BOTTOM, holder.getLayoutPosition());
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
            intent.putExtra("SONGCHART", songArrayList.get(holder.getLayoutPosition()));
            v.getContext().startActivity(intent);
        });

        if (isFromRadio) {
            Handle_From_Radio(holder, position);
        }
    }

    private void Open_Info_Song_Dialog(int gravity, int position) {
        this.dialog_1 = new Dialog(this.context);

        this.dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog_1.setContentView(R.layout.layout_song_more);

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

        this.dialog_1.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        ImageView ivInfoSongImage = this.dialog_1.findViewById(R.id.ivInfoSongImage);
        Picasso.get()
                .load(this.songArrayList.get(position).getImg())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(ivInfoSongImage);

        TextView tvInfoSongName = this.dialog_1.findViewById(R.id.tvInfoSongName);
        tvInfoSongName.setSelected(true);
        tvInfoSongName.setText(this.songArrayList.get(position).getName().trim());

        TextView tvInfoSongSinger = this.dialog_1.findViewById(R.id.tvInfoSongSinger);
        tvInfoSongSinger.setSelected(true);
        tvInfoSongSinger.setText(this.songArrayList.get(position).getSinger().trim());


        RelativeLayout rlPlaySong = this.dialog_1.findViewById(R.id.rlPlaySong);
        TextView tvPlaySong = this.dialog_1.findViewById(R.id.tvPlaySong);
        tvPlaySong.setSelected(true);

        RelativeLayout rlAddSongToPlaylist = this.dialog_1.findViewById(R.id.rlAddSongToPlaylist);
        TextView tvAddSongToPlaylist = this.dialog_1.findViewById(R.id.tvAddSongToPlaylist);
        tvAddSongToPlaylist.setSelected(true);

        RelativeLayout rlDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteSongToPlaylist);
        TextView tvDeleteSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteSongToPlaylist);
        tvDeleteSongToPlaylist.setSelected(true);

        RelativeLayout rlDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.rlDeleteAllSongToPlaylist);
        TextView tvDeleteAllSongToPlaylist = this.dialog_1.findViewById(R.id.tvDeleteAllSongToPlaylist);
        tvDeleteAllSongToPlaylist.setSelected(true);

        RelativeLayout rlDownLoadSong = this.dialog_1.findViewById(R.id.rlDownLoadSong);
        TextView tvDownLoadSong = this.dialog_1.findViewById(R.id.tvDownLoadSong);
        tvDownLoadSong.setSelected(true);

        RelativeLayout rlDeleteDownLoadSong = this.dialog_1.findViewById(R.id.rlDeleteDownLoadSong);
        TextView tvDeleteDownLoadSong = this.dialog_1.findViewById(R.id.tvDeleteDownLoadSong);
        tvDeleteDownLoadSong.setSelected(true);

        RelativeLayout rlCloseInfoPlaylist = this.dialog_1.findViewById(R.id.rlCloseInfoPlaylist);
        TextView tvCloseInfoSong = this.dialog_1.findViewById(R.id.tvCloseInfoSong);
        tvCloseInfoSong.setSelected(true);


        rlDeleteSongToPlaylist.setVisibility(View.GONE);
        rlDeleteAllSongToPlaylist.setVisibility(View.GONE);
        rlDeleteDownLoadSong.setVisibility(View.GONE);


        this.scaleAnimation = new ScaleAnimation(context, rlPlaySong);
        this.scaleAnimation.Event_RelativeLayout();
        rlPlaySong.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullPlayerActivity.class);
            intent.putExtra("SONG", songArrayList.get(position));
            v.getContext().startActivity(intent);
        });

        this.scaleAnimation = new ScaleAnimation(context, rlAddSongToPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlAddSongToPlaylist.setOnClickListener(v -> {
            Open_Insert_Song_Playlist_Dialog(DataLocalManager.getUserID(), songArrayList.get(position).getId());
        });


        this.scaleAnimation = new ScaleAnimation(context, rlDownLoadSong);
        this.scaleAnimation.Event_RelativeLayout();
        rlDownLoadSong.setOnClickListener(v -> {
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            DownloadService downloadService = new DownloadService();
            downloadService.DownloadSong(context, songArrayList.get(position));
            dialog_1.dismiss();
            notifyDataSetChanged();
        });

        this.scaleAnimation = new ScaleAnimation(context, rlCloseInfoPlaylist);
        this.scaleAnimation.Event_RelativeLayout();
        rlCloseInfoPlaylist.setOnClickListener(v -> dialog_1.dismiss());

        this.dialog_1.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Open_Insert_Song_Playlist_Dialog(String userID, int songID) {
        this.dialog_3 = new Dialog(this.context);

        dialog_3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_3.setContentView(R.layout.layout_add_song_to_playlist_dialog);

        Window window = (Window) dialog_3.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog_3.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        TextView tvSelectPlaylist = dialog_3.findViewById(R.id.tvSelectPlaylist);
        tvSelectPlaylist.setSelected(true);
        ShimmerFrameLayout sflItemUserPlaylist = dialog_3.findViewById(R.id.sflItemUserPlaylist);
        TextView tvEmptyPlaylist = dialog_3.findViewById(R.id.tvEmptyPlaylist);
        tvEmptyPlaylist.setSelected(true);
        RecyclerView rvYourPlaylist = dialog_3.findViewById(R.id.rvYourPlaylist);

        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<UserPlaylist>> callBack = dataService.getUserPlaylist(userID);
        callBack.enqueue(new Callback<List<UserPlaylist>>() {
            @Override
            public void onResponse(Call<List<UserPlaylist>> call, Response<List<UserPlaylist>> response) {
                userPlaylistArrayList = new ArrayList<>();
                userPlaylistArrayList = (ArrayList<UserPlaylist>) response.body();

                if (userPlaylistArrayList != null && userPlaylistArrayList.size() > 0) {
                    rvYourPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvYourPlaylist.setLayoutManager(layoutManager);

                    userPlaylistAdapter = new UserPlaylistAdapter(context, userPlaylistArrayList, songID);
                    rvYourPlaylist.setAdapter(userPlaylistAdapter);

                    sflItemUserPlaylist.setVisibility(View.GONE);
                    rvYourPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin Playlist

                    Log.d(TAG, "User Playlist: " + userPlaylistArrayList.get(0).getName());
                } else {
                    sflItemUserPlaylist.setVisibility(View.GONE);
                    tvEmptyPlaylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserPlaylist>> call, Throwable t) {
                Log.d(TAG, "(Error): " + t.getMessage());
            }
        });

        dialog_3.show();
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Hoàn tất tải bài hát: " + songArrayList.get(index).getName() + "!", Toast.LENGTH_LONG).show();
            context.unregisterReceiver(onComplete);
        }
    };

    private void Handle_Position(TextView tvPosition, int position) {
        tvPosition.setText(String.valueOf(position + 1));

        if (position == 0) {
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain4));
        } else if (position == 1) {
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain7));
        } else if (position == 2) {
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorMain8));
        } else { // Ko biết tại sao, khí nào quay lại tìm hiểu sau
            tvPosition.setTextColor(context.getResources().getColor(R.color.colorLight7));
        }
    }

    private void Handle_From_Radio(ViewHolder view, int position) {
        view.itemView.setEnabled(false);
        view.ivChartSongMore.setImageResource(R.drawable.ic_add_1);
        view.ivChartSongMore.setOnClickListener(v -> {
            RadioActivity.RequestToServer(String.valueOf(songArrayList.get(position).getId()));
            int count = Integer.decode(view.tvChartNumber.getText().toString());
            count++;
            view.tvChartNumber.setText(String.valueOf(count));
            view.ivChartSongMore.setEnabled(false);
            dialogRequestFromRadio.dismiss();
            listSongRequested.add(position);
        });
        if (listRequest.size() <= 0) {
            view.tvChartNumber.setText("0");
        } else {
            view.tvChartNumber.setText(String.valueOf(listRequest.get(position)));
            if (listSongRequested.contains(position)) {
                view.ivChartSongMore.setEnabled(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.songArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvChartNumber;
        private final TextView tvChartSongName;
        private final TextView tvChartSongSinger;
        private final TextView tvChartLikeNumber;
        private final ImageView ivSongChartImage;
        private final ImageView ivChartSongMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvChartNumber = itemView.findViewById(R.id.tvChartNumber);
            this.tvChartNumber.setSelected(true); // Text will be moved

            this.tvChartSongName = itemView.findViewById(R.id.tvChartSongName);
            this.tvChartSongName.setSelected(true); // Text will be moved

            this.tvChartSongSinger = itemView.findViewById(R.id.tvChartSongSinger);
            this.tvChartSongSinger.setSelected(true); // Text will be moved

            this.tvChartLikeNumber = itemView.findViewById(R.id.tvChartLikeNumber);
            this.tvChartLikeNumber.setSelected(true); // Text will be moved

            this.ivSongChartImage = itemView.findViewById(R.id.ivSongChartImage);

            this.ivChartSongMore = itemView.findViewById(R.id.ivChartSongMore);
        }
    }
}
