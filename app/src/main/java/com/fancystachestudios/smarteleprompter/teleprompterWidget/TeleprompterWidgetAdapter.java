package com.fancystachestudios.smarteleprompter.teleprompterWidget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.TeleprompterActivity;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;
import com.fancystachestudios.smarteleprompter.room.ScriptViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Widget implemented referencing one of my previous projects at https://github.com/MrPiedPiper/BakingApp/
 */
public class TeleprompterWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    private int appWidgetId;
    ArrayList<Script> scriptsArray = new ArrayList<>();

    public TeleprompterWidgetAdapter(Context context, Intent intent){
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        refreshData();
    }

    private void refreshData(){
        try{
            scriptsArray = new refreshDataAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class refreshDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Script>>{
        @Override
        protected ArrayList<Script> doInBackground(Void... voids) {
            ScriptRoomDatabase scriptRoomDatabase = ScriptSingleton.getInstance(context);
            ArrayList<Script> scripts = (ArrayList<Script>)scriptRoomDatabase.scriptDao().getAllScriptsList();
            return scripts;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        refreshData();
    }

    public void setDataset(ArrayList<Script> newDataset){
        scriptsArray = newDataset;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return scriptsArray.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        Script script = scriptsArray.get(i);

        RemoteViews widgetItem = new RemoteViews(context.getPackageName(), R.layout.teleprompter_widget_list_item);

        widgetItem.setTextViewText(R.id.widget_listview_item, script.getTitle());

        Bundle extras = new Bundle();
        extras.putParcelable(context.getString(R.string.teleprompter_pass_script), script);
        extras.putString(context.getString(R.string.teleprompter_pass_mode), context.getString(R.string.teleprompter_pass_mode_normal));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        widgetItem.setOnClickFillInIntent(R.id.widget_listview_item, fillInIntent);

        return widgetItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
