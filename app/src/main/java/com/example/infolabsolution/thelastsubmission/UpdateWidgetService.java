package com.example.infolabsolution.thelastsubmission;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UpdateWidgetService extends JobService {

    public static final String ACTION_DATA_UPDATED = "com.example.infolabsolution.thelastsubmission.ACTION_DATA_UPDATED";

    final static String TAG = UpdateWidgetService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        this.sendBroadcast(dataUpdatedIntent);
        return false;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
         return true;
    }
}
