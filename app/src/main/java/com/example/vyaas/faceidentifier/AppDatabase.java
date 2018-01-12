package com.example.vyaas.faceidentifier;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by vyaas on 12/8/17.
 */

@Database(entities = {Celebrity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase   INSTANCE;
    public abstract CelebrityDao userDao();

}
