package com.example.infolabsolution.thelastsubmission;

/**
 * Created by jane on 17-4-8.
 */

/**
 * Represents a Review.
 * It contains the author string and the review content string of a movie review.
 */
public class Review {

    // Author of the review
    private String mAuthor;

    // Content of the review
    private String mReviewContent;

    /**
     * Constructs a new {@link Review} object.
     *
     * @param author  is the author of the review
     * @param content is the content of the review
     */
    public Review(String author, String content) {
        mAuthor = author;
        mReviewContent = content;
    }

    // Gets the author of the review
    public String getAuthor() {
        return mAuthor;
    }

    // Gets the content of the review
    public String getReviewContent() {
        return mReviewContent;
    }

    @Override
    public String toString() {
        return "Review{" +
                "mAuthor='" + mAuthor + '\'' +
                ", mReviewContent='" + mReviewContent + '\'' +
                '}';
    }
}
