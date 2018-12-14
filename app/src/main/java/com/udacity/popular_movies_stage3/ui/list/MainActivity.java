package com.udacity.popular_movies_stage3.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.udacity.popular_movies_stage3.R;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;
import com.udacity.popular_movies_stage3.ui.detail.DetailActivity;
import com.udacity.popular_movies_stage3.utils.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieDBAdapter.MovieDBAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieDBAdapter mMovieDBAdapter;
    private MainActivityViewModel mViewModel;
    private static final String SORT_KEY = "Ordering_Sequence";
    private String sort_by;

    private TextView mErrorMessageDisplay;
    private TextView mMovieFavouriteErrorMessageDisplay;

    private static final int SPAN_COUNT = 2;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_movie_error_message_display);

        mMovieFavouriteErrorMessageDisplay = (TextView) findViewById(R.id.tv_movie_favourite_error_message_display);

        /*
         * GridLayoutManager for GridView.
         * SPAN_COUNT parameter defines number of columns.
         */
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, SPAN_COUNT);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        // Pass in 'this' as the MovieDBAdapterOnClickHandler
        /*
         * The MovieDBAdapter is responsible for linking movie data with the Views that
         * will end up displaying our movie data.
         */
        mMovieDBAdapter = new MovieDBAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieDBAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        MainViewModelFactory mainViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory(getApplicationContext());
        mViewModel = ViewModelProviders.of(this, mainViewModelFactory).get(MainActivityViewModel.class);

        if (savedInstanceState == null) {
            sort_by = ServiceGenerator.ORDER_POPULARITY;
            /* Once all of our views are setup, we can load the movie data. */
            mViewModel.setMoviesBasedOnSortOrderInViewModel(sort_by);

        }else{
            sort_by=savedInstanceState.getString(SORT_KEY);
        }
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: sort By "+sort_by);
        loadMovieDataInViewModel();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_KEY, sort_by);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieDataInViewModel() {
        LiveData<List<Movie>> movieList = mViewModel.getMoviesList();
        movieList.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                if(ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: swap Movies sort_by "+sort_by);
                mMovieDBAdapter.setMovieData(movieList,sort_by);
                // Show the movie list or the loading screen based on whether the movie data exists
                // and is loaded
                if (movieList != null && movieList.size() != 0) {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "mRecyclerView mPosition");
                    showMovieDataView();
                } else {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "showErrorMessage");
                    showErrorMessage();
                }
            }
        });
    }


    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieFavouriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the MovieResponse
     * View.
     */
    private void showErrorMessage() {
        if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY) || sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            /* First, hide the currently visible data */
            if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: mErrorMessageDisplay " + sort_by);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mMovieFavouriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
            /* Then, show the internet error */
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
            if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: mMovieFavouriteErrorMessageDisplay " + sort_by);
            //* First, hide the currently visible data and internet connection error message*//*
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            //* Then, show the Favourite movie data error *//*
            mMovieFavouriteErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    //On Clicking GridItem, this method is called.
    @Override
    public void itemClickListener(final Movie movie) {
        mViewModel.setFavouriteMovieById(movie.getId());
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        movieList.add(movie);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: itemClickListener " + movie.getId());
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DetailActivity.EXTRA_DATA, movieList );
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popularity) {//Show Popular Movies
            sort_by = ServiceGenerator.ORDER_POPULARITY;
            mViewModel.setMoviesBasedOnSortOrderInViewModel(sort_by);
            return true;
        } else if (id == R.id.action_toprated) {//Show Top Rated Movies
            sort_by = ServiceGenerator.ORDER_TOPRATED;
            mViewModel.setMoviesBasedOnSortOrderInViewModel(sort_by);
            return true;
        } else if (id == R.id.action_favourite) {//Show Top Favourite Movies
            sort_by = ServiceGenerator.ORDER_FAVOURITE;
            mViewModel.setMoviesBasedOnSortOrderInViewModel(sort_by);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
