package com.fancystachestudios.smarteleprompter.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 *
 * Created referencing https://android.jlelse.eu/room-persistence-library-typeconverters-and-database-migration-3a7d68837d6c
 *
 */
public class ScriptConverter {

    @TypeConverter
    public static Date longToDate (Long value){
        return new Date(value);
    }

    @TypeConverter
    public static Long dateToLong (Date value){
        return value.getTime();
    }
}
