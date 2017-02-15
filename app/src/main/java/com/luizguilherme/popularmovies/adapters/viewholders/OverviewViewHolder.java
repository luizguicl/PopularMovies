package com.luizguilherme.popularmovies.adapters.viewholders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.data.MoviesContract;
import com.luizguilherme.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static com.luizguilherme.popularmovies.Constants.MOVIEDB_IMAGE_BASE_URL;
import static com.luizguilherme.popularmovies.Constants.POSTER_SIZE_DETAIL;

public class OverviewViewHolder extends MovieDetailTypeViewHolder {

    private final ImageView moviePoster;
    private final TextView originalTitle;
    private final TextView releaseDate;
    private final TextView userRating;
    private final TextView overview;
    private final ImageButton favoriteImgButton;
    private final TextView favoriteAction;

    public OverviewViewHolder(View itemView) {
        super(itemView);
        moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
        originalTitle = (TextView) itemView.findViewById(R.id.original_title);
        releaseDate = (TextView) itemView.findViewById(R.id.release_date);
        userRating = (TextView) itemView.findViewById(R.id.user_rating);
        overview = (TextView) itemView.findViewById(R.id.movie_overview);
        favoriteImgButton = (ImageButton) itemView.findViewById(R.id.favorite_button);
        favoriteAction = (TextView) itemView.findViewById(R.id.favorite_action_text);
    }

    @Override
    public void bind(MovieDetailType movieDetailType) {

        final Movie movie = (Movie) movieDetailType;

        String imageUrl = MOVIEDB_IMAGE_BASE_URL + POSTER_SIZE_DETAIL + movie.getPosterPath();

        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_movie_poster)
                .error(R.drawable.placeholder_movie_poster)
                .into(moviePoster);
        originalTitle.setText(movie.getOriginalTitle());
        String formattedDate = String.format("(%s)", movie.getReleaseDate());
        releaseDate.setText(formattedDate);
        String formatedVote = String.format(new Locale(context.getString(R.string.languague), context.getString(R.string.country)), "%.1f", movie.getVoteAverage());
        userRating.setText(context.getString(R.string.format_vote_average, formatedVote, 10f));
        overview.setText(movie.getOverview());

        boolean isFavoriteMovie = checkIfMovieFavorite(movie.getId());
        setupIsFavoriteButton(favoriteImgButton, favoriteAction, isFavoriteMovie);

        bindActions(movie);

    }

    @Override
    public void bindActions(MovieDetailType movieDetailType) {
        final Movie movie = (Movie) movieDetailType;
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
                Toast.makeText(context, favoriteActionMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupIsFavoriteButton(ImageButton favoriteImgButton, TextView favoriteAction, boolean isFavoriteMovie) {
        if (isFavoriteMovie) {
            favoriteImgButton.setImageLevel(1);
            favoriteAction.setText(context.getString(R.string.is_favorite));
            favoriteImgButton.setContentDescription(context.getString(R.string.is_favorite));
        } else {
            favoriteImgButton.setImageLevel(0);
            favoriteAction.setText(context.getString(R.string.mark_as_favorite));
            favoriteImgButton.setContentDescription(context.getString(R.string.mark_as_favorite));
        }
    }

    private boolean checkIfMovieFavorite(int externalId) {

        ContentResolver contentResolver = context.getContentResolver();
        Cursor movieCursor = contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
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
        contentValues.put(MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

        ContentResolver contentResolver = context.getContentResolver();
        // Students: First, check if the location with this city name exists in the db
        Cursor movieCursor = contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
                new String[]{String.valueOf(movie.getId())},
                null);

        // If it exists, return the current ID
        if (movieCursor.moveToNext()) {
            int columnIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry._ID);
            movieId = movieCursor.getLong(columnIndex);
            movieCursor.close();
        }// Otherwise, insert it using the content resolver and the base URI
        else {
            Uri insertUri = contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
            movieId = ContentUris.parseId(insertUri);
        }

        return movieId;
    }

    private void removeMovieAsFavorite(Movie movie) {

        ContentResolver contentResolver = context.getContentResolver();
        // Students: First, check if the location with this city name exists in the db
        contentResolver.delete(MoviesContract.MovieEntry.CONTENT_URI,
                MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID + "= ?",
                new String[]{String.valueOf(movie.getId())});
    }

}
