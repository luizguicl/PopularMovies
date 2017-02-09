package com.luizguilherme.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.luizguilherme.popularmovies.data.MoviesContract.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_EXTERNAL_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
