package com.example.infolabsolution.thelastsubmission;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.ReviewEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.TrailerEntry;
import com.example.infolabsolution.thelastsubmission.MovieDbHelper;
import com.example.infolabsolution.thelastsubmission.databinding.ActivityDetailBinding;
import com.example.infolabsolution.thelastsubmission.Movie;
import com.example.infolabsolution.thelastsubmission.Review;
import com.example.infolabsolution.thelastsubmission.ReviewAdapter;
import com.example.infolabsolution.thelastsubmission.FetchReviewTask;
import com.example.infolabsolution.thelastsubmission.FetchTrailerTask;
import com.example.infolabsolution.thelastsubmission.Trailer;
import com.example.infolabsolution.thelastsubmission.TrailerAdapter;
import com.example.infolabsolution.thelastsubmission.TrailerAdapter.TrailerAdapterOnClickHandler;
import com.example.infolabsolution.thelastsubmission.ExternalPathUtils;



public class DetailActivity extends AppCompatActivity implements TrailerAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int SAVE_MOVIE_SUCCESS = 10;
    private static final int SAVE_MOVIE_FAIL = 11;
    private static final int SAVE_REVIEW_SUCCESS = 20;
    private static final int SAVE_REVIEW_FAIL = 21;
    private static final int SAVE_TRAILER_SUCCESS = 50;
    private static final int SAVE_TRAILER_FAIL = 51;

    private static final int DELETE_MOVIE_SUCCESS = 30;
    private static final int DELETE_MOVIE_FAIL = 31;
    private static final int DELETE_REVIEW_SUCCESS = 40;
    private static final int DELETE_REVIEW_FAIL = 41;
    private static final int DELETE_TRAILER_SUCCESS = 60;
    private static final int DELETE_TRAILER_FAIL = 61;

    private int saveMovieRecordNumber;
    private int saveReviewRecordNumber;

    private int deleteMovieRecordNumber;
    private int deleteReviewRecordNumber;

    private int saveTrailerRecordNumber;
    private int deleteTrailerRecordNumber;

    private final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE_W780 = "w780/";
    private final String BASE_YOUTUBE_URL_APP = "vnd.youtube:";
    private final String BASE_YOUTUBE_URL_WEB = "http://www.youtube.com/watch?v=";
    private final String CACHE_THUMBNAILS_FOLDER_NAME = "/cachethumbnails/";

    private Movie mCurrentMovie;

    private List<Review> mCurrentMovieReviews;

    private List<Trailer> mCurrentMovieTrailers;

    private RecyclerView mReviewRecyclerView;

    private RecyclerView mTrailerRecyclerView;

    private ReviewAdapter mReviewAdapter;

    private TrailerAdapter mTrailerAdapter;

    private String mNumberOfReviewString;

    private String mNumberOfTrailerString;

    private String mFirstTrailerSourceKey;

    private FloatingActionButton mFabButton;

    private ProgressBar mLoadingIndicator;

    private MovieDbHelper mMovieDbHelper;

    private ActivityDetailBinding mDetailBinding;

    private Toast mToast;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ActivityDetailBinding getmDetailBinding() {
        return mDetailBinding;
    }

    public void setmCurrentMovieReviews(List<Review> mCurrentMovieReviews) {
        this.mCurrentMovieReviews = mCurrentMovieReviews;
    }

    public void setmCurrentMovieTrailers(List<Trailer> mCurrentMovieTrailers) {
        this.mCurrentMovieTrailers = mCurrentMovieTrailers;
    }

    public ReviewAdapter getmReviewAdapter() {
        return mReviewAdapter;
    }

    public TrailerAdapter getmTrailerAdapter() {
        return mTrailerAdapter;
    }

    public void setmNumberOfReviewString(String mNumberOfReviewString) {
        this.mNumberOfReviewString = mNumberOfReviewString;
    }

    public void setmNumberOfTrailerString(String mNumberOfTrailerString) {
        this.mNumberOfTrailerString = mNumberOfTrailerString;
    }

    public void setmFirstTrailerSourceKey(String mFirstTrailerSourceKey) {
        this.mFirstTrailerSourceKey = mFirstTrailerSourceKey;
    }

    public Toast getmToast() {
        return mToast;
    }

    public void setmToast(Toast mToast) {
        this.mToast = mToast;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie")) {
                mCurrentMovie = (Movie) getIntent().getExtras().getSerializable("movie");
            }
        }
        setTitle(mCurrentMovie.getOriginalTitle());
        Animation a = AnimationUtils.loadAnimation(this, R.anim.progress_animation_main);
        a.setDuration(1000);
        mDetailBinding.primaryInfo.ivLoading.startAnimation(a);

        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            setCurrentMovieImageThumbnailOnLine();
        } else {
            setCurrentMovieImageThumbnailOffLine();
        }
        if (mCurrentMovie.getOriginalTitle().contains(":")) {
            String[] separated = mCurrentMovie.getOriginalTitle().split(":");
            mDetailBinding.primaryInfo.tvMovieTitle.setText(separated[0] + ":" + "\n" + separated[1].trim());
        } else {
            mDetailBinding.primaryInfo.tvMovieTitle.setText(mCurrentMovie.getOriginalTitle());
        }
        mDetailBinding.primaryInfo.tvUserRating.setText(mCurrentMovie.getUserRating());
        mDetailBinding.primaryInfo.tvReleaseDate.setText(mCurrentMovie.getReleaseDate());
        mDetailBinding.primaryInfo.tvAPlotSynopsis.setText(mCurrentMovie.getAPlotSynopsis());
        mMovieDbHelper = new MovieDbHelper(this);

        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);

        mReviewRecyclerView = mDetailBinding.extraDetails.recyclerviewMovieReviews;

        mReviewRecyclerView.setLayoutManager(layoutManagerReviews);

        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        loadReviewData(mCurrentMovie.getId());

        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false);

        mTrailerRecyclerView = mDetailBinding.extraDetails.recyclerviewMovieTrailers;

        mTrailerRecyclerView.setLayoutManager(layoutManagerTrailers);
        mTrailerAdapter = new TrailerAdapter(this, this);

        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        loadTrailerData(mCurrentMovie.getId());
        mDetailBinding.primaryInfo.ivMoviePosterImageThumbnail.setContentDescription(mCurrentMovie.getOriginalTitle());
        View primaryLayout = findViewById(R.id.primary_info);
        mFabButton = (FloatingActionButton) primaryLayout.findViewById(R.id.fab_favorite);

        mFabButton.setColorFilter(ContextCompat.getColor(DetailActivity.this, setFabButtonStarColor()));

        if (setFabButtonStarColor() == R.color.colorWhiteFavoriteStar) {
            mFabButton.setContentDescription(getString(R.string.a11y_detail_activity_save_floating_button));
        } else {
            mFabButton.setContentDescription(getString(R.string.a11y_detail_activity_unsave_floating_button));
        }

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setFabButtonStarColor() == R.color.colorWhiteFavoriteStar) {
                    mFabButton.setColorFilter(ContextCompat.getColor(DetailActivity.this, R.color.colorYellowFavoriteStar));
                    mFabButton.setContentDescription(getString(R.string.a11y_detail_activity_unsave_floating_button));
                    try {
                        saveMovie();
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    mFabButton.setColorFilter(ContextCompat.getColor(DetailActivity.this, R.color.colorWhiteFavoriteStar));
                    mFabButton.setContentDescription(getString(R.string.a11y_detail_activity_save_floating_button));
                    deleteMovie();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        refreshMovie();
                    }
                }
        );
        int swipeRefreshBgColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(swipeRefreshBgColor);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorWhiteFavoriteStar,
                R.color.trailer10,
                R.color.trailer9,
                R.color.trailer8,
                R.color.trailer7,
                R.color.trailer6,
                R.color.trailer5,
                R.color.trailer4,
                R.color.trailer3,
                R.color.trailer2,
                R.color.trailer1,
                R.color.trailer0);
    }

    private void setCurrentMovieImageThumbnailOffLine() {
        final File pathToPic;
        String currentMovieImageThumbnail = mCurrentMovie.getMoviePosterImageThumbnail();
        String baseThumbnailExternalUrl = ExternalPathUtils.getExternalPathBasicFileName(this)
                + CACHE_THUMBNAILS_FOLDER_NAME;
        String fullMovieImageThumbnailForOneMovie = baseThumbnailExternalUrl
                .concat(currentMovieImageThumbnail);
        pathToPic = new File(fullMovieImageThumbnailForOneMovie);

        Picasso.with(DetailActivity.this)
                .load(pathToPic)
                .error(R.drawable.pic_error_loading_1560_878)
                .into(mDetailBinding.primaryInfo.ivMoviePosterImageThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        if (pathToPic.exists()) {
                            pathToPic.delete();
                        }
                    }
                });
    }

    private void setCurrentMovieImageThumbnailOnLine() {
        String currentMoviePosterImageThumbnail = BASE_IMAGE_URL.concat(IMAGE_SIZE_W780)
                .concat(mCurrentMovie.getMoviePosterImageThumbnail());
        Picasso.with(DetailActivity.this)
                .load(currentMoviePosterImageThumbnail)
                .error(R.drawable.pic_error_loading_1560_878)
                .into(mDetailBinding.primaryInfo.ivMoviePosterImageThumbnail);
    }

    private void refreshMovie() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            setCurrentMovieImageThumbnailOnLine();
            mReviewRecyclerView.setVisibility(View.VISIBLE);
            mTrailerRecyclerView.setVisibility(View.VISIBLE);
            loadReviewData(mCurrentMovie.getId());
            loadTrailerData(mCurrentMovie.getId());
        } else {
            hideLoadingIndicators();
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(DetailActivity.this, getString(R.string.toast_message_refresh_no_internet), Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        }
    }
    private void loadReviewData(String id) {
        try {
            boolean movieIsInDatabase = checkIsMovieAlreadyInFavDatabase(id);
            if (movieIsInDatabase) {
                loadReviewDataFromDatabase(id);
            } else {
                NetworkInfo networkInfo = getNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new FetchReviewTask(this).execute(id);
                } else {
                    hideLoadingIndicators();
                    setNumberOfReviewTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                }
            }
        } catch (NullPointerException e) {
        }
    }

    public void hideLoadingIndicators() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadTrailerData(String id) {

        try {
            boolean movieIsInDatabase = checkIsMovieAlreadyInFavDatabase(id);
            if (movieIsInDatabase) {
                loadTrailerDataFromDatabase(id);
            } else {
                NetworkInfo networkInfo = getNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new FetchTrailerTask(this).execute(id);
                } else {
                    hideLoadingIndicators();
                    setNumberOfTrailerTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                }
            }
        } catch (NullPointerException e) {
        }
    }

    public void setTrailersLoadingIndicator() {
        mDetailBinding.extraDetails.tvNumberOfTrailer.setVisibility(View.INVISIBLE);
        mDetailBinding.extraDetails.ivTrailerLoadingIndicator.setVisibility(View.VISIBLE);
        Animation b = AnimationUtils.loadAnimation(this, R.anim.progress_animation_main);
        b.setDuration(1000);
        mDetailBinding.extraDetails.ivTrailerLoadingIndicator.startAnimation(b);
    }

    public void setNumberOfTrailerTextViewText(String numberOfTrailerTextViewText) {
        mDetailBinding.extraDetails.ivTrailerLoadingIndicator.clearAnimation();
        mDetailBinding.extraDetails.ivTrailerLoadingIndicator.setVisibility(View.GONE);
        mDetailBinding.extraDetails.tvNumberOfTrailer.setVisibility(View.VISIBLE);
        mDetailBinding.extraDetails.tvNumberOfTrailer.setText(numberOfTrailerTextViewText);
    }

    public void setReviewsLoadingIndicator() {
        mDetailBinding.extraDetails.tvNumberOfUserReview.setVisibility(View.INVISIBLE);
        mDetailBinding.extraDetails.ivReviewLoadingIndicator.setVisibility(View.VISIBLE);
        Animation c = AnimationUtils.loadAnimation(this, R.anim.progress_animation_main);
        c.setDuration(1000);
        mDetailBinding.extraDetails.ivReviewLoadingIndicator.startAnimation(c);
    }

    public void setNumberOfReviewTextViewText(String numberOfReviewTextViewText) {
        mDetailBinding.extraDetails.ivReviewLoadingIndicator.clearAnimation();
        mDetailBinding.extraDetails.ivReviewLoadingIndicator.setVisibility(View.GONE);
        mDetailBinding.extraDetails.tvNumberOfUserReview.setVisibility(View.VISIBLE);
        mDetailBinding.extraDetails.tvNumberOfUserReview.setText(numberOfReviewTextViewText);
    }

    @Override
    public void onClick(String trailerSourceKey) {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_YOUTUBE_URL_APP + trailerSourceKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(BASE_YOUTUBE_URL_WEB + trailerSourceKey));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        } else {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, getString(R.string.detail_activity_offline_reminder_text), Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        }
    }

    private void saveMovie() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mCurrentMovieReviews != null && mCurrentMovieTrailers != null) {
                if (mCurrentMovieReviews.size() > 0) {
                    if (mCurrentMovieTrailers.size() > 0) {
                        saveFavoriteMovie();
                        saveFavoriteTrailer();
                        saveFavoriteReview();
                        Log.i(TAG, "mCurrentMovieReviews.size() = " + mCurrentMovieReviews.size());
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        if (saveMovieRecordNumber == SAVE_MOVIE_SUCCESS && saveReviewRecordNumber == SAVE_REVIEW_SUCCESS
                                && saveTrailerRecordNumber == SAVE_TRAILER_SUCCESS) {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_successful), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        } else {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_failed), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        }
                    } else {
                        saveFavoriteMovie();
                        saveFavoriteReview();
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        if (saveMovieRecordNumber == SAVE_MOVIE_SUCCESS && saveReviewRecordNumber == SAVE_REVIEW_SUCCESS) {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_successful), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        } else {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_failed), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        }
                    }

                } else {
                    if (mCurrentMovieTrailers.size() > 0) {
                        saveFavoriteMovie();
                        saveFavoriteTrailer();
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        if (saveMovieRecordNumber == SAVE_MOVIE_SUCCESS && saveTrailerRecordNumber == SAVE_TRAILER_SUCCESS) {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_successful), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        } else {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_failed), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        }
                    } else {
                        saveFavoriteMovie();
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        if (saveMovieRecordNumber == SAVE_MOVIE_SUCCESS) {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_successful), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        } else {
                            mToast = Toast.makeText(this, getString(R.string.insert_movie_failed), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        }
                    }
                }
            } else {
                mFabButton.setColorFilter(ContextCompat.getColor(DetailActivity.this, R.color.colorWhiteFavoriteStar));
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, getString(R.string.toast_message_review_and_trailer_not_loaded_yet), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        } else {
            saveFavoriteMovie();
            if (mToast != null) {
                mToast.cancel();
            }
            if (saveMovieRecordNumber == SAVE_MOVIE_SUCCESS) {
                mToast = Toast.makeText(this, getString(R.string.insert_movie_successful_reviews_trailers_later), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            } else {
                mToast = Toast.makeText(this, getString(R.string.insert_movie_failed), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        }
    }


    private void deleteMovie() {

        if (mCurrentMovieReviews == null && mCurrentMovieTrailers == null) {
            deleteFavoriteMovie();
            if (mToast != null) {
                mToast.cancel();
            }
            if (deleteMovieRecordNumber == DELETE_MOVIE_SUCCESS) {
                mToast = Toast.makeText(this, getString(R.string.delete_movie_successful), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            } else {
                mToast = Toast.makeText(this, getString(R.string.delete_movie_failed), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        } else {
            if (mCurrentMovieReviews != null && mCurrentMovieReviews.size() > 0) {
                if (mCurrentMovieTrailers != null && mCurrentMovieTrailers.size() > 0) {
                    deleteFavoriteMovie();
                    deleteFavoriteTrailer();
                    deleteFavoriteReview();
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (deleteMovieRecordNumber == DELETE_MOVIE_SUCCESS && deleteReviewRecordNumber == DELETE_REVIEW_SUCCESS
                            && deleteTrailerRecordNumber == DELETE_TRAILER_SUCCESS) {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_successful), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    } else {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_failed), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                } else {
                    deleteFavoriteMovie();
                    deleteFavoriteReview();
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (deleteMovieRecordNumber == DELETE_MOVIE_SUCCESS && deleteReviewRecordNumber == DELETE_REVIEW_SUCCESS) {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_successful), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    } else {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_failed), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                }
            } else {
                if (mCurrentMovieTrailers != null && mCurrentMovieTrailers.size() > 0) {
                    deleteFavoriteMovie();
                    deleteFavoriteTrailer();
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (deleteMovieRecordNumber == DELETE_MOVIE_SUCCESS && deleteTrailerRecordNumber == DELETE_TRAILER_SUCCESS) {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_successful), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    } else {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_failed), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                } else {
                    deleteFavoriteMovie();
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (deleteMovieRecordNumber == DELETE_MOVIE_SUCCESS) {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_successful), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    } else {
                        mToast = Toast.makeText(this, getString(R.string.delete_movie_failed), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                }
            }
        }
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            loadTrailerData(mCurrentMovie.getId());
            loadReviewData(mCurrentMovie.getId());
            mTrailerRecyclerView.setVisibility(View.GONE);
            mReviewRecyclerView.setVisibility(View.GONE);
        }
    }


    public void saveFavoriteMovie() {

        ContentValues values = new ContentValues();
        values.put(FavMovieEntry.COLUMN_POSTER_PATH, mCurrentMovie.getPosterPath());
        values.put(FavMovieEntry.COLUMN_ORIGINAL_TITLE, mCurrentMovie.getOriginalTitle());
        values.put(FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL, mCurrentMovie.getMoviePosterImageThumbnail());
        values.put(FavMovieEntry.COLUMN_A_PLOT_SYNOPSIS, mCurrentMovie.getAPlotSynopsis());
        values.put(FavMovieEntry.COLUMN_USER_RATING, mCurrentMovie.getUserRating());
        values.put(FavMovieEntry.COLUMN_RELEASE_DATE, mCurrentMovie.getReleaseDate());
        values.put(FavMovieEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
        values.put(FavMovieEntry.COLUMN_NUMBER_OF_REVIEWS, mNumberOfReviewString);
        Log.i(TAG, "number of reviews: " + mNumberOfReviewString);
        values.put(FavMovieEntry.COLUMN_NUMBER_OF_TRAILERS, mNumberOfTrailerString);

        Uri newUri = getContentResolver().insert(FavMovieEntry.CONTENT_URI, values);

        if (newUri == null) {
            saveMovieRecordNumber = SAVE_MOVIE_FAIL;
            Log.e(TAG, getString(R.string.insert_movie_movie_failed));
        } else {
            saveMovieRecordNumber = SAVE_MOVIE_SUCCESS;
            Log.i(TAG, getString(R.string.insert_movie_movie_successful));
        }
    }


    public void saveFavoriteReview() {

        for (int i = 0; i < Integer.valueOf(mNumberOfReviewString); i++) {
            ContentValues values = new ContentValues();
            values.put(ReviewEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
            values.put(ReviewEntry.COLUMN_AUTHOR, mCurrentMovieReviews.get(i).getAuthor());
            values.put(ReviewEntry.COLUMN_REVIEW_CONTENT, mCurrentMovieReviews.get(i).getReviewContent());

            Uri newUri = getContentResolver().insert(ReviewEntry.CONTENT_URI, values);

            if (newUri == null) {
                saveReviewRecordNumber = SAVE_REVIEW_FAIL;
                Log.e(TAG, getString(R.string.insert_review_failed) + i);
            } else {
                saveReviewRecordNumber = SAVE_REVIEW_SUCCESS;
                Log.i(TAG, getString(R.string.insert_review_successful) + i);
            }
        }
    }


    public void saveFavoriteTrailer() {

        for (int i = 0; i < Integer.valueOf(mNumberOfTrailerString); i++) {
            ContentValues values = new ContentValues();
            values.put(TrailerEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
            values.put(TrailerEntry.COLUMN_KEY_OF_TRAILER, mCurrentMovieTrailers.get(i).getKeyString());

            Uri newUri = getContentResolver().insert(TrailerEntry.CONTENT_URI, values);

            if (newUri == null) {
                saveTrailerRecordNumber = SAVE_TRAILER_FAIL;
                Log.e(TAG, getString(R.string.insert_trailer_failed) + i);
            } else {
                saveTrailerRecordNumber = SAVE_TRAILER_SUCCESS;
                Log.i(TAG, getString(R.string.insert_trailer_successful) + i);
            }
        }
    }


    private void deleteFavoriteMovie() {
        String selection = FavMovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {mCurrentMovie.getId()};
        int rowsDeleted = getContentResolver().delete(FavMovieEntry.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted == 0) {
            deleteMovieRecordNumber = DELETE_MOVIE_FAIL;
            Log.e(TAG, getString(R.string.delete_movie_movie_failed));
        } else {
            deleteMovieRecordNumber = DELETE_MOVIE_SUCCESS;
            Log.i(TAG, getString(R.string.delete_movie_movie_successful));
        }
    }

    private void deleteFavoriteReview() {
        String selection = ReviewEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {mCurrentMovie.getId()};
        int rowsDeleted = getContentResolver().delete(ReviewEntry.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted == 0) {
            deleteReviewRecordNumber = DELETE_REVIEW_FAIL;
            Log.e(TAG, getString(R.string.delete_review_failed));
        } else {
            deleteReviewRecordNumber = DELETE_REVIEW_SUCCESS;
            Log.i(TAG, rowsDeleted + getString(R.string.delete_review_successful));
        }
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            mCurrentMovieReviews = null;
        }
    }


    private void deleteFavoriteTrailer() {
        String selection = TrailerEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {mCurrentMovie.getId()};
        int rowsDeleted = getContentResolver().delete(TrailerEntry.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted == 0) {
            deleteTrailerRecordNumber = DELETE_TRAILER_FAIL;
            Log.e(TAG, getString(R.string.delete_trailer_failed));
        } else {
            deleteTrailerRecordNumber = DELETE_TRAILER_SUCCESS;
            Log.i(TAG, rowsDeleted + getString(R.string.delete_trailer_successful));
        }
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            mCurrentMovieTrailers = null;
        }
    }

    public void loadReviewDataFromDatabase(String movieId) {
        setReviewsLoadingIndicator();
        List<Review> reviews = new ArrayList<>();
        String selection = ReviewEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieId};
        Cursor cursor = getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String author = cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR));
                String review_content = cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_CONTENT));

                Review review = new Review(author, review_content);

                reviews.add(review);
                cursor.moveToNext();
            }
            cursor.close();

            if (reviews != null) {
                hideLoadingIndicators();
                mCurrentMovieReviews = reviews;
                mReviewAdapter.setReviewData(reviews);
                mNumberOfReviewString = Integer.toString(mReviewAdapter.getItemCount());
                setNumberOfReviewTextViewText(mNumberOfReviewString);
            }
        } else {
            NetworkInfo networkInfo = getNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new FetchReviewTask(this).execute(movieId);
            } else {
                hideLoadingIndicators();

                String[] projection = {FavMovieEntry.COLUMN_NUMBER_OF_REVIEWS};
                Cursor newCursor = getContentResolver().query(
                        FavMovieEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
                Log.i(TAG, "number of new cursor: " + newCursor.getCount());
                if (newCursor != null && newCursor.getCount() > 0) {
                    newCursor.moveToFirst();
                    if (newCursor.getString(newCursor.getColumnIndex(FavMovieEntry.COLUMN_NUMBER_OF_REVIEWS)) != null) {
                        String numberOfReview = newCursor.getString(newCursor.getColumnIndex(FavMovieEntry.COLUMN_NUMBER_OF_REVIEWS));
                        if (numberOfReview.equals("0")) {
                            setNumberOfReviewTextViewText(numberOfReview);
                        } else {
                            setNumberOfReviewTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                        }
                    } else {
                        setNumberOfReviewTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                    }
                }
                newCursor.close();
            }
        }
    }

    public void loadTrailerDataFromDatabase(String movieId) {
        setTrailersLoadingIndicator();
        List<Trailer> trailers = new ArrayList<>();
        String selection = TrailerEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieId};
        Cursor cursor = getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mFirstTrailerSourceKey = cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_KEY_OF_TRAILER));
            while (!cursor.isAfterLast()) {
                String trailer_key = cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_KEY_OF_TRAILER));
                Trailer trailer = new Trailer(trailer_key);
                trailers.add(trailer);
                cursor.moveToNext();
            }
            cursor.close();
            if (trailers != null) {
                hideLoadingIndicators();
                mCurrentMovieTrailers = trailers;
                mTrailerAdapter.setTrailerData(trailers);
                mNumberOfTrailerString = Integer.toString(mTrailerAdapter.getItemCount());
                setNumberOfTrailerTextViewText(mNumberOfTrailerString);
            }
        } else {
            NetworkInfo networkInfo = getNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new FetchTrailerTask(this).execute(movieId);
            } else {
                hideLoadingIndicators();

                String[] projection = {FavMovieEntry.COLUMN_NUMBER_OF_TRAILERS};
                Cursor newCursor = getContentResolver().query(
                        FavMovieEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
                Log.i(TAG, "number of new cursor: " + newCursor.getCount());
                if (newCursor != null & newCursor.getCount() > 0) {
                    newCursor.moveToFirst();
                    if (newCursor.getString(newCursor.getColumnIndex(FavMovieEntry.COLUMN_NUMBER_OF_TRAILERS)) != null) {
                        String numberOfTrailer = newCursor.getString(newCursor.getColumnIndex(FavMovieEntry.COLUMN_NUMBER_OF_TRAILERS));
                        if (numberOfTrailer.equals("0")) {
                            setNumberOfTrailerTextViewText(numberOfTrailer);
                        } else {
                            setNumberOfTrailerTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                        }
                    } else {
                        setNumberOfTrailerTextViewText(getString(R.string.detail_activity_offline_reminder_text));
                    }
                }
                newCursor.close();
            }
        }
    }

    public boolean checkIsMovieAlreadyInFavDatabase(String movieId) {
        SQLiteDatabase database = mMovieDbHelper.getReadableDatabase();
        String selectString = "SELECT * FROM " + FavMovieEntry.TABLE_NAME + " WHERE "
                + FavMovieEntry.COLUMN_MOVIE_ID + " =?";
        Cursor cursor = database.rawQuery(selectString, new String[]{movieId});
        int count = cursor.getCount();
        cursor.close();
        database.close();
        return count > 0;
    }

    public int setFabButtonStarColor() {
        int colorOfStar = R.color.colorWhiteFavoriteStar;
        try {
            boolean movieIsInDatabase = checkIsMovieAlreadyInFavDatabase(mCurrentMovie.getId());
            if (movieIsInDatabase) {
                colorOfStar = R.color.colorYellowFavoriteStar;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return colorOfStar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // Have a refresh menu button to perform the refresh again, in case some device or some
            // users cannot perform swipe to refresh.
            case R.id.action_refresh:
                mLoadingIndicator.setVisibility(View.VISIBLE);
                refreshMovie();
                return true;
            // Share first trailer url
            case R.id.action_share:
                NetworkInfo networkInfo = getNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (mCurrentMovieTrailers != null) {
                        if (mFirstTrailerSourceKey != null) {
                            String urlToShare = BASE_YOUTUBE_URL_WEB + mFirstTrailerSourceKey;
                            shareFirstYoutubeUrl(urlToShare);
                        } else {
                            if (mToast != null) {
                                mToast.cancel();
                            }
                            mToast = Toast.makeText(this, getString(R.string.toast_message_no_trailer_to_share), Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.BOTTOM, 0, 0);
                            mToast.show();
                        }
                    } else {
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(this, getString(R.string.toast_message_trailer_not_loaded_yet), Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                } else {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(this, getString(R.string.detail_activity_offline_reminder_text), Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareFirstYoutubeUrl(String urlToShare) {
        String mimeType = "text/plain";
        String title = getString(R.string.title_share_url_string);
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(urlToShare)
                .startChooser();
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }
}
