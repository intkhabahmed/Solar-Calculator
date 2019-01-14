package com.intkhabahmed.solarcalculator.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.intkhabahmed.solarcalculator.model.PinInfo;

import java.util.List;

@Dao
public interface PinDao {

    @Query("SELECT * FROM pins")
    LiveData<List<PinInfo>> getAllPins();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPin(PinInfo info);

    @Delete
    void deletePin(PinInfo info);

    @Query("SELECT * FROM pins WHERE id = :id")
    LiveData<PinInfo> getPinById(int id);
}
