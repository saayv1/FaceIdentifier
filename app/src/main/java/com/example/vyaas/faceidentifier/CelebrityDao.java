package com.example.vyaas.faceidentifier;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by vyaas on 12/8/17.
 */

@Dao
public interface CelebrityDao {
    @Query("SELECT * FROM celebrity")
    List<Celebrity> getAll();
/*
    @Query("SELECT * FROM celebrity WHERE persistedId IN (persistedId)")
    List<Celebrity> loadAllByIds(String[] persistedIds);
*/

    @Insert
    void insertAll(Celebrity... celebrities);

    @Delete
    void delete(Celebrity celebrity);
}
