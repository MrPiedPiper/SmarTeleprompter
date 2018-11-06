package com.fancystachestudios.smarteleprompter.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.List;

/**
 *
 * Created following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

public class ScriptRepository {

    private ScriptDao mScriptDao;
    private LiveData<List<Script>> mAllScripts;

    ScriptRepository(Application application){
        ScriptRoomDatabase db = ScriptRoomDatabase.getDatabase(application);
        mScriptDao = db.scriptDao();
        mAllScripts = mScriptDao.getAllScripts();
    }

    LiveData<List<Script>> getAllScripts(){
        return mAllScripts;
    }

    public void insert(Script script){
        new insertAsyncTask(mScriptDao).execute(script);
    }

    private static class insertAsyncTask extends AsyncTask<Script, Void, Void> {

        private ScriptDao mAsyncTaskDao;

        insertAsyncTask(ScriptDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Script... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
