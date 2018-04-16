package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MovieBasicInfo;
import com.example.infolabsolution.thelastsubmission.FetchExternalStorageFavMovieImageThumbnailsTask;
import com.example.infolabsolution.thelastsubmission.FetchExternalStorageFavMoviePosterImagesTask;
import com.example.infolabsolution.thelastsubmission.MainActivity;
import com.example.infolabsolution.thelastsubmission.ExternalPathUtils;

public class PersistFavMovie {

    private static final int POSTER_UP_TO_DATE = 111;
    private static final int THUMBNAIL_UP_TO_DATE = 112;

    private static int mPosterUpToDateRecordNumber;
    private static int mThumbnailUpToDateRecordNumber;

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_W780 = "w780/";
    private static final String IMAGE_SIZE_W185 = "w185/";
    private static final String CACHE_POSTERS_FOLDER_NAME = "/cacheposters/";
    private static final String CACHE_THUMBNAILS_FOLDER_NAME = "/cachethumbnails/";

    private static final String TAG = PersistFavMovie.class.getSimpleName();

    public static void persistFavMovie(Context context) {



        Boolean enableOffline = getEnableOfflinePreference(context);
        if (enableOffline == true) {
            downloadExtraFavMoviePosterFilePic(context);
            downloadExtraFavMovieImageThumbnailFilePic(context);

            if (MainActivity.mShowToast) {
                if (mPosterUpToDateRecordNumber == POSTER_UP_TO_DATE && mThumbnailUpToDateRecordNumber == THUMBNAIL_UP_TO_DATE) {
                    if (MainActivity.mToast != null) {
                        MainActivity.mToast.cancel();
                    }
                    MainActivity.mToast = Toast.makeText(context, context.getString(R.string.toast_message_refresh_fav_up_to_date), Toast.LENGTH_SHORT);
                    MainActivity.mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    MainActivity.mToast.show();
                }
                MainActivity.mShowToast = false;
            }
        }
    }

    public static void downloadExtraFavMoviePosterFilePic(Context context) {

        File postersMoviePicsFolder
                = new File(ExternalPathUtils.getExternalPathBasicFileName(context)
                + CACHE_POSTERS_FOLDER_NAME);

        if (postersMoviePicsFolder.exists()) {

            String[] fileNameArray = new String[postersMoviePicsFolder.listFiles().length];
              int j = 0;
            for (File pic : postersMoviePicsFolder.listFiles()) {
                String fileName = "/" + pic.getName();
                fileNameArray[j] = fileName;
                 j++;
            }

            String[] projection = {FavMovieEntry.COLUMN_MOVIE_ID, FavMovieEntry.COLUMN_POSTER_PATH};
            Cursor cursor = context.getContentResolver().query(
                    FavMovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            cursor.moveToFirst();

            int i = 0;
            String[] newDataArray = new String[cursor.getCount()];

            while (!cursor.isAfterLast()) {
                String currentPosterPath = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_POSTER_PATH));
                newDataArray[i] = currentPosterPath;
                i++;
                String currentMovieId = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_ID));
                if (!Arrays.asList(fileNameArray).contains(currentPosterPath)) {
                     String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W185)
                            .concat(currentPosterPath);
                    new FetchExternalStorageFavMoviePosterImagesTask(context).execute(
                            new MovieBasicInfo(currentMovieId, fullMoviePosterForOneMovie));
                }
                cursor.moveToNext();
            }
            cursor.close();

            if (Arrays.asList(fileNameArray).containsAll(Arrays.asList(newDataArray))) {
                mPosterUpToDateRecordNumber = POSTER_UP_TO_DATE;
            }
        } else {
            String[] projection = {FavMovieEntry.COLUMN_MOVIE_ID, FavMovieEntry.COLUMN_POSTER_PATH};
            Cursor cursor = context.getContentResolver().query(
                    FavMovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String currentPosterPath = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_POSTER_PATH));
                String currentMovieId = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_ID));
                    String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W185)
                        .concat(currentPosterPath);
                new FetchExternalStorageFavMoviePosterImagesTask(context).execute(
                        new MovieBasicInfo(currentMovieId, fullMoviePosterForOneMovie));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public static void downloadExtraFavMovieImageThumbnailFilePic(Context context) {

        File thumbnailsMoviePicsFolder
                = new File(ExternalPathUtils.getExternalPathBasicFileName(context)
                + CACHE_THUMBNAILS_FOLDER_NAME);

        if (thumbnailsMoviePicsFolder.exists()) {

            String[] fileNameArray = new String[thumbnailsMoviePicsFolder.listFiles().length];
             int j = 0;
            for (File pic : thumbnailsMoviePicsFolder.listFiles()) {
                String fileName = "/" + pic.getName();
                fileNameArray[j] = fileName;
                 j++;
            }

            String[] projection = {FavMovieEntry.COLUMN_MOVIE_ID, FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL};
            Cursor cursor = context.getContentResolver().query(
                    FavMovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            cursor.moveToFirst();

            int i = 0;
            String[] newDataArray = new String[cursor.getCount()];

            while (!cursor.isAfterLast()) {
                String currentImageThumbnail = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
                newDataArray[i] = currentImageThumbnail;
                i++;
                String currentMovieId = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_ID));
                if (!Arrays.asList(fileNameArray).contains(currentImageThumbnail)) {
                     String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W780)
                            .concat(currentImageThumbnail);
                    new FetchExternalStorageFavMovieImageThumbnailsTask(context).execute(
                            new MovieBasicInfo(currentMovieId, fullMoviePosterForOneMovie));
                }
                cursor.moveToNext();
            }
            cursor.close();

            if (Arrays.asList(fileNameArray).containsAll(Arrays.asList(newDataArray))) {
                mThumbnailUpToDateRecordNumber = THUMBNAIL_UP_TO_DATE;

            }
        } else {
            String[] projection = {FavMovieEntry.COLUMN_MOVIE_ID, FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL};
            Cursor cursor = context.getContentResolver().query(
                    FavMovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String currentImageThumbnail = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
                String currentMovieId = cursor.getString(cursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_ID));
                String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W780)
                        .concat(currentImageThumbnail);
                new FetchExternalStorageFavMovieImageThumbnailsTask(context).execute(
                        new MovieBasicInfo(currentMovieId, fullMoviePosterForOneMovie));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private static boolean getEnableOfflinePreference(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(
                context.getString(R.string.pref_enable_offline_key),
                context.getResources().getBoolean(R.bool.pref_enable_offline_default));
    }
}
