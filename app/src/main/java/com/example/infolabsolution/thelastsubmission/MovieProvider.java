package com.example.infolabsolution.thelastsubmission;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieMostPopularEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieTopRatedEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.ReviewEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.TrailerEntry;

public class MovieProvider extends ContentProvider {

    public static final String TAG = MovieProvider.class.getSimpleName();

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static final int REVIEWS = 200;
    private static final int REVIEW_ID = 201;
    private static final int TRAILERS = 300;
    private static final int TRAILER_ID = 301;
    private static final int CACHE_MOVIES_MOST_POPULAR = 400;
    private static final int CACHE_MOVIES_TOP_RATED = 500;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEWS);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEW_ID);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILERS);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/#", TRAILER_ID);

        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_CACHE_MOVIE_MOST_POPULAR, CACHE_MOVIES_MOST_POPULAR);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_CACHE_MOVIE_TOP_RATED, CACHE_MOVIES_TOP_RATED);
    }


    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {

        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mMovieDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                cursor = database.query(
                        MovieContract.FavMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_ID:

                selection = FavMovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(FavMovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REVIEWS:
                cursor = database.query(
                        ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW_ID:
                selection = ReviewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRAILERS:
                cursor = database.query(
                        TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILER_ID:
                selection = TrailerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CACHE_MOVIES_MOST_POPULAR:
                cursor = database.query(CacheMovieMostPopularEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CACHE_MOVIES_TOP_RATED:
                cursor = database.query(CacheMovieTopRatedEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.query_default_illegal_argument_exception_message) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return FavMovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return FavMovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return ReviewEntry.CONTENT_LIST_TYPE;
            case REVIEW_ID:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return TrailerEntry.CONTENT_LIST_TYPE;
            case TRAILER_ID:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case CACHE_MOVIES_MOST_POPULAR:
                return CacheMovieMostPopularEntry.CONTENT_LIST_TYPE;
            case CACHE_MOVIES_TOP_RATED:
                return CacheMovieTopRatedEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.get_type_default_illegal_argument_exception_message_part_one)
                        + uri + getContext().getString(R.string.get_type_default_illegal_argument_exception_message_part_two) + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        long id;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                id = database.insert(FavMovieEntry.TABLE_NAME, null, values);
                break;
            case REVIEWS:
                id = database.insert(ReviewEntry.TABLE_NAME, null, values);
                break;
            case TRAILERS:
                id = database.insert(TrailerEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.insert_default_illegal_argument_exception_message) + uri);
        }

        if (id == -1) {
            Log.e(TAG, getContext().getResources().getString(R.string.insert_fail_log_message) + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsDeleted;

        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                rowsDeleted = database.delete(FavMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = FavMovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FavMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = database.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW_ID:
                selection = ReviewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = database.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER_ID:
                selection = TrailerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CACHE_MOVIES_MOST_POPULAR:
                rowsDeleted = database.delete(CacheMovieMostPopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CACHE_MOVIES_TOP_RATED:
                rowsDeleted = database.delete(CacheMovieTopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_for_deletion) + uri);
        }


        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        int rowsUpdated;

        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                rowsUpdated = database.update(FavMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CACHE_MOVIES_MOST_POPULAR:
                rowsUpdated = database.update(CacheMovieMostPopularEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CACHE_MOVIES_TOP_RATED:
                rowsUpdated = database.update(CacheMovieTopRatedEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.unknown_uri_for_update) + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
         SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CACHE_MOVIES_MOST_POPULAR:
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = database.insert(CacheMovieMostPopularEntry.TABLE_NAME, null,
                                value);
                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case CACHE_MOVIES_TOP_RATED:
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = database.insert(CacheMovieTopRatedEntry.TABLE_NAME, null,
                                value);
                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
