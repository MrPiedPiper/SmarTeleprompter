package com.fancystachestudios.smarteleprompter.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.ArrayList;

public class ScriptSearchLoader implements LoaderManager.LoaderCallbacks<ArrayList<Script>>{

    Context context;
    private LoaderManager loaderManager;

    private static final int LOADER_KEY_SEARCH = 21;

    private scriptSearchLoaderInterface thisInterface;

    public ScriptSearchLoader(Context context, LoaderManager loaderManager) {
        this.loaderManager = loaderManager;
        this.context = context;
        thisInterface = (scriptSearchLoaderInterface)context;
    }

    public interface scriptSearchLoaderInterface{
        void searchComplete(ArrayList<Script> searchResults);
    }

    public void searchForTitle(ArrayList<Script> list, String title){
        Loader<ArrayList<Script>> scriptLoader = loaderManager.getLoader(LOADER_KEY_SEARCH);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(context.getString(R.string.loader_search_title_key), title);
        queryBundle.putParcelableArrayList(context.getString(R.string.loader_search_list_key), list);
        if(scriptLoader == null){
            loaderManager.initLoader(LOADER_KEY_SEARCH, queryBundle, this).forceLoad();
        }else{
            loaderManager.restartLoader(LOADER_KEY_SEARCH, queryBundle, this).forceLoad();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList<Script>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Script>>(context) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(bundle == null){
                    stopLoading();
                }
            }

            @Override
            public ArrayList<Script> loadInBackground() {
                ArrayList<Script> matches = new ArrayList<>();
                ArrayList<Script> passedDataArrayList = bundle.getParcelableArrayList(context.getString(R.string.loader_search_list_key));
                String passedSearchString = bundle.getString(context.getString(R.string.loader_search_title_key));

                for (Script currScript:passedDataArrayList) {
                    if(currScript.getTitle().toLowerCase().contains(passedSearchString.toLowerCase())) matches.add(currScript);
                }

                return matches;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Script>> loader, ArrayList<Script> scripts) {
        thisInterface.searchComplete(scripts);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Script>> loader) {

    }
}
