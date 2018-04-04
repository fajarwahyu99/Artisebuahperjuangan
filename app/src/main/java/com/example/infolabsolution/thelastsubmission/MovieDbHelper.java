package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieMostPopularEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieTopRatedEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.ReviewEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.TrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + FavMovieEntry.TABLE_NAME + " (" +
                        FavMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_A_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                        FavMovieEntry.COLUMN_NUMBER_OF_REVIEWS + " TEXT, " +
                        FavMovieEntry.COLUMN_NUMBER_OF_TRAILERS + " TEXT, " +
                        " UNIQUE (" + MovieContract.FavMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);


        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                        ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewEntry.COLUMN_MOVIE_ID + " TEXT, " +
                        ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                        " FOREIGN KEY(" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                        + MovieContract.FavMovieEntry.TABLE_NAME + "(" + MovieContract.FavMovieEntry.COLUMN_MOVIE_ID + "));";

        final String SQL_CREATE_REVIEW_TABLE_INDEX =
                "CREATE INDEX " + ReviewEntry.INDEX_NAME +
                        " ON " + ReviewEntry.TABLE_NAME + " (" + ReviewEntry.COLUMN_MOVIE_ID + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE_INDEX);
        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                        TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailerEntry.COLUMN_MOVIE_ID + " TEXT, " +
                        TrailerEntry.COLUMN_KEY_OF_TRAILER + " TEXT NOT NULL, " +
                        " FOREIGN KEY(" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                        + MovieContract.FavMovieEntry.TABLE_NAME + "(" + MovieContract.FavMovieEntry.COLUMN_MOVIE_ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_CACHE_MOVIE_MOST_POPULAR_TABLE =
                "CREATE TABLE " + CacheMovieMostPopularEntry.TABLE_NAME + " (" +
                        CacheMovieMostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CacheMovieMostPopularEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_A_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        CacheMovieMostPopularEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +

                        " UNIQUE (" + CacheMovieMostPopularEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_CACHE_MOVIE_MOST_POPULAR_TABLE);

        final String SQL_CREATE_CACHE_MOVIE_TOP_RATED_TABLE =
                "CREATE TABLE " + CacheMovieTopRatedEntry.TABLE_NAME + " (" +

                        CacheMovieTopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CacheMovieTopRatedEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_A_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        CacheMovieTopRatedEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                        " UNIQUE (" + CacheMovieTopRatedEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_CACHE_MOVIE_TOP_RATED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
