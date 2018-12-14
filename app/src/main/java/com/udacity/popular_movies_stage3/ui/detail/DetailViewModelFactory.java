package com.udacity.popular_movies_stage3.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.popular_movies_stage3.data.TheMovieDBRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final TheMovieDBRepository mRepository;
    private final Long mMovieId;

    public DetailViewModelFactory(TheMovieDBRepository repository, Long movieId) {
        this.mRepository = repository;
        this.mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailActivityViewModel(mRepository,mMovieId);
    }
}
