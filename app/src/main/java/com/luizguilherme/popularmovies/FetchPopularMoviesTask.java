package com.luizguilherme.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.luizguilherme.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.luizguilherme.popularmovies.Constants.APPID_PARAM;
import static com.luizguilherme.popularmovies.Constants.LANGUAGE_PARAM;
import static com.luizguilherme.popularmovies.Constants.MOVIES_BASE_URL;

public class FetchPopularMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

    private final String TAG = FetchPopularMoviesTask.class.getSimpleName();

    private Context context;
    private ArrayAdapter<Movie> adapter;

    public FetchPopularMoviesTask(Context context, ArrayAdapter<Movie> adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    protected List<Movie> doInBackground(Void... voids) {

        String sortingPath = getSortingPathAccordingToSettings();

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

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
        if (result == null || adapter == null) {
            return;
        }

        adapter.clear();
        adapter.addAll(result);
    }

    private String getSortingPathAccordingToSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefSortOrder = preferences.getString(context.getString(R.string.pref_key_sort_order)
                , context.getString(R.string.pref_default_sort_order));

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
