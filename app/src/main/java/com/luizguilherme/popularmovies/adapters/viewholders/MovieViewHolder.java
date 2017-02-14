package com.luizguilherme.popularmovies.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.activities.MovieDetailActivity;
import com.luizguilherme.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import static com.luizguilherme.popularmovies.Constants.MOVIEDB_IMAGE_BASE_URL;
import static com.luizguilherme.popularmovies.Constants.POSTER_SIZE;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    private final ImageView poster;
    private final Context context;

    public MovieViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        poster = (ImageView) itemView.findViewById(R.id.poster_image);
    }

    public void bind(final Movie movie) {

        String imageUrl = MOVIEDB_IMAGE_BASE_URL + POSTER_SIZE + movie.getPosterPath();

        Picasso.with(itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_movie_poster)
                .error(R.drawable.placeholder_movie_poster)
                .into(poster);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(Constants.EXTRA_MOVIE, movie);
                context.startActivity(intent);
            }
        });

    }
}
