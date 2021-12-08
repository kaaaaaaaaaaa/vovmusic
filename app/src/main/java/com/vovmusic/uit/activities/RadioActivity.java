package com.vovmusic.uit.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.adapters.ChartAdapter;
import com.vovmusic.uit.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RadioActivity extends AppCompat {
    Button btn;
    TextView roomName;
    private TextView tvListItemSongName;
    private TextView tvListItemSongSinger;
    private TextView tvRoomName;

    private ImageView ivListItemSongLove;
    private ImageView ivRequest;
    private ImageView ivPlay;
    private ImageView ivListItemSong;
    ArrayList<Song> userPlaylistArrayList = null;
    List<Integer> listRequest = new ArrayList<>();
    public static int room;
    Gson gs = new Gson();
    private int timeCurrent = 0;
    public static String userid = "usercurrent" + DataLocalManager.getUserID();
    private static MediaPlayer mediaPlayer;
    private static final String CHAR_LIST = "0123456789";
    int session = new Random().nextInt(100) + 1;

    private static final int RANDOM_STRING_LENGTH = 15;
    public static Socket mSocket;
    private ImageView ivBack;
    private Dialog dialogRequest;
    private ArrayList<Song> songArrayList;
    private RecyclerView rvChart;

    private ShimmerFrameLayout sflItemSong;
    private TextView tvChart;
    private boolean isContinute = true;

    {
        try {
            mSocket = IO.socket("https://radio-server-uit.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        room = getIntent().getIntExtra("room", 1);
        mSocket.connect();
        mSocket.emit("joinroom", gs.toJson(new Room(room, userid)));
        mSocket.on("currentsong", onNewMessage2);
        mSocket.on("getallsonginroom" + userid, getAllDataSong);
        mSocket.on(userid, onNewMessage);
        mSocket.on("updaterequest", updateRequest);
        Log.d("Radio", userid + session);

        Mapping();
        Event();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mSocket.emit("forceDisconnect", userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Room {
        int room;
        String userid;

        public Room(int r, String id) {
            room = r;
            userid = id;
        }
    }

    private void Mapping() {
        ivListItemSong = findViewById(R.id.ivListItemSong);
        ivPlay = findViewById(R.id.ivPlayRadio);
        ivListItemSongLove = findViewById(R.id.ivLove);
        ivListItemSongLove.setVisibility(View.GONE);
        tvListItemSongName = findViewById(R.id.tvListItemSongName);
        tvListItemSongSinger = findViewById(R.id.tvListItemSongSinger);
        ivRequest = findViewById(R.id.ivRequest);
        //ivRequest.setVisibility(View.INVISIBLE);
        ivBack = findViewById(R.id.ivBack);
        this.tvRoomName = findViewById(R.id.tvRoomName);
        this.tvRoomName.setSelected(true); // Text will be

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("ROOMNAME1")) {
                String roomName1 = intent.getStringExtra("ROOMNAME1");
                this.tvRoomName.setText(roomName1.trim());
            } else if (intent.hasExtra("ROOMNAME2")) {
                String roomName2 = intent.getStringExtra("ROOMNAME2");
                this.tvRoomName.setText(roomName2.trim());
            }
        }
    }

    private void Event() {
        ivPlay.setOnClickListener(v -> {
            onSongPlay();
        });

        ivBack.setOnClickListener(v -> {
            room = -1;
            finish();
        });

        ivRequest.setOnClickListener(v -> {
            Open_Request_Dialog();
        });
    }

    public static void RequestToServer(String id) {
        mSocket.emit("requestfromapp", new Gson().toJson(new Room(room, id)));
    }

    private void onSongPlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            this.ivPlay.setImageResource(R.drawable.ic_play_2);
            //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PAUSE);
        } else {
            mSocket.emit("joinroom", gs.toJson(new Room(room, userid)));
            this.ivPlay.setImageResource(R.drawable.ic_pause);
            //CreateNotification(MiniPlayerOnLockScreenService.ACTION_PLAY);
        }
    }

    private void Open_Request_Dialog() {
        this.dialogRequest = new Dialog(this);

        dialogRequest.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRequest.setContentView(R.layout.fragment_chart);

        Window window = (Window) dialogRequest.getWindow();
        if (window == null) {
            return;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, height / 2 + height / 3);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialogRequest.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog
        //TextView tvSelectPlaylist = dialogRequest.findViewById(R.id.tvSelectPlaylist);
//        tvSelectPlaylist.setSelected(true);
        //ShimmerFrameLayout sflItemUserPlaylist = dialogRequest.findViewById(R.id.sflItemUserPlaylist);
        //TextView tvEmptyPlaylist = dialogRequest.findViewById(R.id.tvEmptyPlaylist);
//        tvEmptyPlaylist.setSelected(true);
        //RecyclerView rvYourPlaylist = dialogRequest.findViewById(R.id.rvYourPlaylist);

        Handle_SongRequest(dialogRequest);

        dialogRequest.show();
    }

    private void Handle_SongRequest(Dialog dialog) {
        this.rvChart = dialog.findViewById(R.id.rvChart);
        this.sflItemSong = dialog.findViewById(R.id.sflItemSong);
        this.tvChart = dialog.findViewById(R.id.tvChart);
        tvChart.setVisibility(View.GONE);
        if (songArrayList != null && songArrayList.size() > 0) {
            sflItemSong.setVisibility(View.GONE); // Load biến mất
            rvChart.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
            rvChart.setLayoutManager(layoutManager);
            rvChart.setAdapter(new ChartAdapter(dialog.getContext(), songArrayList, true, listRequest, dialog));
            rvChart.setVisibility(View.VISIBLE); // Hiện thông tin
        }
/*        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack =  dataService.getSongLove();
        if(room == 1){
            callBack = dataService.getSongLove();
        }else if(room ==2){
            callBack = dataService.getSongRandom();
        }
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    sflItemSong.setVisibility(View.GONE); // Load biến mất

                    rvChart.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvChart.setLayoutManager(layoutManager);
                    rvChart.setAdapter(new ChartAdapter(dialog.getContext(), songArrayList));
                    rvChart.setVisibility(View.VISIBLE); // Hiện thông tin
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                t.printStackTrace();
            }
        });*/
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
                try {
                    if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release(); // Đồng bộ
                        mediaPlayer = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });
                //Log.d("CurrentSong",FullPlayerManagerService.currentSong.getName());
                mediaPlayer.setDataSource(song); // Cái này quan trọng nè Thắng
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.seekTo(timeCurrent * 1000);
                Log.i("myactivity", song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //byte[] audio;
                    Song song = null;
                    try {
                        //audio = (byte[]) data.get("audio");
                        if (data.getString("userid").equalsIgnoreCase(userid) && data.getInt("room") == room && isContinute) {
                            isContinute = false;
                            timeCurrent += 5;
                            //RunMp31(song.getLink(),time+3);
                            new PlayMP3().execute(data.getString("link"));
                            tvListItemSongName.setText(data.getString("name"));
                            tvListItemSongSinger.setText(data.getString("singer"));
                            timeCurrent = data.getInt("timecurrent");
                            String link = data.getString("image");
                            Picasso.get()
                                    .load(link)
                                    .placeholder(R.drawable.ic_logo)
                                    .error(R.drawable.ic_logo)
                                    .into(ivListItemSong);
                        }
                        new Handler().postDelayed(() -> { // Sau khi người dùng nhấn Next hoặc Prev thì cho dừng khoảng 2s sau mới cho tác động lại nút
                            isContinute = true;
                        }, 1000);
                       /* if(aaa == 1){
                            mSocket.emit("server-gui-request",mediaPlayerRoom1.getCurrentPosition());
                        }*/
/*                         if(aaa == 2){
                            mSocket.emit("server-gui-request",mediaPlayerRoom2.getCurrentPosition());
                        }*/
                        //playMp3FromByte(audio);
                    } catch (JSONException e) {
                        Log.d("Error", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener onNewMessage2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //byte[] audio;
                    Song song = null;
                    try {
                        //audio = (byte[]) data.get("audio");
                        if (data.getInt("room") == room) {
                            timeCurrent += 5;
                            //RunMp31(song.getLink(),time+3);
                            new PlayMP3().execute(data.getString("link"));
                            tvListItemSongName.setText(data.getString("name"));
                            tvListItemSongSinger.setText(data.getString("singer"));
                            timeCurrent = data.getInt("timecurrent");
                            String link = data.getString("image");
                            Picasso.get()
                                    .load(link)
                                    .placeholder(R.drawable.ic_logo)
                                    .error(R.drawable.ic_logo)
                                    .into(ivListItemSong);
                            ChartAdapter.listSongRequested.clear();
                        }
                       /* if(aaa == 1){
                            mSocket.emit("server-gui-request",mediaPlayerRoom1.getCurrentPosition());
                        }*/
/*                         if(aaa == 2){
                            mSocket.emit("server-gui-request",mediaPlayerRoom2.getCurrentPosition());
                        }*/
                        //playMp3FromByte(audio);
                    } catch (JSONException e) {
                        Log.d("Error", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener getAllDataSong = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //byte[] audio;
                    Song song;
                    try {
                        //audio = (byte[]) data.get("audio");
                        if (data.getInt("room") == room) {
                            try {
                                songArrayList = new ArrayList<>();
                                JSONArray jsonArray = data.getJSONArray("data");
                                JSONArray listDataSongRequested = data.getJSONArray("requests");
                                JSONObject jsonObject;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    song = gs.fromJson(jsonObject.toString(), Song.class);
                                    songArrayList.add(song);
                                    listRequest.add(listDataSongRequested.getInt(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                       /* if(aaa == 1){
                            mSocket.emit("server-gui-request",mediaPlayerRoom1.getCurrentPosition());
                        }*/
/*                         if(aaa == 2){
                            mSocket.emit("server-gui-request",mediaPlayerRoom2.getCurrentPosition());
                        }*/
                        //playMp3FromByte(audio);
                    } catch (JSONException e) {
                        Log.d("Error", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener updateRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //byte[] audio;
                    Song song;
                    try {
                        //audio = (byte[]) data.get("audio");
                        if (data.getInt("room") == room) {
                            try {
                                //listRequest = new ArrayList<>();
                                JSONArray jsonArray = data.getJSONArray("data");
                                JSONObject jsonObject;
                                listRequest.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    listRequest.add(jsonArray.getInt(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                       /* if(aaa == 1){
                            mSocket.emit("server-gui-request",mediaPlayerRoom1.getCurrentPosition());
                        }*/
/*                         if(aaa == 2){
                            mSocket.emit("server-gui-request",mediaPlayerRoom2.getCurrentPosition());
                        }*/
                        //playMp3FromByte(audio);
                    } catch (JSONException e) {
                        Log.d("Error", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
}