package com.example.infolabsolution.thelastsubmission;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieMostPopularEntry;
import com.example.infolabsolution.thelastsubmission.Movie;
import com.example.infolabsolution.thelastsubmission.DetailActivity;

public class WatchMovieNotificationUtils {

    private static final String TAG = WatchMovieNotificationUtils.class.getSimpleName();

    private static final int WATCH_MOVIE_NOTIFICATION_ID = 1135;
    private static final int WATCH_MOVIE_PENDING_INTENT_ID = 3577;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(WATCH_MOVIE_NOTIFICATION_ID);
    }

    public static void notifyUserWatchMovie(Context context) {
        Log.i(TAG, "Alarm: Watch movie notify !");

        String movie_title = "Interstellar";
        String tv_channel = "TV4 Film";

        String contentText = String.format(context.getString(R.string.format_watch_movie_notification), movie_title, tv_channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification_small_icon)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.watch_movie_notification_title))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(WATCH_MOVIE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Movie getTheMovieOut(Context context) {

        Movie movie = null;
        String selection = CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE + "=?";
        String[] selectionArgs = {"Interstellar"};
        Cursor cursor = context.getContentResolver().query(
                CacheMovieMostPopularEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String poster_path = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_POSTER_PATH));
            String original_title = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE));
            String movie_poster_image_thumbnail =
                    cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
            String a_plot_synopsis = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_A_PLOT_SYNOPSIS));
            String user_rating = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_USER_RATING));
            String release_date = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_RELEASE_DATE));
            String id = cursor.getString(cursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_MOVIE_ID));

            movie = new Movie(poster_path, original_title, movie_poster_image_thumbnail
                    , a_plot_synopsis, user_rating, release_date, id);

            cursor.close();
        }

        return movie;
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_notification_large_icon);
        return largeIcon;
    }

    private static PendingIntent contentIntent(Context context) {
        Movie movie = getTheMovieOut(context);
        Intent startActivityIntent = new Intent(context, DetailActivity.class);
        startActivityIntent.putExtra("movie", movie);
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(startActivityIntent)
                .getPendingIntent(WATCH_MOVIE_PENDING_INTENT_ID, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

