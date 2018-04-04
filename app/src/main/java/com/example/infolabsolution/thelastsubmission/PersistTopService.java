package com.example.infolabsolution.thelastsubmission;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

import com.example.infolabsolution.thelastsubmission.PersistTopMovieTask;
import com.example.infolabsolution.thelastsubmission.Movie;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PersistTopService extends JobService {

    private static final String TAG = PersistTopService.class.getSimpleName();

    PersistTopMovieTask mPersistTopMovieTask;

    @Override
    public boolean onStartJob(final JobParameters params) {

//        Log.i(TAG, "Halloooooooooo, jag ar pa start job top vag.");

        mPersistTopMovieTask = new PersistTopMovieTask(this) {
            @Override
            protected void onPostExecute(List<Movie> movieData) {
                jobFinished(params, false);
                Log.i(TAG, "Halloooooooooo, jag ar pa finish job top vag.");
            }
        };

        mPersistTopMovieTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPersistTopMovieTask != null) {
            mPersistTopMovieTask.cancel(true);
        }
        Log.i(TAG, "Halloooooooooo, jag ar pa stop job top vag.");
        return true;
    }
}

