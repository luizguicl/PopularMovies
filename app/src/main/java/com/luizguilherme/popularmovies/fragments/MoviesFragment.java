package com.luizguilherme.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.asynctasks.FetchPopularMoviesTask;
import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.activities.MovieDetailActivity;
import com.luizguilherme.popularmovies.activities.SettingsActivity;
import com.luizguilherme.popularmovies.adapters.MoviesAdapter;
import com.luizguilherme.popularmovies.models.AndroidUtils;
import com.luizguilherme.popularmovies.models.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class MoviesFragment extends Fragment {

    private final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.movies_list)
    GridView listView;

    private MoviesAdapter moviesAdapter;
    private Unbinder unbinder;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        moviesAdapter = new MoviesAdapter(
                getActivity(),
                new ArrayList<Movie>()
        );

        listView.setAdapter(moviesAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AndroidUtils.isOnline(getActivity())) {
            FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask(getActivity(), moviesAdapter);
            moviesTask.execute();
        } else {
            Toast.makeText(getActivity(), "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @OnItemClick(R.id.movies_list)
    void onItemClicked(int position) {
        Movie movie = moviesAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

}
