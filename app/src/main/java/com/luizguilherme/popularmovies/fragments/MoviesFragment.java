package com.luizguilherme.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.activities.SettingsActivity;
import com.luizguilherme.popularmovies.adapters.MoviesAdapter;
import com.luizguilherme.popularmovies.asynctasks.FetchPopularMoviesTask;
import com.luizguilherme.popularmovies.AndroidUtils;
import com.luizguilherme.popularmovies.models.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MoviesFragment extends Fragment {

    private final String TAG = MoviesFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    @BindView(R.id.movies_list)
    RecyclerView recyclerView;

    private MoviesAdapter moviesAdapter;
    private RecyclerView.LayoutManager layoutManager;
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

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        supportActionBar.setLogo(R.mipmap.ic_launcher);

        layoutManager = new GridLayoutManager(this.getActivity(),2);
       recyclerView.setLayoutManager(layoutManager);

        moviesAdapter = new MoviesAdapter(
                new ArrayList<Movie>()
        );

        recyclerView.setAdapter(moviesAdapter);

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

}
