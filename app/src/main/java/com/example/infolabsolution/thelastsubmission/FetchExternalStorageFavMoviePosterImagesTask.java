package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieBasicInfo;
import com.example.infolabsolution.thelastsubmission.MainActivity;
import com.example.infolabsolution.thelastsubmission.ExternalPathUtils;

/**
 * Created by jane on 17-5-31.
 */

public class FetchExternalStorageFavMoviePosterImagesTask extends AsyncTask<MovieBasicInfo, Void, String> {

    private Context context;

    public FetchExternalStorageFavMoviePosterImagesTask(Context context) {
        this.context = context;
    }

    private static final String TAG = FetchExternalStorageFavMoviePosterImagesTask.class.getSimpleName();

    String urlToBeDownloaded;

    String movieId;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(MovieBasicInfo... params) {

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (params.length == 0) {
            return null;
        }

        urlToBeDownloaded = params[0].getmExternalUrl();

        movieId = params[0].getmId();

        try {

            URL url = new URL(urlToBeDownloaded);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            // Nothing special, just want to give every file a different file name, according to their pass-in url string.
            String[] parts = urlToBeDownloaded.split("/");
            String lastPart = parts[7];
            String filename = lastPart;
            Log.i(TAG, context.getString(R.string.log_information_message_download_filename) + filename);

            File file = new File(ExternalPathUtils.getExternalPathBasicFileName(this.context) + "/cacheposters/" + filename);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                boolean fileCreated = file.createNewFile();
                Log.i(TAG, "creating new file: " + file.getAbsolutePath() + ", result: " + fileCreated);
            } else {
                Log.i(TAG, "File already exists: " + file.getAbsolutePath());
            }

            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i(TAG, context.getString(R.string.log_information_message_download_downloadedSize)
                        + downloadedSize + context.getString(R.string.log_information_message_download_totalSize)
                        + totalSize);
            }
            inputStream.close();
            fileOutput.close();
            if (downloadedSize == totalSize) {
                filepath = file.getPath();
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            filepath = null;
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, context.getString(R.string.log_information_message_download_filepath) + filepath);
        return filepath;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            Log.i(TAG, "Insert external poster path: " + s + " into fav movie folder successful.");
        } else {
            Log.e(TAG, context.getString(R.string.log_error_message_offline_before_download_pics_finish));
            String expectedMsg = context.getString(R.string.toast_message_offline_before_download_finish);

            if (MainActivity.mToast != null) {
                String displayedText = ((TextView) ((LinearLayout) MainActivity.mToast.getView())
                        .getChildAt(0)).getText().toString();
                if (!displayedText.equals(expectedMsg)) {
                    MainActivity.mToast.cancel();
                    MainActivity.mToast = Toast.makeText(context, context.getString(R.string.toast_message_offline_before_download_finish), Toast.LENGTH_SHORT);
                    MainActivity.mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    MainActivity.mToast.show();
                }
            } else {
                MainActivity.mToast = Toast.makeText(context, expectedMsg, Toast.LENGTH_SHORT);
                MainActivity.mToast.setGravity(Gravity.BOTTOM, 0, 0);
                MainActivity.mToast.show();
            }
        }
    }
}
