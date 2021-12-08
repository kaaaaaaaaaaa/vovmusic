package com.vovmusic.uit.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.activities.PersonalPageActivity;
import com.vovmusic.uit.activities.YoutubeActivity;
import com.vovmusic.uit.adapters.CommentSongAdapter;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Comment;
import com.vovmusic.uit.models.Song;
import com.vovmusic.uit.models.Status;
import com.vovmusic.uit.services.DownloadService;
import com.vovmusic.uit.services.FullPlayerManagerService;
import com.vovmusic.uit.services.MiniPlayerOnLockScreenService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullPlayerFragment extends Fragment {
    private static final String TAG = "FullPlayerFragment";

    private MediaPlayer mediaPlayer;

    private ImageView ivCover;
    private ImageView ivFavorite;
    private ImageView ivComment;
    private ImageView ivDownload;
    private ImageView ivMv;
    private ImageView ivShuffle;
    private ImageView ivPrev;
    private ImageView ivPlayPause;
    private ImageView ivNext;
    private ImageView ivRepeat;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private SeekBar sbSong;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;
    private Dialog dialog;

    private List<Comment> commentList;
    private CommentSongAdapter commentSongAdapter;

    private ArrayList<Status> statusArrayList;

    private final String ACTION_INSERT_COMMENT = "insert";

    private int position = 0;
    private boolean repeat = false;
    private boolean checkRandom = false;
    private boolean next = false;

    private boolean isEvent_Of_FullPlayerFragment = false;
    private boolean isRegister = false;

    private ISendPositionListener iSendPositionListener;

    public  TextView tvSongName;
    public static TextView tvArtist;


    public interface ISendPositionListener {
        void Send_Position(int index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

        if (context instanceof ISendPositionListener) {
            iSendPositionListener = (ISendPositionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "You need implement");
        }
//        iSendPositionListener = (ISendPositionListener) getActivity(); // Khở tạo Interface khi Fragment gắn vào Activity
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_full_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        DataLocalManager.init(getContext());

        Mapping(view);
        Event();

        if (!FullPlayerManagerService.isRegister) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACkS"));
            FullPlayerManagerService.isRegister = true;
        }
//        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
            FullPlayerManagerService.isRegister = true;
        } catch (Exception e) {
            Log.e(TAG, "unregisterReceiver");
        }
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

       this.tvSongName = view.findViewById(R.id.tvSongName1);
        this.tvSongName.setSelected(true); // Text will be moved
        this.tvArtist = view.findViewById(R.id.tvArtist1);
        this.tvArtist.setSelected(true); // Text will be moved
        this.loadingDialog = new LoadingDialog(getActivity());
//        this.loadingDialog.Start_Loading();

        this.ivCover = view.findViewById(R.id.ivCover);
        this.ivDownload = view.findViewById(R.id.ivDownload);
//        this.ivFavorite = view.findViewById(R.id.ivFavorite);
        this.ivComment = view.findViewById(R.id.ivComment);
//        this.ivMv = view.findViewById(R.id.ivMv);
        this.ivShuffle = view.findViewById(R.id.ivShuffle);
        this.ivPrev = view.findViewById(R.id.ivPrev);
        this.ivPlayPause = view.findViewById(R.id.ivPlayPause);
        this.ivNext = view.findViewById(R.id.ivNext);
        this.ivRepeat = view.findViewById(R.id.ivRepeat);
        this.tvTimeStart = view.findViewById(R.id.tvTimeStart);
        this.tvTimeEnd = view.findViewById(R.id.tvTimeEnd);
        this.sbSong = view.findViewById(R.id.sbSong);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (FullPlayerManagerService.listCurrentSong != null && FullPlayerActivity.dataSongArrayList.size() <= 0) {
            FullPlayerActivity.dataSongArrayList = new ArrayList<>(FullPlayerManagerService.listCurrentSong);
            position = FullPlayerManagerService.position;
        } else {
            FullPlayerManagerService.position = 0;
        }

        if (FullPlayerActivity.dataSongArrayList.size() > 0) {
            try {
                //FullPlayerManagerService.mediaPlayer.stop();
                if (DownloadService.isSongDownloaded(FullPlayerActivity.dataSongArrayList.get(position).getId())) {
                    Song songDownload = DownloadService.GetDownloadSongById(FullPlayerActivity.dataSongArrayList.get(position).getId());
                    new PlayMP3().execute(songDownload.getLink());
                    FullPlayerManagerService.currentSong = FullPlayerActivity.dataSongArrayList.get(position);
                } else {
                    new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(position).getLink());
                }
            } catch (Exception e) {
                new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(position).getLink());
            }

            this.tvSongName.setText(FullPlayerActivity.dataSongArrayList.get(position).getName().trim());
            this.tvArtist.setText(FullPlayerActivity.dataSongArrayList.get(position).getSinger().trim());

            if (FullPlayerManagerService.mediaPlayer != null && isCurrentSong()) {
                if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
                    this.ivPlayPause.setImageResource(R.drawable.ic_pause);
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                } else {
                    this.ivPlayPause.setImageResource(R.drawable.ic_play_2);
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
                }
            } else {
                this.ivPlayPause.setImageResource(R.drawable.ic_pause);
                CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FullPlayerActivity.dataSongArrayList.size() > 0) {
                        Picasso.get()
                                .load(FullPlayerActivity.dataSongArrayList.get(position).getImg())
                                .placeholder(R.drawable.logovov)
                                .error(R.drawable.logovov)
                                .into(ivCover);
                    } else {
                        handler.postDelayed(this, 1000);
                    }
                }
            }, 1000);
//            this.loadingDialog.Cancel_Loading();
        }
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivDownload);
        this.scaleAnimation.Event_ImageView();
//        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivFavorite);
//        this.scaleAnimation.Event_ImageView();

        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivComment);
        this.scaleAnimation.Event_ImageView();
        this.ivComment.setOnClickListener(v -> {
            Open_Comment_Dialog(position);
        });

//        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivMv);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivShuffle);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPrev);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPlayPause);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivNext);
        this.scaleAnimation.Event_ImageView();
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivRepeat);
        this.scaleAnimation.Event_ImageView();

        this.ivPlayPause.setOnClickListener(v -> {
            onSongPlay();
        });


        this.ivRepeat.setOnClickListener(v -> {
            isEvent_Of_FullPlayerFragment = true;
            if (!repeat) {
                if (checkRandom) {
                    checkRandom = false;
                    FullPlayerManagerService.checkRandom = false;
                    this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle);
                    repeat = true;
                    FullPlayerManagerService.repeat = true;
                } else {
                    this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    repeat = true;
                    FullPlayerManagerService.repeat = true;
                }
            } else {
                this.ivRepeat.setImageResource(R.drawable.ic_loop);
                repeat = false;
                FullPlayerManagerService.repeat = false;
            }
        });


        this.ivShuffle.setOnClickListener(v -> {
            isEvent_Of_FullPlayerFragment = true;
            if (!checkRandom) {
                if (repeat) {
                    repeat = false;
                    FullPlayerManagerService.repeat = false;
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    this.ivRepeat.setImageResource(R.drawable.ic_loop);
                    checkRandom = true;
                    FullPlayerManagerService.checkRandom = true;
                } else {
                    this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    checkRandom = true;
                    FullPlayerManagerService.checkRandom = true;
                }
            } else {
                this.ivShuffle.setImageResource(R.drawable.ic_shuffle);
                checkRandom = false;
                FullPlayerManagerService.checkRandom = false;
            }
        });


        this.sbSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //mediaPlayer.seekTo(seekBar.getProgress());
                FullPlayerManagerService.mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        this.ivNext.setOnClickListener(v -> {
            onSongNext();
        });


        this.ivPrev.setOnClickListener(v -> {
            onSongPrev();
        });

        this.ivDownload.setOnClickListener(v -> {
            if (FullPlayerActivity.dataSongArrayList.size() > 0) {
                DownloadService.DownloadSong(getContext(), FullPlayerActivity.dataSongArrayList.get(position));
            }
        });

//        this.ivMv.setOnClickListener(v -> {
//            if (FullPlayerActivity.dataSongArrayList.get(position).getMvcode() != null) {
//                Intent intent = new Intent(getActivity(), YoutubeActivity.class);
//                intent.putExtra("MvCode", FullPlayerActivity.dataSongArrayList.get(position).getMvcode().trim());
//                intent.putExtra("Artist", FullPlayerActivity.dataSongArrayList.get(position).getSinger().trim());
//                intent.putExtra("SongName", FullPlayerActivity.dataSongArrayList.get(position).getName().trim());
//                startActivity(intent);
//            } else {
//                Toast.makeText(v.getContext(), R.string.toast32, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void CreateNotification(String action) {
        Intent intent = new Intent(getContext(), MiniPlayerOnLockScreenService.class);
        intent.setAction(action);
        getActivity().startService(intent);
        //NotificationService.NotificationService(getContext(),FullPlayerActivity.dataSongArrayList.get(position),R.drawable.ic_pause,position,FullPlayerActivity.dataSongArrayList.size());
    }

    public void onSongPlay() {
        isEvent_Of_FullPlayerFragment = true;
        if (FullPlayerManagerService.mediaPlayer.isPlaying()) {
            FullPlayerManagerService.mediaPlayer.pause();
            this.ivPlayPause.setImageResource(R.drawable.ic_play_2);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
        } else {
            FullPlayerManagerService.mediaPlayer.start();
            this.ivPlayPause.setImageResource(R.drawable.ic_pause);
            CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
        }
    }

    public void onSongNext() {
        isEvent_Of_FullPlayerFragment = true;
        if (FullPlayerActivity.dataSongArrayList.size() > 0) {
            if (FullPlayerManagerService.mediaPlayer.isPlaying() || FullPlayerManagerService.mediaPlayer != null) {
                FullPlayerManagerService.mediaPlayer.stop();
                FullPlayerManagerService.mediaPlayer.release(); // Đồng bộ
                FullPlayerManagerService.mediaPlayer = null;
            }
            if (this.position < FullPlayerActivity.dataSongArrayList.size()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause);
                this.position++;

                if (repeat) {
                    if (this.position == 0) {
                        this.position = FullPlayerActivity.dataSongArrayList.size();
                    }
                    this.position -= 1;
                }

                if (checkRandom) {
                    Random random = new Random();
                    int index = random.nextInt(FullPlayerActivity.dataSongArrayList.size());
                    if (index == this.position) {
                        this.position = index - 1;
                    }
                    this.position = index;
                }
                if (this.position > FullPlayerActivity.dataSongArrayList.size() - 1) {
                    this.position = 0;
                }
            }
            new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(this.position).getLink());

            Picasso.get()
                    .load(FullPlayerActivity.dataSongArrayList.get(this.position).getImg())
                    .placeholder(R.drawable.logovov)
                    .error(R.drawable.logovov)
                    .into(this.ivCover);

            FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongArrayList.get(this.position).getName());
            FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongArrayList.get(this.position).getSinger());
            iSendPositionListener.Send_Position(this.position); // Chú ý
            UpdateTimeSong();
        }
        this.ivNext.setClickable(false);
        this.ivPrev.setClickable(false);

        new Handler().postDelayed(() -> {
            this.ivNext.setClickable(true);
            this.ivPrev.setClickable(true);
        }, 2000);
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }

    public void onSongPrev() {
        isEvent_Of_FullPlayerFragment = true;
        if (FullPlayerActivity.dataSongArrayList.size() > 0) {
            if (FullPlayerManagerService.mediaPlayer.isPlaying() || FullPlayerManagerService.mediaPlayer != null) {
                FullPlayerManagerService.mediaPlayer.stop();
                FullPlayerManagerService.mediaPlayer.release(); // Đồng bộ
                FullPlayerManagerService.mediaPlayer = null;
            }
            if (this.position < FullPlayerActivity.dataSongArrayList.size()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause);
                this.position--;

                if (this.position < 0) {
                    this.position = FullPlayerActivity.dataSongArrayList.size() - 1;
                }

                if (repeat) {
                    this.position += 1;
                }

                if (checkRandom) {
                    Random random = new Random();
                    int index = random.nextInt(FullPlayerActivity.dataSongArrayList.size());
                    if (index == this.position) {
                        this.position = index - 1;
                    }
                    this.position = index;
                }
            }
            new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(this.position).getLink());

            Picasso.get()
                    .load(FullPlayerActivity.dataSongArrayList.get(this.position).getImg())
                    .placeholder(R.drawable.logovov)
                    .error(R.drawable.logovov)
                    .into(this.ivCover);
            BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
//            FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongArrayList.get(this.position).getName());
//            FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongArrayList.get(this.position).getSinger());
            iSendPositionListener.Send_Position(this.position); // Chú ý
            UpdateTimeSong();
        }
        this.ivNext.setClickable(false);
        this.ivPrev.setClickable(false);

        new Handler().postDelayed(() -> {
            this.ivNext.setClickable(true);
            this.ivPrev.setClickable(true);
        }, 2000);
        CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
    }


    private void Open_Comment_Dialog(int position) {
        this.dialog = new Dialog(getContext());
        DataLocalManager.init(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_comment_song);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog


        // Ánh xạ các view trong dialog
        ImageView ivInfoSongImageComment = dialog.findViewById(R.id.ivInfoSongImageComment);
        Picasso.get()
                .load(FullPlayerActivity.dataSongArrayList.get(position).getImg())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(ivInfoSongImageComment);

        TextView tvInfoSongNameComment = dialog.findViewById(R.id.tvInfoSongNameComment);
        tvInfoSongNameComment.setSelected(true); // Text will be moved
        tvInfoSongNameComment.setText(String.valueOf(FullPlayerActivity.dataSongArrayList.get(position).getName()));

        TextView tvInfoSongSingerComment = dialog.findViewById(R.id.tvInfoSongSingerComment);
        tvInfoSongSingerComment.setSelected(true); // Text will be moved
        tvInfoSongSingerComment.setText(String.valueOf(FullPlayerActivity.dataSongArrayList.get(position).getSinger()));

        ImageView ivIconComment = dialog.findViewById(R.id.ivIconComment);

        TextView tvNullComment = dialog.findViewById(R.id.tvNullComment);
        tvNullComment.setSelected(true);

        ImageView ivClose = dialog.findViewById(R.id.ivClose);
        ShimmerFrameLayout sflItemComment = dialog.findViewById(R.id.sflItemComment);
        RecyclerView rvComment = dialog.findViewById(R.id.rvComment);

        CircleImageView civYourAvatarComment = dialog.findViewById(R.id.civYourAvatarComment);
        Picasso.get()
                .load(DataLocalManager.getUserAvatar())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(civYourAvatarComment);

        EditText etInputComment = dialog.findViewById(R.id.etInputComment);
        etInputComment.requestFocus();
        this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ImageView ivSend = dialog.findViewById(R.id.ivSend);

        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Comment>> callBack = dataService.getCommentSong(FullPlayerActivity.dataSongArrayList.get(position).getId());
        callBack.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                commentList = new ArrayList<>();
                commentList = response.body();

                if (commentList != null && commentList.size() > 0) {
                    rvComment.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvComment.setLayoutManager(layoutManager);

                    commentSongAdapter = new CommentSongAdapter(getContext(), commentList);
                    rvComment.setAdapter(commentSongAdapter);

                    sflItemComment.setVisibility(View.GONE);
                    rvComment.setVisibility(View.VISIBLE); // Hiện thông tin Playlist

                    Log.d(TAG, "User Playlist: " + commentList.get(0).getContent());
                } else {
                    sflItemComment.setVisibility(View.GONE);
                    ivIconComment.setVisibility(View.VISIBLE);
                    tvNullComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.d(TAG, "Handle_Comment(Error): " + t.getMessage());
            }
        });

        this.scaleAnimation = new ScaleAnimation(getContext(), ivClose);
        this.scaleAnimation.Event_ImageView();
        ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(getContext(), civYourAvatarComment);
        this.scaleAnimation.Event_CircleImageView();
        civYourAvatarComment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalPageActivity.class);
            startActivity(intent);
        });

        this.scaleAnimation = new ScaleAnimation(getContext(), ivSend);
        this.scaleAnimation.Event_ImageView();
        ivSend.setOnClickListener(v -> {
            String content = etInputComment.getText().toString().trim();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = dateFormat.format(date);

            if (content.isEmpty()) {
                Toast.makeText(v.getContext(), R.string.toast12, Toast.LENGTH_SHORT).show();
            } else if (content.length() > 200) {
                Toast.makeText(v.getContext(), R.string.toast27, Toast.LENGTH_SHORT).show();
            } else {
//                loadingDialog.Start_Loading();
                Handle_Add_Comment(ACTION_INSERT_COMMENT, 0, FullPlayerActivity.dataSongArrayList.get(position).getId(), DataLocalManager.getUserID(), content, time);
            }
        });

        dialog.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handle_Add_Comment(String action, int commentID, int songID, String userID, String content, String date) {
        DataService dataService = APIService.getService();
        Call<List<Status>> callBack = dataService.addUpdateDeleteCommentSong(action, commentID, songID, userID, content, date);
        callBack.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                statusArrayList = new ArrayList<>();
                statusArrayList = (ArrayList<Status>) response.body();

                if (statusArrayList != null) {
                    if (statusArrayList.get(0).getStatus() == 1) {
//                        loadingDialog.Cancel_Loading();

                        Toast.makeText(getContext(), R.string.toast28, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (statusArrayList.get(0).getStatus() == 2) {
//                        loadingDialog.Cancel_Loading();

                        Toast.makeText(getContext(), R.string.toast27, Toast.LENGTH_SHORT).show();
                    } else {
//                        loadingDialog.Cancel_Loading();

                        Toast.makeText(getContext(), R.string.toast11, Toast.LENGTH_SHORT).show();
                    }
                }
//                loadingDialog.Cancel_Loading();
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                Log.d(TAG, "Handle_Add_Comment(Error): " + t.getMessage());
            }
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
                if (!isCurrentSong() || isEvent_Of_FullPlayerFragment) {

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
                    FullPlayerManagerService.position = position;
                    //Log.d("CurrentSong",FullPlayerManagerService.currentSong.getName());
                    FullPlayerManagerService.mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                    FullPlayerManagerService.mediaPlayer.prepare();
                    FullPlayerManagerService.mediaPlayer.start();
                    mediaPlayer = FullPlayerManagerService.mediaPlayer;
                    CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    FullPlayerManagerService.listCurrentSong = new ArrayList<Song>(FullPlayerActivity.dataSongArrayList);
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Lỗi. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            TimeSong();
            UpdateTimeSong();
        }
    }

    private boolean isCurrentSong() {
        try {
            if (FullPlayerManagerService.listCurrentSong != null) {
                if (FullPlayerManagerService.listCurrentSong.get(position).getId() == FullPlayerActivity.dataSongArrayList.get(position).getId()) {
                    repeat = FullPlayerManagerService.repeat;
                    checkRandom = FullPlayerManagerService.checkRandom;
                    if (repeat) {
                        this.ivRepeat.setImageResource(R.drawable.ic_loop_check);
                    }
                    if (checkRandom) {
                        this.ivShuffle.setImageResource(R.drawable.ic_shuffle_check);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        FullPlayerManagerService.repeat = false;
        FullPlayerManagerService.checkRandom = false;
        return false;
    }

    private void TimeSong() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

        this.tvTimeEnd.setText(simpleDateFormat.format(FullPlayerManagerService.mediaPlayer.getDuration()));
        this.sbSong.setMax(FullPlayerManagerService.mediaPlayer.getDuration());
    }

    private void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FullPlayerManagerService.mediaPlayer != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
                    try {
                        //tvTimeStart.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                        //sbSong.setProgress(mediaPlayer.getCurrentPosition());
                        int time = FullPlayerManagerService.mediaPlayer.getCurrentPosition();
                        Log.d("Test", String.valueOf(time));
                        tvTimeStart.setText(simpleDateFormat.format(FullPlayerManagerService.mediaPlayer.getCurrentPosition()));
                        sbSong.setProgress(FullPlayerManagerService.mediaPlayer.getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this, 1000); // Gọi lại ham này thực thi 1s mỗi lần
/*                    mediaPlayer.setOnCompletionListener(mp -> {
                        next = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });*/
                    FullPlayerManagerService.mediaPlayer.setOnCompletionListener(mp -> {
                        next = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }, 1000);


        Handler handler_1 = new Handler(); // Lằng nghe khi chuyển bài hát
        handler_1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (next) {
                    if (position < FullPlayerActivity.dataSongArrayList.size()) {
                        ivPlayPause.setImageResource(R.drawable.ic_pause);
                        position++;

                        if (repeat) {
                            if (position == 0) {
                                position = FullPlayerActivity.dataSongArrayList.size();
                            }
                            position -= 1;
                        }

                        if (checkRandom) {
                            Random random = new Random();
                            int index = random.nextInt(FullPlayerActivity.dataSongArrayList.size());
                            if (index == position) {
                                position = index - 1;
                            }
                            position = index;
                        }
                        if (position > FullPlayerActivity.dataSongArrayList.size() - 1) {
                            position = 0;
                        }
                    }
                    new PlayMP3().execute(FullPlayerActivity.dataSongArrayList.get(position).getLink());

                    Picasso.get()
                            .load(FullPlayerActivity.dataSongArrayList.get(position).getImg())
                            .placeholder(R.drawable.logovov)
                            .error(R.drawable.logovov)
                            .into(ivCover);

//                    FullPlayerActivity.tvSongName.setText(FullPlayerActivity.dataSongArrayList.get(position).getName());
//                    FullPlayerActivity.tvArtist.setText(FullPlayerActivity.dataSongArrayList.get(position).getSinger());
                    iSendPositionListener.Send_Position(position); // Chú ý
                    ivNext.setClickable(false);
                    ivPrev.setClickable(false);

                    new Handler().postDelayed(() -> { // Sau khi người dùng nhấn Next hoặc Prev thì cho dừng khoảng 2s sau mới cho tác động lại nút
                        ivNext.setClickable(true);
                        ivPrev.setClickable(true);
                    }, 2000);

                    next = false;
                    handler_1.removeCallbacks(this); // Lưu ý khúc này
                } else {
                    handler_1.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case MiniPlayerOnLockScreenService.ACTION_PLAY:
                    onSongPlay();
                    break;
                case MiniPlayerOnLockScreenService.ACTION_PAUSE:
                    onSongPlay();
                    break;
                case MiniPlayerOnLockScreenService.ACTION_PRE:
                    try {
                        onSongPrev();
                        //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MiniPlayerOnLockScreenService.ACTION_NEXT:

                    try {
                        onSongNext();
                        //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}