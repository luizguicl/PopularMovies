package com.luizguilherme.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.asynctasks.FetchReviewsByMovieTask;
import com.luizguilherme.popularmovies.asynctasks.FetchTrailersByMovieTask;
import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailAdapter;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.models.AndroidUtils;
import com.luizguilherme.popularmovies.models.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    @BindView(R.id.movie_detail_list)
    ListView movieDetailList;

    private Unbinder unbinder;
    private Movie movie;

    private MovieDetailAdapter movieDetailsAdapter;

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

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_MOVIE)) {
            movie = intent.getParcelableExtra(Constants.EXTRA_MOVIE);
        } else {
            Log.d(TAG, "Could not get movie from extras.");
            return rootView;
        }

        movieDetailsAdapter = new MovieDetailAdapter(this.getActivity(), new ArrayList<MovieDetailType>());


        movieDetailList.setAdapter(movieDetailsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        movieDetailsAdapter.clear();
        movieDetailsAdapter.add(movie);

        if (AndroidUtils.isOnline(getActivity())) {
            FetchTrailersByMovieTask trailersTask = new FetchTrailersByMovieTask(getActivity(), movieDetailsAdapter );
            trailersTask.execute(String.valueOf(movie.getId()));

            FetchReviewsByMovieTask reviewsTask = new FetchReviewsByMovieTask(getActivity(), movieDetailsAdapter );
            reviewsTask.execute(String.valueOf(movie.getId()));
        } else {
            Toast.makeText(getActivity(), "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
