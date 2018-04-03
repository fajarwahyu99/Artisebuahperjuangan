package com.example.infolabsolution.thelastsubmission;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

import com.example.infolabsolution.thelastsubmission.PersistTopMovieTask;
import com.example.infolabsolution.thelastsubmission.Movie;

/**
 * Created by jane on 17-7-10.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PersistTopService extends JobService {

    private static final String TAG = PersistTopService.class.getSimpleName();

    PersistTopMovieTask mPersistTopMovieTask;

    /**
     * @return true because the job in onStartJob is long, and on the other thread, won't be finish
     * in a second. We should let the system know that the job needs time to finish, but override
     * onPostExecute() to let the system know that the job is done. And after the periodic intervals,
     * the onStartJob() will be fired up again.
     */
    @Override
    public boolean onStartJob(final JobParameters params) {

        Log.i(TAG, "Halloooooooooo, jag ar pa start job top vag.");

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

    /**
     * Because background task was executed, that will need to be canceled if it is still
     * running. This is where you want to be very careful, because any lingering threads could
     * create a memory leak in the app! So clean up the code !
     *
     * @return true so if something happens, and the job stops in the middle, it will reschedule.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPersistTopMovieTask != null) {
            mPersistTopMovieTask.cancel(true);
        }
        Log.i(TAG, "Halloooooooooo, jag ar pa stop job top vag.");
        return true;
    }
}

