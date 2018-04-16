package com.example.infolabsolution.thelastsubmission;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.infolabsolution.thelastsubmission.PersistFavMovie;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PersistFavService extends JobService {

    private static final String TAG = PersistFavService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {


        PersistFavMovie.persistFavMovie(this);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
          return true;
    }
}
