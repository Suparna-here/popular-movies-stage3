package com.udacity.popular_movies_stage3.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.udacity.popular_movies_stage3.data.TheMovieDBRepository;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.database.MovieReviewResponse;
import com.udacity.popular_movies_stage3.data.database.MovieTrailerResponse;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;

import java.util.List;

public class DetailActivityViewModel extends ViewModel {
    public static final String LOG_TAG = DetailActivityViewModel.class.getSimpleName();
    // Movie data the user is looking at
    private final MutableLiveData<Movie> mMovie;
    private final MutableLiveData<Boolean> isInternetErrorForTrailer;
    private final MutableLiveData<Boolean> isInternetErrorForReview;
    private boolean isFavouriteFlag;
    // id for the Movie
    private final Long mMovieId;
    private final TheMovieDBRepository mRepository;

    private final MutableLiveData<List<MovieTrailerResponse.Trailer>> mTrailerList;
    private final MutableLiveData<List<MovieReviewResponse.Review>> mReviewList;

    public DetailActivityViewModel(TheMovieDBRepository repository, Long movieId) {
        this.mMovieId = movieId;
        this.mRepository = repository;
        this.mMovie = mRepository.getFavouriteMovieById(mMovieId);
        if (mMovie.getValue() != null) {
            setFavouriteFlag(true);
        } else setFavouriteFlag(false);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: setFavouriteFlag() from Constructor");
        mTrailerList = mRepository.getTrailerListFromRepository(mMovieId);
        this.isInternetErrorForTrailer = mRepository.getIsInternetErrorForTrailer();
        mReviewList = mRepository.getReviewListFromRepository(mMovieId);
        this.isInternetErrorForReview = mRepository.getIsInternetErrorForReview();
    }

    public void setFavouriteFlag(boolean flag) {
        isFavouriteFlag = flag;
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: setFavouriteFlag(flag)");
    }

    public MutableLiveData<Boolean> getTrailerErrorMessage() {
        return isInternetErrorForTrailer;
    }

    public MutableLiveData<Boolean> getReviewErrorMessage() {
        return isInternetErrorForReview;
    }

    public boolean isFavouriteFlag() {
        return isFavouriteFlag;
    }

    public MutableLiveData<Movie> getFavouriteMovieById() {
        return mMovie;
    }

    public void deleteMovieFromFavouriteTable(Movie movie) {
        mRepository.deleteMovieFromFavourite(mMovieId);
        setFavouriteFlag(false);
    }

    public void insertMovieToFavouriteTable(Movie movie) {
        mRepository.insertMovieToFavourite(movie);
        setFavouriteFlag(true);
    }

    public LiveData<List<MovieTrailerResponse.Trailer>> getTrailerList() {
        return mTrailerList;
    }

    public LiveData<List<MovieReviewResponse.Review>> getReviewList() {
        return mReviewList;
    }
}
