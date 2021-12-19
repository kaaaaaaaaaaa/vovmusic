package com.vovmusic.uit.fragments;

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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.adapters.ChartAdapter;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartFragment extends Fragment {
    private ArrayList<Song> songArrayList;
    private RecyclerView rvChart;

    private ShimmerFrameLayout sflItemSong;

    private ScaleAnimation scaleAnimation;

    private static final String TAG = "ChartFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Mapping(view);
        Handle_SongChart();
    }

    private void Mapping(View view) {
        this.rvChart = view.findViewById(R.id.rvChart);
        this.sflItemSong = view.findViewById(R.id.sflItemSong);
    }

    private void Handle_SongChart() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getSongChart();
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    sflItemSong.setVisibility(View.GONE); // Load biến mất

                    rvChart.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc
                    rvChart.setLayoutManager(layoutManager);
                    rvChart.setAdapter(new ChartAdapter(getContext(), songArrayList));
                    rvChart.setVisibility(View.VISIBLE); // Hiện thông tin

                    Log.d(TAG, songArrayList.get(0).getName());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}