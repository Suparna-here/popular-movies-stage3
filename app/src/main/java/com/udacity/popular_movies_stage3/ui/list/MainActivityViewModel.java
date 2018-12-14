package com.udacity.popular_movies_stage3.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.udacity.popular_movies_stage3.data.TheMovieDBRepository;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    public static final String LOG_TAG=MainActivityViewModel.class.getSimpleName();
    // movie list the user is looking at
    private final MutableLiveData<List<Movie>> mMovieList;
    private final TheMovieDBRepository mRepository;


    public MainActivityViewModel(TheMovieDBRepository repository) {
        mRepository=repository;
        mMovieList=mRepository.getMoviesListFromRepository();
    }


    public LiveData<List<Movie>> getMoviesList(){
        return mMovieList;
    }

    public void setMoviesBasedOnSortOrderInViewModel(String sort_by) {
        mRepository.setMoviesBasedOnSortOrder(sort_by);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su: setMoviesBasedOnSortOrderInViewModel "+sort_by);
    }

    void setFavouriteMovieById(long mMovieId) {
        mRepository.setFavouriteMovieById(mMovieId);
    }
}
