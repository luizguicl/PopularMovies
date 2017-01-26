package com.luizguilherme.popularmovies.asynctasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.luizguilherme.popularmovies.BuildConfig;
import com.luizguilherme.popularmovies.HttpUtils;
import com.luizguilherme.popularmovies.adapters.MovieDetailAdapter;
import com.luizguilherme.popularmovies.models.Label;
import com.luizguilherme.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.luizguilherme.popularmovies.Constants.APPID_PARAM;
import static com.luizguilherme.popularmovies.Constants.MOVIEDB_VIDEOS_PATH;
import static com.luizguilherme.popularmovies.Constants.MOVIES_BASE_URL;

public class FetchTrailersByMovieTask extends AsyncTask<String, Void, List<Trailer>> {

    private static final String TAG = FetchTrailersByMovieTask.class.getSimpleName();
    private final Context context;
    private final MovieDetailAdapter adapter;

    public FetchTrailersByMovieTask(Context context, MovieDetailAdapter movieDetailsAdapter) {
        this.context = context;
        this.adapter = movieDetailsAdapter;
    }

    @Override
    protected List<Trailer> doInBackground(String... params) {

        String movieId = params[0];

        // Construct the URL for the MovieDatabase api request
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIEDB_VIDEOS_PATH)
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        // Will contain the raw JSON response as a string.
        String movieTrailersJsonStr = HttpUtils.makeRequestFromUri(builtUri);

        if (movieTrailersJsonStr == null) return null;

        try {
            return getTrailersDataFromJson(movieTrailersJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> result) {
        if (result == null || adapter == null) {
            return;
        }

        adapter.add(new Label("Trailers:"));
        adapter.addAll(result);
    }

    private List<Trailer> getTrailersDataFromJson(String movieTrailersJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MT_RESULTS = "results";
        final String MT_ID = "id";
        final String MT_KEY = "key";
        final String MT_NAME = "name";
        final String MT_SITE = "site";
        final String MT_SIZE = "size";
        final String MT_TYPE = "type";

        JSONObject trailersJson = new JSONObject(movieTrailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(MT_RESULTS);

        List<Trailer> trailersResult = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {

            // Get the JSON object representing a trailer
            JSONObject trailerObject = trailersArray.getJSONObject(i);

            String id = trailerObject.getString(MT_ID);
            String key = trailerObject.getString(MT_KEY);
            String name = trailerObject.getString(MT_NAME);
            String site = trailerObject.getString(MT_SITE);
            int size = trailerObject.getInt(MT_SIZE);
            String type = trailerObject.getString(MT_TYPE);

            Trailer trailer = new Trailer(id, key, name, site, size, type);

            trailersResult.add(trailer);

        }

        return trailersResult;
    }


}
