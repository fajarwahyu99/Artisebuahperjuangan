package com.example.infolabsolution.thelastsubmission;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.infolabsolution.thelastsubmission.DeleteExternalFolderExtraPic;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DeleteExtraMoviePicService extends JobService {

    static final String TAG = DeleteExtraMoviePicService.class.getSimpleName();
    @Override
    public boolean onStartJob(JobParameters params) {

        DeleteExternalFolderExtraPic.deleteExtraMoviePosterFilePic(this);
        DeleteExternalFolderExtraPic.deleteExtraMovieThumbnailFilePic(this);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
