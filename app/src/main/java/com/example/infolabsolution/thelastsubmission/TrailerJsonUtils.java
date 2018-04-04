package com.example.infolabsolution.thelastsubmission;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.infolabsolution.thelastsubmission.Trailer;
import timber.log.Timber;


public class TrailerJsonUtils {

    public static List<Trailer> extractResultsFromMovieTrailerJson(String movieTrailerJSON) {

        if (TextUtils.isEmpty(movieTrailerJSON)) {
            return Collections.emptyList();
        }

        List<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(movieTrailerJSON);

            JSONArray trailerArray = baseJsonResponse.getJSONArray("youtube");
            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject currentTrailer = trailerArray.getJSONObject(i);
                String key = currentTrailer.getString("source");
                Trailer trailer = new Trailer(key);
                trailers.add(trailer);
            }
        } catch (JSONException e) {
            Timber.e(e, "Failed to pass Trailer JSON.");
        }
        return trailers;
    }
}


