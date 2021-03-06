package com.fancystachestudios.smarteleprompter.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fancystachestudios.smarteleprompter.customClasses.Script;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

@Dao
public interface ScriptDao {

    @Insert
    void insert(Script script);

    @Update
    void update(Script... script);

    @Query("DELETE FROM script_table")
    void deleteAll();

    @Query("DELETE FROM script_table WHERE id = :scriptId")
    void delete(Long scriptId);

    @Query("SELECT * from script_table ORDER BY date DESC")
    LiveData<List<Script>> getAllScripts();

    @Query("SELECT * from script_table WHERE id = :scriptId")
    LiveData<Script> getScript(Long scriptId);

    @Query("SELECT * from script_table ORDER BY date DESC")
    List<Script> getAllScriptsList();
}
