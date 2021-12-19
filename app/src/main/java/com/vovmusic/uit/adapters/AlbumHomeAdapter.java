package com.vovmusic.uit.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.squareup.picasso.Picasso;
import com.vovmusic.uit.R;
import com.vovmusic.uit.activities.SongActivity;
import com.vovmusic.uit.models.Album;

import java.util.List;

public class AlbumHomeAdapter extends RecyclerView.Adapter<AlbumHomeAdapter.ViewHolder> {
    private static final String TAG = "AlbumHomeAdapter";

    private Context context;
    private List<Album> albumArrayList;
    private ViewPager2 viewPager2;

    public AlbumHomeAdapter(Context context, List<Album> albumArrayList, ViewPager2 viewPager2) {
        this.context = context;
        this.albumArrayList = albumArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_home, parent, false);
//        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHomeAdapter.ViewHolder holder, int position) {
        Picasso.get()
                .load(this.albumArrayList.get(position).getImg())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(holder.ivAlbum);
        holder.tvAlbumName.setText(this.albumArrayList.get(position).getName().trim());
        holder.tvAlbumSinger.setText(this.albumArrayList.get(position).getSinger().trim());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongActivity.class);
            intent.putExtra("ALBUM", albumArrayList.get(holder.getLayoutPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.albumArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cvAlbum;
        private ImageView ivAlbum;
        private TextView tvAlbumName;
        private TextView tvAlbumSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cvAlbum = (CardView) itemView.findViewById(R.id.cvAlbum);
            this.ivAlbum = (ImageView) itemView.findViewById(R.id.ivAlbum);

            this.tvAlbumName = (TextView) itemView.findViewById(R.id.tvAlbumName);
            this.tvAlbumName.setSelected(true); // Text will be moved

            this.tvAlbumSinger = (TextView) itemView.findViewById(R.id.tvAlbumSinger);
            this.tvAlbumSinger.setSelected(true); // Text will be moved
        }
    }
}
