package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.util.Log;


public class NotificationTasks {

    private final static String TAG = NotificationTasks.class.getSimpleName();

    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_NOTIFY = "notify";

    public static void executeTask(Context context, String action) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {

            PopBestMovieNotificationUtils.clearAllNotifications(context);
        } else if (ACTION_NOTIFY.equals(action)) {

            notifyUser(context);
        }
    }

    private static void notifyUser(Context context) {
        PopBestMovieNotificationUtils.notifyUserHighestRatePopularMovie(context);
    }
}
