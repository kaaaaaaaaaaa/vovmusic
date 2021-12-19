package com.vovmusic.uit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.adapters.AlbumHomeAdapter;
import com.vovmusic.uit.adapters.PlaylistHomeAdapter;
import com.vovmusic.uit.adapters.SliderAdapter;
import com.vovmusic.uit.adapters.ThemeHomeAdapter;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Album;
import com.vovmusic.uit.models.Playlist;
import com.vovmusic.uit.models.Slider;
import com.vovmusic.uit.models.Theme;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final String TAG_1 = "Album";
    private static final String TAG_2 = "Playlist";
    private static final String TAG_3 = "Theme";

    private ScaleAnimation scaleAnimation;

    private SliderView sliderView;
    private ArrayList<Slider> sliderArrayList;

    // Album
    private List<Album> albumArrayList;
    private ShimmerFrameLayout sflItemAlbum;
    private ViewPager2 vpg2Album;
    private ImageView ivAlbumMore;
    private TextView tvAlbum;

    // Playlist
    private ArrayList<Playlist> playlistArrayList;
    private ShimmerFrameLayout sflItemPlaylist;
    private RecyclerView rvPlaylist;
    private ImageView ivPlaylistMore;
    private TextView tvPlaylist;

    // Theme
    private List<Theme> themeArrayList;
    private ShimmerFrameLayout sflItemTheme;
    private RecyclerView rvTheme;
    private ImageView ivThemeMore;
    private TextView tvTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Mapping(view);
        Handle_Slider();
        Handle_Album();
        Handle_Playlist();
        Handle_Theme();
    }

    private void Mapping(View view) {
        this.sliderView = view.findViewById(R.id.isvSlider);

        // Album
        this.sflItemAlbum = view.findViewById(R.id.sflItemAlbum);
        this.vpg2Album = view.findViewById(R.id.vpg2Album);

        this.ivAlbumMore = view.findViewById(R.id.ivAlbumMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivAlbumMore);
        this.scaleAnimation.Event_ImageView();

        this.tvAlbum = view.findViewById(R.id.tvAlbum);
        this.tvAlbum.setSelected(true); // Text will be moved

        // Playlist
        this.sflItemPlaylist = view.findViewById(R.id.sflItemPlaylist);
        this.rvPlaylist = view.findViewById(R.id.rvPlaylist);

        this.ivPlaylistMore = view.findViewById(R.id.ivPlaylistMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivPlaylistMore);
        this.scaleAnimation.Event_ImageView();

        this.tvPlaylist = view.findViewById(R.id.tvPlaylist);
        this.tvPlaylist.setSelected(true); // Text will be moved

        // Theme
        this.sflItemTheme = view.findViewById(R.id.sflItemTheme);
        this.rvTheme = view.findViewById(R.id.rvTheme);

        this.ivThemeMore = view.findViewById(R.id.ivThemeMore);
        this.scaleAnimation = new ScaleAnimation(getActivity(), this.ivThemeMore);
        this.scaleAnimation.Event_ImageView();

        this.tvTheme = view.findViewById(R.id.tvTheme);
        this.tvTheme.setSelected(true); // Text will be moved
    }

    private void Handle_Slider() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Slider>> callBack = dataService.getSlider(); // Gửi phương thức getSlider() -> trả về dữ liệu cho biến callBack
        callBack.enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                sliderArrayList = new ArrayList<>();
                sliderArrayList = (ArrayList<Slider>) response.body(); // Lấy dữ liệu về đưa vào Arraylist

                if (sliderArrayList != null) {
                    sliderView.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION);
                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                    sliderView.setSliderAdapter(new SliderAdapter(getContext(), sliderArrayList));

                    Log.d(TAG, sliderArrayList.get(0).getImage());
                }
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                Log.d(TAG, "Handle_Slider(Error): " + t.getMessage());
            }
        });
    }

    private void Handle_Album() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Album>> callBack = dataService.getAlbumCurrentDay();
        callBack.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                albumArrayList = new ArrayList<>();
                albumArrayList = response.body();

                if (albumArrayList != null) {
//                    rvAlbum.setHasFixedSize(true);
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                    layoutManager.setOrientation(RecyclerView.HORIZONTAL); // Chiều ngang
//                    rvAlbum.setLayoutManager(layoutManager);
//                    rvAlbum.setAdapter(new AlbumHomeAdapter(getContext(), albumArrayList));

                    vpg2Album.setClipToPadding(false); // Set clip padding
                    vpg2Album.setClipChildren(false); // Set clip children
                    vpg2Album.setOffscreenPageLimit(3); // Set page limit
                    vpg2Album.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER); // Không bao giờ cho phép người dùng cuộn quá chế độ xem này.

                    vpg2Album.setAdapter(new AlbumHomeAdapter(getContext(), albumArrayList, vpg2Album));

                    // Xét các hiệu ứng chuyển động cho vpg2Album
                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(30));
                    compositePageTransformer.addTransformer((page, position) -> {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.8f + r * 0.2f);
                    });

                    vpg2Album.setPageTransformer(compositePageTransformer);

                    sflItemAlbum.setVisibility(View.GONE); // Load biến mất
                    vpg2Album.setVisibility(View.VISIBLE); // Hiện thông tin

                    Log.d(TAG_1, albumArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.d(TAG_1, t.getMessage());
            }
        });
    }

    private void Handle_Playlist() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Playlist>> callBack = dataService.getPlaylistCurrentDay(); // Gửi phương thức getPlaylistCurrentDay() -> trả về dữ liệu cho biến callBack
        callBack.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                playlistArrayList = new ArrayList<>();
                playlistArrayList = (ArrayList<Playlist>) response.body(); // Lấy dữ liệu về đưa vào Arraylist

                if (playlistArrayList != null) {
                    rvPlaylist.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.VERTICAL); // Chiều dọc xuống
                    rvPlaylist.setLayoutManager(layoutManager);
                    rvPlaylist.setAdapter(new PlaylistHomeAdapter(getContext(), playlistArrayList));

                    sflItemPlaylist.setVisibility(View.GONE); // Load biến mất
                    rvPlaylist.setVisibility(View.VISIBLE); // Hiện thông tin

                    Log.d(TAG_2, playlistArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG_2, t.getMessage());
            }
        });
    }

    private void Handle_Theme() {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Theme>> callBack = dataService.getThemeCurrentDay(); // Gửi phương thức getThemeCurrentDay() -> trả về dữ liệu cho biến callBack
        callBack.enqueue(new Callback<List<Theme>>() {
            @Override
            public void onResponse(Call<List<Theme>> call, Response<List<Theme>> response) {
                themeArrayList = new ArrayList<>();
                themeArrayList = response.body();

                if (themeArrayList != null) {
                    rvTheme.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(RecyclerView.HORIZONTAL); // Chiều ngang
                    rvTheme.setLayoutManager(layoutManager);
                    rvTheme.setAdapter(new ThemeHomeAdapter(getContext(), themeArrayList));

                    sflItemTheme.setVisibility(View.GONE); // Load biến mất
                    rvTheme.setVisibility(View.VISIBLE); // Hiện thông tin

                    Log.d(TAG_3, themeArrayList.get(0).getImg());
                }
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {
                Log.d(TAG_3, t.getMessage());
            }
        });
    }
}
