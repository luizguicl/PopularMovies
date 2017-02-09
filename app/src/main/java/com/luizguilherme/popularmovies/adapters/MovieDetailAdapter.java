package com.luizguilherme.popularmovies.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.data.MoviesContract.MovieEntry;
import com.luizguilherme.popularmovies.models.Label;
import com.luizguilherme.popularmovies.models.Movie;
import com.luizguilherme.popularmovies.models.Review;
import com.luizguilherme.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import static com.luizguilherme.popularmovies.Constants.MOVIEDB_IMAGE_BASE_URL;
import static com.luizguilherme.popularmovies.Constants.POSTER_SIZE_DETAIL;
import static com.luizguilherme.popularmovies.Constants.YOUTUBE_VIDEO_PARAMETER;
import static com.luizguilherme.popularmovies.Constants.YOUTUBE_WATCH_BASE_URL;

public class MovieDetailAdapter extends ArrayAdapter<MovieDetailType> {

    public MovieDetailAdapter(Context context, List<MovieDetailType> movieDetails) {
        super(context, 0, movieDetails);
    }

    @Override
    public int getViewTypeCount() {
        return MovieDetailType.DetailType.values().length;
    }


    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMovieDetailType().ordinal();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MovieDetailType movieDetailType = getItem(position);

        // Get the data item type for this position
        int type = getItemViewType(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            // Inflate XML layout based on the type     
            convertView = getInflatedLayoutForType(type);
        }

        bindViewForType(type, convertView, movieDetailType);

        return convertView;
    }

    private void bindViewForType(int type, View convertView, MovieDetailType movieDetailType) {

        if (type == MovieDetailType.DetailType.OVERVIEW.ordinal()) {
            final Movie movie = (Movie) movieDetailType;

            ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
            TextView originalTitle = (TextView) convertView.findViewById(R.id.original_title);
            TextView releaseDate = (TextView) convertView.findViewById(R.id.release_date);
            TextView userRating = (TextView) convertView.findViewById(R.id.user_rating);
            TextView overview = (TextView) convertView.findViewById(R.id.overview);
            final ImageButton favoriteImgButton = (ImageButton) convertView.findViewById(R.id.favorite_button);
            final TextView favoriteAction = (TextView) convertView.findViewById(R.id.favorite_action_text);

            String imageUrl = MOVIEDB_IMAGE_BASE_URL + POSTER_SIZE_DETAIL + movie.getPosterPath();

            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_movie_poster)
                    .error(R.drawable.placeholder_movie_poster)
                    .into(moviePoster);
            originalTitle.setText(movie.getOriginalTitle());
            String formattedDate = String.format("(%s)", movie.getReleaseDate());
            releaseDate.setText(formattedDate);
            userRating.setText(String.format(new Locale(getContext().getString(R.string.languague), getContext().getString(R.string.country)), "%.2f", movie.getVoteAverage()));
            overview.setText(movie.getOverview());

            boolean isFavoriteMovie = checkIfMovieFavorite(movie.getId());
            setupIsFavoriteButton(favoriteImgButton, favoriteAction, isFavoriteMovie);

            favoriteImgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFavorite = checkIfMovieFavorite(movie.getId());
                    int favoriteActionMessage;
                    if (isFavorite) {
                        favoriteActionMessage = R.string.unmark_favorite_sucess_message;
                        removeMovieAsFavorite(movie);
                        setupIsFavoriteButton(favoriteImgButton, favoriteAction, !isFavorite);
                    } else {
                        favoriteActionMessage = R.string.mark_favorite_sucess_message;
                        addMovieAsFavorite(movie);
                        setupIsFavoriteButton(favoriteImgButton, favoriteAction, !isFavorite);
                    }
                    Toast.makeText(getContext(), favoriteActionMessage, Toast.LENGTH_SHORT).show();
                }
            });

        } else if (type == MovieDetailType.DetailType.LABEL.ordinal()) {

            Label trailerLabel = (Label) movieDetailType;

            TextView label = (TextView) convertView.findViewById(R.id.section_label);
            label.setText(trailerLabel.getLabel());

        } else if (type == MovieDetailType.DetailType.TRAILER.ordinal()) {
            Trailer trailer = (Trailer) movieDetailType;

            TextView trailerName = (TextView) convertView.findViewById(R.id.trailer_name);
            trailerName.setText(trailer.getName());

            final Uri builtUri = Uri.parse(YOUTUBE_WATCH_BASE_URL).buildUpon()
                    .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER, trailer.getKey())
                    .build();

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);
                    getContext().startActivity(intent);
                }
            });
        } else if (type == MovieDetailType.DetailType.REVIEW.ordinal()) {

            final Review review = (Review) movieDetailType;

            TextView author = (TextView) convertView.findViewById(R.id.author);
            author.setText(review.getAuthor());

            TextView content = (TextView) convertView.findViewById(R.id.content);
            content.setText(review.getContent());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private void setupIsFavoriteButton(ImageButton favoriteImgButton, TextView favoriteAction, boolean isFavoriteMovie) {
        if (isFavoriteMovie) {
            favoriteImgButton.setImageLevel(1);
            favoriteAction.setText(getContext().getString(R.string.is_favorite));
            favoriteImgButton.setContentDescription(getContext().getString(R.string.is_favorite));
        } else {
            favoriteImgButton.setImageLevel(0);
            favoriteAction.setText(getContext().getString(R.string.mark_as_favorite));
            favoriteImgButton.setContentDescription(getContext().getString(R.string.mark_as_favorite));
        }
    }

    private View getInflatedLayoutForType(int type) {

        if (type == MovieDetailType.DetailType.OVERVIEW.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_overview, null);
        } else if (type == MovieDetailType.DetailType.LABEL.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_label, null);
        } else if (type == MovieDetailType.DetailType.TRAILER.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, null);
        } else if (type == MovieDetailType.DetailType.REVIEW.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_review, null);
        } else {
            return null;
        }

    }

    private boolean checkIfMovieFavorite(int externalId) {

        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor movieCursor = contentResolver.query(MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
                new String[]{String.valueOf(externalId)},
                null);

        if (movieCursor.moveToNext()) {
            return true;
        } else {
            return false;
        }
    }

    private long addMovieAsFavorite(Movie movie) {

        long movieId;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_EXTERNAL_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

        ContentResolver contentResolver = getContext().getContentResolver();
        // Students: First, check if the location with this city name exists in the db
        Cursor movieCursor = contentResolver.query(MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
                new String[]{String.valueOf(movie.getId())},
                null);

        // If it exists, return the current ID
        if (movieCursor.moveToNext()) {
            int columnIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            movieId = movieCursor.getLong(columnIndex);
            movieCursor.close();
        }// Otherwise, insert it using the content resolver and the base URI
        else {
            Uri insertUri = contentResolver.insert(MovieEntry.CONTENT_URI, contentValues);
            movieId = ContentUris.parseId(insertUri);
        }

        return movieId;
    }

    private void removeMovieAsFavorite(Movie movie) {

        ContentResolver contentResolver = getContext().getContentResolver();
        // Students: First, check if the location with this city name exists in the db
        contentResolver.delete(MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
                new String[]{String.valueOf(movie.getId())});
    }

}
