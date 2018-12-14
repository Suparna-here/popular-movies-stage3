package com.udacity.popular_movies_stage3.utils;

import android.content.Context;
import android.util.Log;

import com.udacity.popular_movies_stage3.AppExecutors;
import com.udacity.popular_movies_stage3.data.TheMovieDBRepository;
import com.udacity.popular_movies_stage3.data.database.MoviesDatabase;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;
import com.udacity.popular_movies_stage3.ui.detail.DetailViewModelFactory;
import com.udacity.popular_movies_stage3.ui.list.MainViewModelFactory;

public class InjectorUtils {
    private static final String LOG_TAG =InjectorUtils.class.getName();

    public static TheMovieDBRepository provideRepository(Context context ) {
        MoviesDatabase database = MoviesDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su: get an instance of TheMovieDBRepository");
        return TheMovieDBRepository.getInstance(database.moviesDao(),executors);
    }

   public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Long movieId) {
        TheMovieDBRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, movieId);
    }

     public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        TheMovieDBRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }
}
