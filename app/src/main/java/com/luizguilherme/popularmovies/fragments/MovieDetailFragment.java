package com.luizguilherme.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.Movie;
import com.luizguilherme.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.luizguilherme.popularmovies.Constants.MOVIEDB_IMAGE_BASE_URL;
import static com.luizguilherme.popularmovies.Constants.POSTER_SIZE_DETAIL;


public class MovieDetailFragment extends Fragment {

    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.original_title)
    TextView originalTitle;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.user_rating)
    TextView userRating;
    @BindView(R.id.overview)
    TextView overview;

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    private Unbinder unbinder;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Movie movie = null;

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_MOVIE)) {
            movie = intent.getParcelableExtra(Constants.EXTRA_MOVIE);
        } else {
            Log.d(TAG, "Could not get movie from extras.");
            return rootView;
        }

        String imageUrl = MOVIEDB_IMAGE_BASE_URL + POSTER_SIZE_DETAIL + movie.getPosterPath();

        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_movie_poster)
                .error(R.mipmap.placeholder_movie_poster)
                .into(moviePoster);
        originalTitle.setText(movie.getOriginalTitle());
        String formattedDate = String.format("(%s)", movie.getReleaseDate());
        releaseDate.setText(formattedDate);
        userRating.setText(String.format(new Locale(getString(R.string.languague), getString(R.string.country)), "%.2f", movie.getVoteAverage()));
        overview.setText(movie.getOverview());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
