package com.luizguilherme.popularmovies.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.luizguilherme.popularmovies.adapters.MovieDetailType;

public abstract class MovieDetailTypeViewHolder extends RecyclerView.ViewHolder {
    protected final Context context;

    public MovieDetailTypeViewHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }

    public abstract void bind(MovieDetailType movieDetailType);

    public abstract void bindActions(MovieDetailType movieDetailType);

}
