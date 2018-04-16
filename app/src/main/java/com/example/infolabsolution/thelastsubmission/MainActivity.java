package com.example.infolabsolution.thelastsubmission;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.AlarmReceiver;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieMostPopularEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.CacheMovieTopRatedEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.FavMovieEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.ReviewEntry;
import com.example.infolabsolution.thelastsubmission.MovieContract.TrailerEntry;
import com.example.infolabsolution.thelastsubmission.JobSchedulersConstants;
import com.example.infolabsolution.thelastsubmission.PersistFavMovie;
import com.example.infolabsolution.thelastsubmission.PersistPopMovieTask;
import com.example.infolabsolution.thelastsubmission.PersistTopMovieTask;
import com.example.infolabsolution.thelastsubmission.DeleteExtraMoviePicService;
import com.example.infolabsolution.thelastsubmission.NotificationService;
import com.example.infolabsolution.thelastsubmission.PersistFavService;
import com.example.infolabsolution.thelastsubmission.PersistPopService;
import com.example.infolabsolution.thelastsubmission.PersistTopService;
import com.example.infolabsolution.thelastsubmission.UpdateWidgetService;
import com.example.infolabsolution.thelastsubmission.Movie;
import com.example.infolabsolution.thelastsubmission.MovieAdapter;
import com.example.infolabsolution.thelastsubmission.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.infolabsolution.thelastsubmission.ScheduleNotificationLowerVersion;
import com.example.infolabsolution.thelastsubmission.FetchMoviePostersTask;
import com.example.infolabsolution.thelastsubmission.DeleteExternalFolderExtraPic;

import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.FAVORITE_PIC_TITLE_CODE;
import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.POPULAR_PIC_TITLE_CODE;
import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.TOPRATED_PIC_TITLE_CODE;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler
        , android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ACTION_DATA_UPDATED = "com.example.infolabsolution.thelastsubmission.ACTION_DATA_UPDATED";

    private static final String TAG = MainActivity.class.getSimpleName();

    public static Toast mToast;

    public static Boolean mShowToast = false;

    public static final int MOVIE_LOADER = 0;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    public MovieAdapter getmMovieAdapter() {
        return mMovieAdapter;
    }

    public ProgressBar getmLoadingIndicator() {
        return mLoadingIndicator;
    }

    public SwipeRefreshLayout getmSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public Toast getmToast() {
        return mToast;
    }

    public void setmToast(Toast mToast) {
        this.mToast = mToast;
    }

    private static PendingIntent alarmIntent;

    private static AlarmManager alarmManager;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String titleOrderBy = getOrderByPreference();
        if ("upcoming".equals(titleOrderBy)) {
            setTitle(getString(R.string.main_activity_title_most_popular));
        } else if ("now_playing".equals(titleOrderBy)) {
            setTitle(getString(R.string.main_activity_title_top_rated));
        } else {
            setTitle(getString(R.string.main_activity_title_favorite));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movieposters);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        final GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mMovieAdapter == null) {
            mMovieAdapter = new MovieAdapter(this, this);
        }
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

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


        // If there is a network connection, fetch data
        if (getNetworkInfo() != null && getNetworkInfo().isConnected()) {
            // First loading the app, don't show up to date toast if it is up to date.
            mShowToast = false;

            String orderBy = getOrderByPreference();
            if ("upcoming".equals(orderBy)) {
                orderBy = "movie/" + orderBy;
                new FetchMoviePostersTask(this).execute(orderBy);
                new PersistPopMovieTask(this).execute();
                initCursorLoader();
            } else if ("now_playing".equals(orderBy)) {
                orderBy = "movie/" + orderBy;
                new FetchMoviePostersTask(this).execute(orderBy);
                new PersistTopMovieTask(this).execute();
                initCursorLoader();
            } else {
                initCursorLoader();
                PersistFavMovie.persistFavMovie(this);
            }

            DeleteExternalFolderExtraPic.deleteExtraMoviePosterFilePic(this);
            DeleteExternalFolderExtraPic.deleteExtraMovieThumbnailFilePic(this);

        } else {
            Boolean enableOffline = getEnableOfflinePreference();
            if (enableOffline) {
                hideLoadingIndicators();
                initCursorLoader();
            } else {
                hideLoadingIndicators();
                showErrorMessage();
                mErrorMessageDisplay.setText(getString(R.string.error_message_enable_offline_false));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            scheduleUpdatePopMovieJob();
            scheduleUpdateTopMovieJob();
            scheduleUpdateFavMovieJob();
            scheduleDeleteExtraPic();
            scheduleUpdateWidgetJob();
            scheduleNotificationJob();
        } else {
            ScheduleNotificationLowerVersion.scheduleNotification(this);
        }

        setUpAlarm();

    }

    private void setUpAlarm() {

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.set(Calendar.YEAR, 2018);
        scheduledCalendar.set(Calendar.MONTH, Calendar.APRIL);
        scheduledCalendar.set(Calendar.DATE, 4);
        scheduledCalendar.set(Calendar.HOUR_OF_DAY, 11);
        scheduledCalendar.set(Calendar.MINUTE, 26);
        scheduledCalendar.set(Calendar.SECOND, 0);

        Calendar current = Calendar.getInstance();
        if (!scheduledCalendar.before(current)) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledCalendar.getTimeInMillis(), alarmIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleUpdatePopMovieJob() {
        ComponentName serviceName = new ComponentName(this, PersistPopService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_PERSIST_POP_MOVIE, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_FETCH_POP_MOVIE, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleUpdateTopMovieJob() {
        ComponentName serviceName = new ComponentName(this, PersistTopService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_PERSIST_TOP_MOVIE, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_FETCH_TOP_MOVIE, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
        }
    }

    // N == api 24
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleUpdateFavMovieJob() {
        Log.i(TAG, "Scheduling fetch fav movie job.");
        ComponentName serviceName = new ComponentName(this, PersistFavService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_PERSIST_FAV_MOVIE, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_FETCH_FAV_MOVIE, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Fetch fav movie job scheduled successfully!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleDeleteExtraPic() {
        Log.i(TAG, "Scheduling delete extra pic job.");
        ComponentName serviceName = new ComponentName(this, DeleteExtraMoviePicService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_DELETE_EXTRA_PIC, serviceName)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_DELETE_EXTRA_PIC, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Scheduler delete extra pic job scheduled successfully!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleUpdateWidgetJob() {
        Log.i(TAG, "Scheduling update widget job.");
        ComponentName serviceName = new ComponentName(this, UpdateWidgetService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_UPDATE_WIDGET, serviceName)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_UPDATE_WIDGET, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Scheduler update widget job scheduled successfully!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleNotificationJob() {
        Log.i(TAG, "Scheduling notification job.");
        ComponentName serviceName = new ComponentName(this, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(JobSchedulersConstants.JOB_ID_NOTIFICATION, serviceName)
                .setPeriodic(JobSchedulersConstants.PERIOD_MILLIS_NOTIFICATION, JobInfo.getMinFlexMillis())
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Scheduler notification scheduled successfully!");
        }
    }

    private void refreshMovie() {
        if (getNetworkInfo() != null && getNetworkInfo().isConnected()) {
            mShowToast = true;
            String orderBy = getOrderByPreference();
            if ("upcoming".equals(orderBy)) {
                orderBy = "movie/" + orderBy;
                new FetchMoviePostersTask(this).execute(orderBy);
                new PersistPopMovieTask(this).execute();
                initCursorLoader();
            } else if ("now_playing".equals(orderBy)) {
                orderBy = "movie/" + orderBy;
                new FetchMoviePostersTask(this).execute(orderBy);
                new PersistTopMovieTask(this).execute();
                initCursorLoader();
            } else {
                initCursorLoader();
                PersistFavMovie.persistFavMovie(this);
            }

            DeleteExternalFolderExtraPic.deleteExtraMoviePosterFilePic(this);
            DeleteExternalFolderExtraPic.deleteExtraMovieThumbnailFilePic(this);

        } else {
            hideLoadingIndicators();
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(MainActivity.this, getString(R.string.toast_message_refresh_no_internet), Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        }
    }

    public void initCursorLoader() {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        int titleCode;
        String orderBy = getOrderByPreference();
        if ("upcoming".equals(orderBy)) {
            titleCode = POPULAR_PIC_TITLE_CODE;
        } else if ("now_playing".equals(orderBy)) {
            titleCode = TOPRATED_PIC_TITLE_CODE;
        } else {
            titleCode = FAVORITE_PIC_TITLE_CODE;
        }
        dataUpdatedIntent.putExtra("title_code", titleCode);
        this.sendBroadcast(dataUpdatedIntent);
        Log.i(TAG, "jag ! jag init loader !");
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }


    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    public void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage() {

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteAll = menu.findItem(R.id.action_delete_all);
        String orderBy = getOrderByPreference();
        Boolean enableOffline = getEnableOfflinePreference();
        if ("favorites".equals(orderBy)) {
            if (getNetworkInfo() != null && getNetworkInfo().isConnected()) {
                deleteAll.setVisible(true);
            } else {
                if (enableOffline == true) {
                    deleteAll.setVisible(true);
                } else {
                    deleteAll.setVisible(false);
                }
            }
        } else {
            deleteAll.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                mLoadingIndicator.setVisibility(View.VISIBLE);
                refreshMovie();
                return true;
            case R.id.action_settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                return true;
            case R.id.action_language:
                Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(mIntent);
                return true;
            case R.id.action_delete_all:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllMovies() {

        Cursor cursor = getContentResolver().query(
                FavMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor.getCount() == 0 || cursor == null) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, getString(R.string.delete_all_movie_no_movies_to_delete), Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        } else {
            int rowsDeletedFavMovie = getContentResolver().delete(FavMovieEntry.CONTENT_URI, null, null);
            int rowsDeletedReview = getContentResolver().delete(ReviewEntry.CONTENT_URI, null, null);
            int rowsDeletedTrailer = getContentResolver().delete(TrailerEntry.CONTENT_URI, null, null);

            if (rowsDeletedFavMovie == 0) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, getString(R.string.delete_all_movie_failed), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            } else {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, getString(R.string.delete_all_movie_successful), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        }
        cursor.close();

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        this.sendBroadcast(dataUpdatedIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String orderBy = getOrderByPreference();

        if ("upcoming".equals(orderBy)) {
            return new CursorLoader(
                    this,
                    CacheMovieMostPopularEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } else if ("now_playing".equals(orderBy)) {
            return new CursorLoader(
                    this,
                    CacheMovieTopRatedEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(
                    this,
                    FavMovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            hideLoadingIndicators();
            showMovieDataView();
            mMovieAdapter.swapCursor(cursor);

        } else {

            supportStartPostponedEnterTransition();
            hideLoadingIndicators();
            showErrorMessage();

            String orderBy = getOrderByPreference();

            if ("upcoming".equals(orderBy)) {

                mErrorMessageDisplay.setText(getString(R.string.error_message_no_popular_movie));
            } else if ("now_playing".equals(orderBy)) {

                mErrorMessageDisplay.setText(getString(R.string.error_message_no_top_rated_movie));
            } else {
                mErrorMessageDisplay.setText(getString(R.string.error_message_no_fav_movie));
            }
        }
    }

    private void hideLoadingIndicators() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);

    }

    public void restartLoader() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @NonNull
    private String getOrderByPreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connMgr.getActiveNetworkInfo();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllMovies();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean getEnableOfflinePreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.getBoolean(
                getString(R.string.pref_enable_offline_key),
                getResources().getBoolean(R.bool.pref_enable_offline_default));
    }
}
