package com.intkhabahmed.solarcalculator.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.intkhabahmed.solarcalculator.model.PinInfo;

@Database(entities = PinInfo.class, version = 1, exportSchema = false)
public abstract class PinDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "pins.db";
    private static PinDatabase sInstance;

    public static PinDatabase getInstance(Context context) {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context, PinDatabase.class, DB_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract PinDao pinDao();
}
