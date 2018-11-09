package com.fancystachestudios.smarteleprompter.teleprompterWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.TeleprompterActivity;
import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.ArrayList;

/**
 * Widget implemented referencing one of my previous projects at https://github.com/MrPiedPiper/BakingApp/
 */
public class TeleprompterWidgetProvider extends AppWidgetProvider {

    private static RemoteViews updateWidgetListView(Context context, int appWidgetId, ArrayList<Script> scripts){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.teleprompter_widget_provider);
        Intent serviceIntent = new Intent(context, TeleprompterWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.widget_listview, serviceIntent);

        //Activity launching added referencing Udacity course "Android Developer Nanodegree Program" > "3. Advanced Android App Development" > "Lesson 7 Widgets"
        Intent appIntent = new Intent(context, TeleprompterActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_listview, appPendingIntent);

        remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);
        return remoteViews;
    }

    static void updateAppWidget(Context context, int[] appWidgetIds, AppWidgetManager appWidgetManager, ArrayList<Script> scripts) {
        for(int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, updateWidgetListView(context, appWidgetId, scripts));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TeleprompterWidgetIntentService.startActionUpdateTeleprompterWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}

