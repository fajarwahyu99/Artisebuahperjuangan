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

        Log.i(TAG, "Halloooooooooo, jag ar pa start job upcoming vag.");

        mPersistPopMovieTask = new PersistPopMovieTask(this) {
            @Override
            protected void onPostExecute(List<Movie> movieData) {
                jobFinished(params, false);
                Log.i(TAG, "Halloooooooooo, jag ar pa finish job upcoming vag.");
            }
        };

        mPersistPopMovieTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPersistPopMovieTask != null) {
            mPersistPopMovieTask.cancel(true);
        }
        Log.i(TAG, "Halloooooooooo, jag ar pa stop job upcoming vag.");
        return true;
    }
}

