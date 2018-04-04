package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;


public class ScheduleNotificationLowerVersion {

    private static boolean sInitialized;

    private static final String NOTIFICATION_JOB_TAG = "notification_job_tag";
    private static final String TAG = ScheduleNotificationLowerVersion.class.getSimpleName();
    private static final int NOTIFICATION_INTERVAL_MINUTES = 24 * 60;
    private static final int NOTIFICATION_INTERVAL_SECONDS
            = (int) (TimeUnit.MINUTES.toSeconds(NOTIFICATION_INTERVAL_MINUTES));
    private static final int NOTIFICATION_FLEXTIME_SECONDS = 10;

    synchronized public static void scheduleNotification(@NonNull final Context context) {
        Log.i(TAG, "jag support lower version schedule notification!");

        if (sInitialized) {
            return;
        }

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintNotificationJob = dispatcher.newJobBuilder()
                .setService(NotificationFirebaseJobService.class)
                .setTag(NOTIFICATION_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        NOTIFICATION_INTERVAL_SECONDS,
                        NOTIFICATION_INTERVAL_SECONDS + NOTIFICATION_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintNotificationJob);
        sInitialized = true;
    }
}
