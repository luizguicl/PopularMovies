package com.luizguilherme.popularmovies.asynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.luizguilherme.popularmovies.BuildConfig;
import com.luizguilherme.popularmovies.Constants;
import com.luizguilherme.popularmovies.HttpUtils;
import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MoviesAdapter;
import com.luizguilherme.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.luizguilherme.popularmovies.Constants.APPID_PARAM;
import static com.luizguilherme.popularmovies.Constants.LANGUAGE_PARAM;
import static com.luizguilherme.popularmovies.Constants.MOVIES_BASE_URL;
import static com.luizguilherme.popularmovies.data.MoviesContract.MovieEntry;

public class FetchPopularMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

    private final String TAG = FetchPopularMoviesTask.class.getSimpleName();

    private Context context;
    private MoviesAdapter adapter;

    public FetchPopularMoviesTask(Context context, MoviesAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    protected List<Movie> doInBackground(Void... voids) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefSortOrder = preferences.getString(context.getString(R.string.pref_key_sort_order)
                , context.getString(R.string.pref_default_sort_order));


        if (context.getString(R.string.sort_oder_value_favorite).equalsIgnoreCase(prefSortOrder)) {
            List<Movie> favoriteMovies = new ArrayList<>();

            ContentResolver contentResolver = context.getContentResolver();

            Cursor movieCursor = contentResolver.query(MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            // If it exists, return the current ID
            while (movieCursor.moveToNext()) {

                int indexMovieId = movieCursor.getColumnIndex(MovieEntry.COLUMN_EXTERNAL_ID);
                int movieId = movieCursor.getInt(indexMovieId);
                int indexOriginalTitle = movieCursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE);
                String originalTitle = movieCursor.getString(indexOriginalTitle);
                int indexOverview = movieCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
                String overview = movieCursor.getString(indexOverview);
                int indexPosterPath = movieCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
                String posterPath = movieCursor.getString(indexPosterPath);
                int indexReleaseDate = movieCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
                int releaseDate = movieCursor.getInt(indexReleaseDate);
                int indexVoteAverage = movieCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
                double voteAverage = movieCursor.getDouble(indexVoteAverage);


                Movie movie = new Movie(movieId, originalTitle, posterPath, overview, voteAverage, new Date(releaseDate).toString());

                favoriteMovies.add(movie);

            }

            movieCursor.close();

            return favoriteMovies;

        } else {
            String sortingPath = getSortingPathAccordingToSettings(prefSortOrder);

            // Construct the URL for the MovieDatabase api request
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendEncodedPath(sortingPath)
                    .appendQueryParameter(LANGUAGE_PARAM, context.getString(R.string.languague))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            // Will contain the raw JSON response as a string.
            String popularMoviesJsonStr = HttpUtils.makeRequestFromUri(builtUri);

            if (popularMoviesJsonStr == null) return null;

            try {
                return getMovieDataFromJson(popularMoviesJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
        if (result == null || adapter == null) {
            return;
        }
        adapter.addItems(result);
    }

    private String getSortingPathAccordingToSettings(String prefSortOrder) {
        if (context.getString(R.string.sort_oder_value_popular).equalsIgnoreCase(prefSortOrder)) {
            return Constants.MOVIEDB_POPULAR_PATH;
        } else {
            return Constants.MOVIEDB_TOP_RATED_PATH;
        }
    }

    private List<Movie> getMovieDataFromJson(String popularMoviesJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_ORIGINAL_TITLE = "original_title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_POPULARITY = "popularity";

        JSONObject popularMoviesJson = new JSONObject(popularMoviesJsonStr);
        JSONArray moviesArray = popularMoviesJson.getJSONArray(MDB_RESULTS);

        List<Movie> moviesResult = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing a movie
            JSONObject movieObject = moviesArray.getJSONObject(i);

            int id = movieObject.getInt(MDB_ID);
            String originalTitle = movieObject.getString(MDB_ORIGINAL_TITLE);
            String posterPath = movieObject.getString(MDB_POSTER);
            String overview = movieObject.getString(MDB_OVERVIEW);
            double voteAverage = movieObject.getDouble(MDB_VOTE_AVERAGE);
            String releaseDate = movieObject.getString(MDB_RELEASE_DATE);
            double popularity = movieObject.getDouble(MDB_POPULARITY);

            Movie movie = new Movie(id, originalTitle, posterPath, overview, voteAverage, releaseDate);

            moviesResult.add(movie);

        }

        return moviesResult;
    }
}
