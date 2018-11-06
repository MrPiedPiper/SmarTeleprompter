package com.fancystachestudios.smarteleprompter.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.fancystachestudios.smarteleprompter.customClasses.Script;

/**
 *
 * Created following this guide https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 *
 */

@Database(entities = {Script.class}, version = 1)
@TypeConverters({ScriptConverter.class})
public abstract class ScriptRoomDatabase extends RoomDatabase{
    public abstract ScriptDao scriptDao();

    private static volatile ScriptRoomDatabase INSTANCE;

    static ScriptRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null){
            synchronized (ScriptRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = (ScriptRoomDatabase) Room.databaseBuilder(context.getApplicationContext(),
                            RoomDatabase.class, "script_table")
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
