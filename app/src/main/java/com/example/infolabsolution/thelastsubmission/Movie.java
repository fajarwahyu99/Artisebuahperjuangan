package com.example.infolabsolution.thelastsubmission;

import java.io.Serializable;


public class Movie implements Serializable {

    private String mPosterPath;
    private String mOriginalTitle;
    private String mMoviePosterImageThumbnail;
    private String mAPlotSynopsis;
    private String mUserRating;
    private String mReleaseDate;
    private String mId;

    public Movie(String posterPath, String originalTitle, String moviePosterImageThumbnail, String aPlotSynopsis,
                 String userRating, String releaseDate, String id) {
        mPosterPath = posterPath;
        mOriginalTitle = originalTitle;
        mMoviePosterImageThumbnail = moviePosterImageThumbnail;
        mAPlotSynopsis = aPlotSynopsis;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
        mId = id;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public String getOriginalTitle() {
        return mOriginalTitle;
    }
    public String getMoviePosterImageThumbnail() {
        return mMoviePosterImageThumbnail;
    }
    public String getAPlotSynopsis() {
        return mAPlotSynopsis;
    }
    public String getUserRating() {
        return mUserRating;
    }
    public String getReleaseDate() {
        return mReleaseDate;
    }
    public String getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mPosterPath='" + mPosterPath + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                ", mMoviePosterImageThumbnail='" + mMoviePosterImageThumbnail + '\'' +
                ", mAPlotSynopsis='" + mAPlotSynopsis + '\'' +
                ", mUserRating='" + mUserRating + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mId='" + mId + '\'' +
                '}';
    }
}

