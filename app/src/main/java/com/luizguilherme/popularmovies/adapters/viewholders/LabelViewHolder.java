package com.luizguilherme.popularmovies.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.models.Label;


public class LabelViewHolder extends MovieDetailTypeViewHolder {

    private final TextView label;

    public LabelViewHolder(View itemView) {
        super(itemView);

        label = (TextView) itemView.findViewById(R.id.section_label);
    }

    @Override
    public void bind(MovieDetailType movieDetailType) {
        Label trailerLabel = (Label) movieDetailType;
        label.setText(trailerLabel.getLabel());
    }

    @Override
    public void bindActions(MovieDetailType movieDetailType) {

    }
}
