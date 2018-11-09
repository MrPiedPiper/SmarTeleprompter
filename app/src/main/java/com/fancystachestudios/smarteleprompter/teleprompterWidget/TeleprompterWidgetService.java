package com.fancystachestudios.smarteleprompter.teleprompterWidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;

/**
 * Widget implemented referencing one of my previous projects at https://github.com/MrPiedPiper/BakingApp/
 */

public class TeleprompterWidgetService extends RemoteViewsService {

    TeleprompterWidgetAdapter widgetAdapter;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        widgetAdapter = new TeleprompterWidgetAdapter(this.getApplicationContext(), intent);
        return widgetAdapter;
    }
}
