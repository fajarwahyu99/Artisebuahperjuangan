package com.example.infolabsolution.thelastsubmission;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.infolabsolution.thelastsubmission.Movie;
import timber.log.Timber;

public class MovieJsonUtils {

    public static List<Movie> extractResultsFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();


        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject currentMovie = movieArray.getJSONObject(i);

                String poster_path = currentMovie.getString("poster_path");

                String original_title = currentMovie.getString("original_title");

                String movie_poster_image_thumbnail = currentMovie.getString("backdrop_path");

                String a_plot_synopsis = currentMovie.getString("overview");

                String user_rating = currentMovie.getString("vote_average");

                String release_date = currentMovie.getString("release_date");

                String id = currentMovie.getString("id");

                Movie movie = new Movie(poster_path, original_title, movie_poster_image_thumbnail
                        , a_plot_synopsis, user_rating, release_date, id);

                movies.add(movie);
            }
        } catch (JSONException e) {
            Timber.e(e, "Failed to pass Movie JSON.");
        }

        return movies;
    }
}
