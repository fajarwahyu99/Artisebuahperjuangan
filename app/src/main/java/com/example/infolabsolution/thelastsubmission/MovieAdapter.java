package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.MovieContract;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieMostPopularEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieTopRatedEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MainActivity;
import com.example.infolabsolution.thelastsubmission.ExternalPathUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE_W185 = "w185/";
    private final String CACHE_POSTERS_FOLDER_NAME = "/cacheposters/";

    private Cursor mCursor;
    private boolean mLoadFromDb;

    private final MovieAdapterOnClickHandler mClickHandler;

    private ArrayList<String> mMoviePostersUrlStrings = new ArrayList<>();

    private MainActivity mainActivity;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, MainActivity mainActivity) {
        mClickHandler = clickHandler;
        this.mainActivity = mainActivity;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_movie;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, final int position) {

        Animation a = AnimationUtils.loadAnimation(mainActivity, R.anim.progress_animation_main);
        a.setDuration(1000);
        movieAdapterViewHolder.mLoadingImageView.startAnimation(a);

        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            setMoviePostersOnline(movieAdapterViewHolder, position);
        } else {
            setMoviePostersOffline(movieAdapterViewHolder, position);
        }

        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String original_title = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE));
            movieAdapterViewHolder.mMoviePosterImageView.setContentDescription(original_title);
        }
    }

    private void setMoviePostersOffline(final MovieAdapterViewHolder movieAdapterViewHolder, final int position) {

        String basePosterExternalUrl = ExternalPathUtils.getExternalPathBasicFileName(this.mainActivity)
                + CACHE_POSTERS_FOLDER_NAME;

        String orderBy = getPreference();
        if ("upcoming".equals(orderBy)) {
            mCursor.moveToPosition(position);
            String moviePosterForOneMovie = mCursor.getString(mCursor
                    .getColumnIndex(CacheMovieMostPopularEntry.COLUMN_POSTER_PATH));

            String fullMoviePosterForOneMovie = basePosterExternalUrl
                    .concat(moviePosterForOneMovie);

            final File pathToPic = new File(fullMoviePosterForOneMovie);

            Picasso.with(mainActivity)
                    .load(pathToPic)
                    .error(R.drawable.pic_error_loading_w370)
                    .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                             mCursor.moveToPosition(position);
                            String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE));
                            if (currentMovieTitle.contains(":")) {
                                String[] separated = currentMovieTitle.split(":");
                                movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                            } else {
                                movieAdapterViewHolder.mErrorMovieNameTextView
                                        .setText(currentMovieTitle);
                            }
                            if (pathToPic.exists()) {
                                pathToPic.delete();
                            }
                        }
                    });

        } else if ("now_playing".equals(orderBy)) {
            mCursor.moveToPosition(position);
            String moviePosterForOneMovie = mCursor.getString(mCursor
                    .getColumnIndex(CacheMovieTopRatedEntry.COLUMN_POSTER_PATH));

            String fullMoviePosterForOneMovie = basePosterExternalUrl
                    .concat(moviePosterForOneMovie);
            final File pathToPic = new File(fullMoviePosterForOneMovie);

            Picasso.with(mainActivity)
                    .load(pathToPic)
                    .error(R.drawable.pic_error_loading_w370)
                    .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                             mCursor.moveToPosition(position);
                            String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_ORIGINAL_TITLE));
                            if (currentMovieTitle.contains(":")) {
                                String[] separated = currentMovieTitle.split(":");
                                movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                            } else {
                                movieAdapterViewHolder.mErrorMovieNameTextView
                                        .setText(currentMovieTitle);
                            }
                            if (pathToPic.exists()) {
                                pathToPic.delete();
                            }
                        }
                    });
        } else {
            mCursor.moveToPosition(position);
            String moviePosterForOneMovie = mCursor.getString(mCursor
                    .getColumnIndex(FavMovieEntry.COLUMN_POSTER_PATH));

            String fullMovieFavPosterForOneMovie = basePosterExternalUrl
                    .concat(moviePosterForOneMovie);
            final File pathToPic = new File(fullMovieFavPosterForOneMovie);

            Picasso.with(mainActivity)
                    .load(pathToPic)
                    .error(R.drawable.pic_error_loading_w370)
                    .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                             mCursor.moveToPosition(position);
                            String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_ORIGINAL_TITLE));
                            if (currentMovieTitle.contains(":")) {
                                String[] separated = currentMovieTitle.split(":");
                                movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                            } else {
                                movieAdapterViewHolder.mErrorMovieNameTextView
                                        .setText(currentMovieTitle);
                            }
                            if (pathToPic.exists()) {
                                pathToPic.delete();
                            }
                        }
                    });
        }
    }

    private void setMoviePostersOnline(final MovieAdapterViewHolder movieAdapterViewHolder, final int position) {
        String orderBy = getPreference();

        if (!"favorites".equals(orderBy)) {
            if (mLoadFromDb == false) {
                String moviePosterForOneMovie = mMoviePostersUrlStrings.get(position);

                Picasso.with(mainActivity)
                        .load(moviePosterForOneMovie)
                        .error(R.drawable.pic_error_loading_w370)
                        .into(movieAdapterViewHolder.mMoviePosterImageView);
            } else {
                if ("upcoming".equals(orderBy)) {
                    mCursor.moveToPosition(position);
                    String moviePosterForOneMovie = mCursor.getString(mCursor
                            .getColumnIndex(CacheMovieMostPopularEntry.COLUMN_POSTER_PATH));
                    String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W185)
                            .concat(moviePosterForOneMovie);

                    Picasso.with(mainActivity)
                            .load(fullMoviePosterForOneMovie)
                            .error(R.drawable.pic_error_loading_w370)
                            .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                                     mCursor.moveToPosition(position);
                                    String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE));
                                    if (currentMovieTitle.contains(":")) {
                                        String[] separated = currentMovieTitle.split(":");
                                        movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                                    } else {
                                        movieAdapterViewHolder.mErrorMovieNameTextView
                                                .setText(currentMovieTitle);
                                    }
                                }
                            });
                } else {
                    mCursor.moveToPosition(position);
                    String moviePosterForOneMovie = mCursor.getString(mCursor
                            .getColumnIndex(CacheMovieTopRatedEntry.COLUMN_POSTER_PATH));
                    String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W185)
                            .concat(moviePosterForOneMovie);

                    Picasso.with(mainActivity)
                            .load(fullMoviePosterForOneMovie)
                            .error(R.drawable.pic_error_loading_w370)
                            .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                                    mCursor.moveToPosition(position);
                                    String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_ORIGINAL_TITLE));
                                    if (currentMovieTitle.contains(":")) {
                                        String[] separated = currentMovieTitle.split(":");
                                        movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                                    } else {
                                        movieAdapterViewHolder.mErrorMovieNameTextView
                                                .setText(currentMovieTitle);
                                    }
                                }
                            });
                }
            }
        } else {
            mCursor.moveToPosition(position);
            String moviePosterForOneMovie = mCursor.getString(mCursor
                    .getColumnIndex(FavMovieEntry.COLUMN_POSTER_PATH));
            String fullMoviePosterForOneMovie = BASE_IMAGE_URL.concat(IMAGE_SIZE_W185)
                    .concat(moviePosterForOneMovie);

            Picasso.with(mainActivity)
                    .load(fullMoviePosterForOneMovie)
                    .error(R.drawable.pic_error_loading_w370)
                    .into(movieAdapterViewHolder.mMoviePosterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            movieAdapterViewHolder.mErrorMovieNameTextView.setVisibility(View.VISIBLE);
                            mCursor.moveToPosition(position);
                            String currentMovieTitle = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_ORIGINAL_TITLE));
                            if (currentMovieTitle.contains(":")) {
                                String[] separated = currentMovieTitle.split(":");
                                movieAdapterViewHolder.mErrorMovieNameTextView.setText(separated[0] + ":" + "\n" + separated[1].trim());
                            } else {
                                movieAdapterViewHolder.mErrorMovieNameTextView
                                        .setText(currentMovieTitle);
                            }
                        }
                    });
        }
    }


    @Override
    public int getItemCount() {

        if (mLoadFromDb) {
            if (null == mCursor) {
                return 0;
            }
            return mCursor.getCount();
        } else {
            return mMoviePostersUrlStrings.size();
        }
    }


    public void setMoviePosterData(ArrayList<String> moviePostersUrls) {
        mLoadFromDb = false;
        mMoviePostersUrlStrings.clear();
        mMoviePostersUrlStrings.addAll(moviePostersUrls);
        notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(Movie currentMovie);

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final ImageView mMoviePosterImageView;

        public final ImageView mLoadingImageView;

        public final TextView mErrorMovieNameTextView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_posters);
            mLoadingImageView = (ImageView) view.findViewById(R.id.iv_loading);
            mErrorMovieNameTextView = (TextView) view.findViewById(R.id.tv_error_movie_title_display);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mCursor != null) {
                String orderBy = getPreference();

                if ("upcoming".equals(orderBy)) {
                    int adapterPosition = getAdapterPosition();
                    mCursor.moveToPosition(adapterPosition);

                    String poster_path = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_POSTER_PATH));
                    String original_title = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_ORIGINAL_TITLE));
                    String movie_poster_image_thumbnail =
                            mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
                    String a_plot_synopsis = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_A_PLOT_SYNOPSIS));
                    String user_rating = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_USER_RATING));
                    String release_date = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_RELEASE_DATE));
                    String id = mCursor.getString(mCursor.getColumnIndex(CacheMovieMostPopularEntry.COLUMN_MOVIE_ID));

                    Movie currentMovieData = new Movie(poster_path, original_title, movie_poster_image_thumbnail
                            , a_plot_synopsis, user_rating, release_date, id);
                    mClickHandler.onClick(currentMovieData);
                } else if ("now_playing".equals(orderBy)) {
                    int adapterPosition = getAdapterPosition();
                    mCursor.moveToPosition(adapterPosition);

                    String poster_path = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_POSTER_PATH));
                    String original_title = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_ORIGINAL_TITLE));
                    String movie_poster_image_thumbnail =
                            mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
                    String a_plot_synopsis = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_A_PLOT_SYNOPSIS));
                    String user_rating = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_USER_RATING));
                    String release_date = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_RELEASE_DATE));
                    String id = mCursor.getString(mCursor.getColumnIndex(CacheMovieTopRatedEntry.COLUMN_MOVIE_ID));

                    Movie currentMovieData = new Movie(poster_path, original_title, movie_poster_image_thumbnail
                            , a_plot_synopsis, user_rating, release_date, id);
                    mClickHandler.onClick(currentMovieData);
                } else {
                    int adapterPosition = getAdapterPosition();
                    mCursor.moveToPosition(adapterPosition);

                    String poster_path = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_POSTER_PATH));
                    String original_title = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_ORIGINAL_TITLE));
                    String movie_poster_image_thumbnail =
                            mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL));
                    String a_plot_synopsis = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_A_PLOT_SYNOPSIS));
                    String user_rating = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_USER_RATING));
                    String release_date = mCursor.getString(mCursor.getColumnIndex(FavMovieEntry.COLUMN_RELEASE_DATE));
                    String id = mCursor.getString(mCursor.getColumnIndex(MovieContract.FavMovieEntry.COLUMN_MOVIE_ID));

                    Movie currentMovieData = new Movie(poster_path, original_title, movie_poster_image_thumbnail
                            , a_plot_synopsis, user_rating, release_date, id);
                    mClickHandler.onClick(currentMovieData);
                }
            } else {
                if (mainActivity.getmToast() != null) {
                    mainActivity.getmToast().cancel();
                }
                Toast newToast = Toast.makeText(mainActivity, mainActivity.getString(R.string.toast_message_movies_not_load_yet), Toast.LENGTH_SHORT);
                mainActivity.setmToast(newToast);
                mainActivity.getmToast().setGravity(Gravity.BOTTOM, 0, 0);
                mainActivity.getmToast().show();
            }
        }
    }

    public void swapCursor(Cursor newCursor) {
        mLoadFromDb = true;
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @NonNull
    private String getPreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        return sharedPrefs.getString(
                mainActivity.getString(R.string.settings_order_by_key),
                mainActivity.getString(R.string.settings_order_by_default)
        );
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }
}





