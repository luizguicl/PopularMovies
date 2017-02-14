package com.luizguilherme.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.viewholders.MovieViewHolder;
import com.luizguilherme.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> movies = new ArrayList<>();

    public MoviesAdapter(List<Movie> objects) {
        this.movies = objects;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    public void addItems(List<Movie> movie) {
        movies.addAll(movie);
        notifyDataSetChanged();
    }

}
