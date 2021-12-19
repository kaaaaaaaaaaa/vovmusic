package com.vovmusic.uit.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vovmusic.uit.R;
import com.vovmusic.uit.activities.SongActivity;
import com.vovmusic.uit.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private static final String TAG = "GenreAdapter";

    private Context context;
    private List<Genre> genreList;

    public GenreAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder holder, int position) {
        Picasso.get()
                .load(this.genreList.get(position).getImg().trim())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(holder.ivGenreImage);

        holder.tvGenreName.setText(this.genreList.get(position).getName().trim());

        holder.ivGenreImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongActivity.class);
            intent.putExtra("GENRE", genreList.get(holder.getLayoutPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.genreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGenreImage;
        private TextView tvGenreName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.ivGenreImage = itemView.findViewById(R.id.ivGenreImage);

            this.tvGenreName = itemView.findViewById(R.id.tvGenreName);
            this.tvGenreName.setSelected(true);
        }
    }
}
