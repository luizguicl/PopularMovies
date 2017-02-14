package com.luizguilherme.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailType.DetailType;
import com.luizguilherme.popularmovies.adapters.viewholders.LabelViewHolder;
import com.luizguilherme.popularmovies.adapters.viewholders.MovieDetailTypeViewHolder;
import com.luizguilherme.popularmovies.adapters.viewholders.OverviewViewHolder;
import com.luizguilherme.popularmovies.adapters.viewholders.ReviewViewHolder;
import com.luizguilherme.popularmovies.adapters.viewholders.TrailerViewHolder;

import java.util.List;

import static com.luizguilherme.popularmovies.adapters.MovieDetailType.DetailType.values;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailTypeViewHolder> {

    private final List< MovieDetailType> data;
    private final Context context;

    public MovieDetailAdapter(Context context, List<MovieDetailType> movieDetails) {
        this.context = context;
        this.data = movieDetails;
    }

    @Override
    public MovieDetailTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        DetailType detailType = values()[viewType];

        switch (detailType){
            case OVERVIEW:
                return new OverviewViewHolder(inflateViewItem(parent, R.layout.item_overview));
            case LABEL:
                return new LabelViewHolder(inflateViewItem(parent, R.layout.item_label));
            case TRAILER:
                return new TrailerViewHolder(inflateViewItem(parent, R.layout.item_trailer));
            case REVIEW:
                return new ReviewViewHolder(inflateViewItem(parent, R.layout.item_review));
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MovieDetailTypeViewHolder holder, int position) {
        MovieDetailType detailType = getItem(position);
        holder.bind(detailType);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMovieDetailType().ordinal();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public MovieDetailType getItem(int position){
        return data.get(position);
    }

    private View inflateViewItem(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
    }

    public void addItem(MovieDetailType movieDetailType) {
        data.add(movieDetailType);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<? extends MovieDetailType> movieDetailTypes) {
        data.addAll(movieDetailTypes);
        notifyDataSetChanged();
    }
}
