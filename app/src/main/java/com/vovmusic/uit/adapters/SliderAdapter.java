package com.vovmusic.uit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.activities.FullPlayerActivity;
import com.vovmusic.uit.models.Slider;
import com.vovmusic.uit.models.Song;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.MyViewHolder> {
    private static final String TAG = "SliderAdapter";

    private AlertDialog alertDialog;

    private Context context;
    private ArrayList<Slider> imageSliders;

    private ArrayList<Song> songArrayList;

    public SliderAdapter(Context context, ArrayList<Slider> imageSliders) {
        this.context = context;
        this.imageSliders = imageSliders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Picasso.get()
                .load(this.imageSliders.get(position).getImage())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(viewHolder.ivSliderImage);

        viewHolder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v.getContext(), R.style.WrapContentDialog);
            View view = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_loading_dialog, null);
            alertBuilder.setView(view);
            alertBuilder.setCancelable(false);
            this.alertDialog = alertBuilder.create();
            this.alertDialog.show();

            Handle_Song_Slider(imageSliders.get(position).getSongID());
        });
    }

    private void Handle_Song_Slider(int songID) {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<Song>> callBack = dataService.getSongFromSlider(songID); // Gửi phương thức getSlider() -> trả về dữ liệu cho biến callBack
        callBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                songArrayList = new ArrayList<>();
                songArrayList = (ArrayList<Song>) response.body();

                if (songArrayList != null && songArrayList.size() > 0) {
                    Intent intent = new Intent(context, FullPlayerActivity.class);
                    intent.putExtra("SONGSLIDER", songArrayList);
                    context.startActivity(intent);

                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                alertDialog.dismiss();
                Log.d(TAG, "Handle_Song_Slider(Error): " + t.getMessage());
            }
        });
    }

    @Override
    public int getCount() {
        return this.imageSliders.size();
    }

    public class MyViewHolder extends SliderViewAdapter.ViewHolder {
        private ImageView ivSliderImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.ivSliderImage = (ImageView) itemView.findViewById(R.id.ivSliderImage);
        }
    }
}
