package com.luizguilherme.popularmovies.asynctasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.luizguilherme.popularmovies.BuildConfig;
import com.luizguilherme.popularmovies.HttpUtils;
import com.luizguilherme.popularmovies.adapters.MovieDetailAdapter;
import com.luizguilherme.popularmovies.models.Label;
import com.luizguilherme.popularmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.luizguilherme.popularmovies.Constants.APPID_PARAM;
import static com.luizguilherme.popularmovies.Constants.MOVIEDB_REVIEWS_PATH;
import static com.luizguilherme.popularmovies.Constants.MOVIES_BASE_URL;

public class FetchReviewsByMovieTask extends AsyncTask<String, Void, List<Review>> {

    private static final String TAG = FetchReviewsByMovieTask.class.getSimpleName();
    private final Context context;
    private final MovieDetailAdapter adapter;

    public FetchReviewsByMovieTask(Context context, MovieDetailAdapter movieDetailsAdapter) {
        this.context = context;
        this.adapter = movieDetailsAdapter;
    }

    @Override
    protected List<Review> doInBackground(String... params) {

        String movieId = params[0];

        // Construct the URL for the MovieDatabase api request
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIEDB_REVIEWS_PATH)
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        // Will contain the raw JSON response as a string.
        String movieReviewsJsonStr = HttpUtils.makeRequestFromUri(builtUri);

        if (movieReviewsJsonStr == null) return null;

        try {
            return getReviewsDataFromJson(movieReviewsJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> result) {
        if (result == null || adapter == null) {
            return;
        }

        adapter.add(new Label("Reviews:"));
        adapter.addAll(result);
    }

    private List<Review> getReviewsDataFromJson(String movieTrailersJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MR_RESULTS = "results";
        final String MR_ID = "id";
        final String MR_AUTHOR = "author";
        final String MR_CONTENT = "content";
        final String MR_URL = "url";

        JSONObject reviewsJson = new JSONObject(movieTrailersJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(MR_RESULTS);

        List<Review> reviewsResult = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {

            // Get the JSON object representing a trailer
            JSONObject trailerObject = reviewsArray.getJSONObject(i);

            String id = trailerObject.getString(MR_ID);
            String author = trailerObject.getString(MR_AUTHOR);
            String content = trailerObject.getString(MR_CONTENT);
            String url = trailerObject.getString(MR_URL);

            Review review = new Review(id, author, content, url);

            reviewsResult.add(review);

        }

        return reviewsResult;
    }


}
