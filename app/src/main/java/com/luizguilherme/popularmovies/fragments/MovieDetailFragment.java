package com.luizguilherme.popularmovies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luizguilherme.popularmovies.AndroidUtils;
import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailAdapter;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.asynctasks.FetchReviewsByMovieTask;
import com.luizguilherme.popularmovies.asynctasks.FetchTrailersByMovieTask;
import com.luizguilherme.popularmovies.models.Movie;
import com.luizguilherme.popularmovies.models.Trailer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.luizguilherme.popularmovies.Constants.YOUTUBE_VIDEO_PARAMETER;
import static com.luizguilherme.popularmovies.Constants.YOUTUBE_WATCH_BASE_URL;


public class MovieDetailFragment extends Fragment implements FetchTrailersByMovieTask.FinishTrailerTaskListener {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    @BindView(R.id.movie_detail_list)
    RecyclerView movieDetailList;

    private ShareActionProvider shareActionProvider;

    private Unbinder unbinder;
    private Movie movie;

    private MovieDetailAdapter movieDetailsAdapter;
    private LinearLayoutManager layoutManger;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_MOVIE)) {
            movie = intent.getParcelableExtra(Constants.EXTRA_MOVIE);
        } else {
            Log.d(TAG, "Could not get movie from extras.");
            return rootView;
        }

        movieDetailsAdapter = new MovieDetailAdapter(this.getActivity(), new ArrayList<MovieDetailType>());
        layoutManger = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        movieDetailList.setLayoutManager(layoutManger);
        movieDetailList.setAdapter(movieDetailsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        movieDetailsAdapter.clear();
        movieDetailsAdapter.addItem(movie);

        if (AndroidUtils.isOnline(getActivity())) {
            FetchTrailersByMovieTask trailersTask = new FetchTrailersByMovieTask(getActivity(), movieDetailsAdapter, this);
            trailersTask.execute(String.valueOf(movie.getId()));

            FetchReviewsByMovieTask reviewsTask = new FetchReviewsByMovieTask(getActivity(), movieDetailsAdapter);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    private void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
            getActivity().invalidateOptionsMenu();
        }
    }

    private Intent createShareIntent(String trailerUrl) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailerUrl);
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    @Override
    public void OnFinishTask(List<Trailer> trailers) {
        if (trailers != null && !trailers.isEmpty()) {
            final Uri builtUri = Uri.parse(YOUTUBE_WATCH_BASE_URL).buildUpon()
                    .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER, trailers.get(0).getKey())
                    .build();
            Intent shareIntent = createShareIntent(builtUri.toString());
            setShareIntent(shareIntent);

        }
    }
}
