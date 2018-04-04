package com.example.infolabsolution.thelastsubmission;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.infolabsolution.thelastsubmission.NotificationTasks;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationService extends JobService {

    final static String TAG = NotificationService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i(TAG, "Halloooooooooo, jag ar pa start job notification vag.");

        Context context = NotificationService.this;
        NotificationTasks.executeTask(context, NotificationTasks.ACTION_NOTIFY);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "Halloooooooooo, jag ar pa stop job notification vag.");
        return true;
    }
}
