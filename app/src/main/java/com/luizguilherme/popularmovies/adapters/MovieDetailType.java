package com.luizguilherme.popularmovies.adapters;

public interface MovieDetailType {

    DetailType getMovieDetailType();

    enum DetailType{
        OVERVIEW,
        LABEL,
        TRAILER,
        REVIEW
    }

}
