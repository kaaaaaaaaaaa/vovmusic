package com.vovmusic.uit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vovmusic.uit.R;
import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.adapters.SongAdapter;

public class DetailPlayerFragment extends Fragment {
    private static final String TAG = "DetailPlayerFragment";

    private RecyclerView rvDataListSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Mapping(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void Mapping(View view) {
        this.rvDataListSong = view.findViewById(R.id.rvDataListSong);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (FullPlayerActivity.dataSongArrayList.size() > 0) {
            this.rvDataListSong.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
            this.rvDataListSong.setLayoutManager(layoutManager);

            this.rvDataListSong.setAdapter(new SongAdapter(getContext(), FullPlayerActivity.dataSongArrayList, "LISTSONG"));

            Log.d(TAG, FullPlayerActivity.dataSongArrayList.get(0).getName());
        } else {
            Log.d(TAG, "Lỗi! Không có dữ liệu");
        }
    }
}