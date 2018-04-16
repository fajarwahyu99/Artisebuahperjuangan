package com.example.infolabsolution.thelastsubmission;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

import com.example.infolabsolution.thelastsubmission.PersistPopMovieTask;
import com.example.infolabsolution.thelastsubmission.Movie;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PersistPopService extends JobService {

    private static final String TAG = PersistPopService.class.getSimpleName();

    PersistPopMovieTask mPersistPopMovieTask;


    @Override
    public boolean onStartJob(final JobParameters params) {

        mPersistPopMovieTask = new PersistPopMovieTask(this) {
            @Override
            protected void onPostExecute(List<Movie> movieData) {
                jobFinished(params, false);

            }
        };

        mPersistPopMovieTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPersistPopMovieTask != null) {
            mPersistPopMovieTask.cancel(true);
        }   return true;
    }
}

