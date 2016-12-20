package com.luizguilherme.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.luizguilherme.popularmovies.Constants.MOVIEDB_IMAGE_BASE_URL;
import static com.luizguilherme.popularmovies.Constants.POSTER_SIZE;

public class MoviesAdapter extends ArrayAdapter<Movie> {



    public MoviesAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        String imageUrl = MOVIEDB_IMAGE_BASE_URL + POSTER_SIZE + movie.getPosterPath();

        ImageView poster = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_movie_poster)
                .error(R.mipmap.placeholder_movie_poster)
                .into(poster);

        return convertView;
    }
}
