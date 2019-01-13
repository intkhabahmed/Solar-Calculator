package com.intkhabahmed.solarcalculator.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.intkhabahmed.solarcalculator.model.PinInfo;

@Dao
public interface PinDao {

    @Query("SELECT * FROM pins")
    LiveData<PinInfo> getAllPins();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPin(PinInfo info);

    @Delete
    void deletePin(PinInfo info);
}
