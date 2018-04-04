package com.example.infolabsolution.thelastsubmission;



import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.infolabsolution.thelastsubmission.R;
import com.example.infolabsolution.thelastsubmission.UpdateWidgetService;
import com.example.infolabsolution.thelastsubmission.DetailActivity;
import com.example.infolabsolution.thelastsubmission.MainActivity;

import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.FAVORITE_PIC_TITLE_CODE;
import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.POPULAR_PIC_TITLE_CODE;
import static com.example.infolabsolution.thelastsubmission.WidgetConstants.IntentExtraWidgetTileCode.TOPRATED_PIC_TITLE_CODE;
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetProvider extends AppWidgetProvider {

    final static String TAG = DetailWidgetProvider.class.getSimpleName();

    private int titleCode;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);

            String orderBy = getOrderByPreference(context);
            if ("upcoming".equals(orderBy)) {
                views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_popular);
                views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_pop));
            } else if ("now_playing".equals(orderBy)) {
                views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_toprated);
                views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_top));
            } else {
                views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_favorite);
                views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_fav));
            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            Intent clickIntentTemplate = new Intent(context, DetailActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (MainActivity.ACTION_DATA_UPDATED.equals(intent.getAction())
                || UpdateWidgetService.ACTION_DATA_UPDATED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

            if (intent.hasExtra("title_code")) {
                titleCode = intent.getExtras().getInt("title_code");
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);
                if (titleCode == POPULAR_PIC_TITLE_CODE) {
                    views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_popular);
                    views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_pop));
                } else if (titleCode == TOPRATED_PIC_TITLE_CODE) {
                    views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_toprated);
                    views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_top));
                } else if (titleCode == FAVORITE_PIC_TITLE_CODE) {
                    views.setImageViewResource(R.id.im_widget_title, R.drawable.widgettitle_favorite);
                    views.setContentDescription(R.id.im_widget_title, context.getString(R.string.a11y_widget_title_fav));
                }
                ComponentName thisWidget = new ComponentName(context, DetailWidgetProvider.class);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                manager.updateAppWidget(thisWidget, views);
            } else {
                Log.i(TAG, "jag : widget provider on receive without titleCoder, not from initCursorLoader" +
                        ", from JobSchedular or delete All Movies.");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }


    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }

    @NonNull
    public String getOrderByPreference(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.settings_order_by_key),
                context.getString(R.string.settings_order_by_default));
    }
}
