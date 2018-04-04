package com.example.infolabsolution.thelastsubmission;


public class Review {


    private String mAuthor;

    private String mReviewContent;
    public Review(String author, String content) {
        mAuthor = author;
        mReviewContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

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
