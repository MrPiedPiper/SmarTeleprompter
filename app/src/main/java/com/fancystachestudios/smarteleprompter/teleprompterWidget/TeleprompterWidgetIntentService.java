package com.fancystachestudios.smarteleprompter.teleprompterWidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget implemented referencing one of my previous projects at https://github.com/MrPiedPiper/BakingApp/
 */
public class TeleprompterWidgetIntentService extends IntentService {

    private static final String ACTION_UPDATE_TELEPROMPTER_WIDGETS = "com.fancystachestudios.smarteleprompter.teleprompterWidget.action.UPDATE_TELEPROMPTER_WIDGETS";

    private static final String CONTEXT = "com.fancystachestudios.smarteleprompter.teleprompterWidget.extra.CONTEXT";

    public TeleprompterWidgetIntentService() {
        super("TeleprompterWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            handleActionUpdateTeleprompterWidgets();
        }
    }

    private void handleActionUpdateTeleprompterWidgets(){
        ScriptRoomDatabase database = ScriptSingleton.getInstance(this);
        ArrayList<Script> scripts = (ArrayList<Script>)database.scriptDao().getAllScriptsList();
        if(scripts.isEmpty()) return;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TeleprompterWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);

        TeleprompterWidgetProvider.updateAppWidget(this, appWidgetIds, appWidgetManager, scripts);
    }

    public static void startActionUpdateTeleprompterWidgets(Context context){
        Intent intent = new Intent(context, TeleprompterWidgetIntentService.class);
        intent.setAction(ACTION_UPDATE_TELEPROMPTER_WIDGETS);
        context.startService(intent);
    }
}
