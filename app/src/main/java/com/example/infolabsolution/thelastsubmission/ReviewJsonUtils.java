package com.example.infolabsolution.thelastsubmission;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.infolabsolution.thelastsubmission.Review;
import timber.log.Timber;

public class ReviewJsonUtils {

    public static List<Review> extractResultsFromMovieReviewJson(String movieReviewJSON) {

        if (TextUtils.isEmpty(movieReviewJSON)) {
            return Collections.emptyList();
        }

        List<Review> reviews = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(movieReviewJSON);
            JSONArray reviewArray = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject currentReview = reviewArray.getJSONObject(i);
                String author = currentReview.getString("author");
                String content = currentReview.getString("content");
                Review review = new Review(author, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            Timber.e(e, "Failed to pass Review JSON.");
        }
        return reviews;
    }
}
