package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import com.example.infolabsolution.thelastsubmission.NotificationTasks;


public class NotificationFirebaseJobService extends JobService {

    private static final String TAG = NotificationFirebaseJobService.class.getSimpleName();

    private AsyncTask mBackgroundTask;


    @Override
    public boolean onStartJob(final JobParameters params) {

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = NotificationFirebaseJobService.this;
                NotificationTasks.executeTask(context, NotificationTasks.ACTION_NOTIFY);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
