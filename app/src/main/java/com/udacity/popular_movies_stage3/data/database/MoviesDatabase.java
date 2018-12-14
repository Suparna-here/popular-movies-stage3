package com.udacity.popular_movies_stage3.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities={Movie.class},version=1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    public abstract MoviesDao moviesDao();

    private static final String DATABASE_NAME="favourite_movies";
    //For Singleton instantiation
    private static final Object LOCK=new Object();
    private static volatile MoviesDatabase mInstance;

    public static MoviesDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            MoviesDatabase.class, MoviesDatabase.DATABASE_NAME).build();
                }
            }
        }
        return mInstance;
    }
}
