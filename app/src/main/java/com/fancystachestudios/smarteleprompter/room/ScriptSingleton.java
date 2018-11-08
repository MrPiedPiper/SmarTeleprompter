package com.fancystachestudios.smarteleprompter.room;

import android.arch.persistence.room.Room;
import android.content.Context;

public class ScriptSingleton {
    private static ScriptRoomDatabase scriptRoomDatabase;

    private ScriptSingleton(Context context){}

    public static ScriptRoomDatabase getInstance(Context context){
        if(scriptRoomDatabase == null){
            scriptRoomDatabase = Room.databaseBuilder(context.getApplicationContext(), ScriptRoomDatabase.class, "script_table").build();
        }
        return scriptRoomDatabase;
    }
}
