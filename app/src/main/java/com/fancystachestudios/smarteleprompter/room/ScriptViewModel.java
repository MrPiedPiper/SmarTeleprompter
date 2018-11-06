package com.fancystachestudios.smarteleprompter.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.List;

/**
 *
 * Created following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

public class ScriptViewModel extends AndroidViewModel {

    private ScriptRepository mRepository;
    private LiveData<List<Script>> mAllScripts;

    public ScriptViewModel (Application application){
        super(application);
        mRepository = new ScriptRepository(application);
        mAllScripts = mRepository.getAllScripts();
    }

    LiveData<List<Script>> getAllScripts(){return mAllScripts;}

    public void insert(Script script){mRepository.insert(script);}
}
