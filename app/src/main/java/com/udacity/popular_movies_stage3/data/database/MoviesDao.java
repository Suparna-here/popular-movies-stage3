package com.udacity.popular_movies_stage3.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieToFavourite(Movie movie);

    @Query("SELECT * from favourite_movies WHERE id=:Id")
    LiveData<Movie> getFavouriteMovieById(long Id);

    @Query("SELECT * from favourite_movies")
    LiveData<List<Movie>> getFavouriteMovies();

    @Query("SELECT count(*) from favourite_movies")
    int countAllFavouriteMovies();

    @Query("SELECT count(*) from favourite_movies WHERE id=:id")
    int countInFavourite(long id);

    @Query("DELETE from favourite_movies WHERE id=:Id")
    void deleteMovieFromFavourite(long Id);
}
