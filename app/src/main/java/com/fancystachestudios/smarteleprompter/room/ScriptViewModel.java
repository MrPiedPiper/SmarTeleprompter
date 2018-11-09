package com.fancystachestudios.smarteleprompter.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.List;

/**
 *
 * Created following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

public class ScriptViewModel extends AndroidViewModel {

    private ScriptRoomDatabase mRepository;
    private LiveData<List<Script>> mAllScripts;

    public ScriptViewModel (Application application){
        super(application);
        mRepository = ScriptSingleton.getInstance(application.getApplicationContext());
        mAllScripts = mRepository.scriptDao().getAllScripts();
    }

    public LiveData<List<Script>> getAllScripts(){return mAllScripts;}

    public LiveData<Script> getScript(long id){
        return mRepository.scriptDao().getScript(id);
    }

    public void insert(Script script){mRepository.scriptDao().insert(script);}
}
