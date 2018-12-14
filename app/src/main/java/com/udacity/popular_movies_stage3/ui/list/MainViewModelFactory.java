package com.udacity.popular_movies_stage3.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.popular_movies_stage3.data.TheMovieDBRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TheMovieDBRepository mRepository;


    public MainViewModelFactory(TheMovieDBRepository repository) {
        this.mRepository = repository;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
