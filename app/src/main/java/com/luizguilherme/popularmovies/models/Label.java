package com.luizguilherme.popularmovies.models;

import com.luizguilherme.popularmovies.adapters.MovieDetailType;

public class Label implements MovieDetailType {

    private String label;

    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public DetailType getMovieDetailType() {
        return DetailType.LABEL;
    }
}
