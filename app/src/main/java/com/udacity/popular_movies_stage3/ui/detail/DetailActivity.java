package com.udacity.popular_movies_stage3.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popular_movies_stage3.R;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.database.MovieReviewResponse;
import com.udacity.popular_movies_stage3.data.database.MovieTrailerResponse;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;
import com.udacity.popular_movies_stage3.utils.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerDBAdapter.TrailerDBAdapterOnClickHandler {
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String FAVOURITE_KEY = "Favourite_Key";
    private int mTrailerPosition = RecyclerView.NO_POSITION, mReviewPosition=RecyclerView.NO_POSITION;
    private static final String TRAILER_VISIBLE_POSITION="Trailer_Visible_Position";
    private static final String REVIEW_VISIBLE_POSITION="Review_Visible_Position";
    private boolean isFavouriteMovie;

    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_DATA = "extra_data";
    private RecyclerView mTrailerRecyclerView, mReviewRecyclerView;
    private TrailerDBAdapter mTrailerDBAdapter;
    private ReviewDBAdapter mReviewDBAdapter;

    private TextView mTrailerDetailErrorMessageDisplay, mTrailerErrorMessageDisplay, mReviewErrorMessageDisplay, mReviewDetailErrorMessageDisplay;
    private ImageView moviePosterIV;
    private FloatingActionButton favouriteButton;


    private DetailActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG, "su: populateUI");
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        Bundle bundle = intent.getBundleExtra(DetailActivity.EXTRA_BUNDLE);
        ArrayList<Movie> movieArrayList = bundle.getParcelableArrayList(EXTRA_DATA);
        final Movie movie = movieArrayList.get(0);
        if (movieArrayList == null) {
            // EXTRA_DATA not found in intent
            closeOnError();
            return;
        }
        if (movie == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }
        setContentView(R.layout.activity_detail);
        favouriteButton = findViewById(R.id.iv_favourite_button);

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        mTrailerErrorMessageDisplay = (TextView) findViewById(R.id.tv_trailer_error_message_display);
        mTrailerDetailErrorMessageDisplay = (TextView) findViewById(R.id.tv_trailer_detail_error_message_display);
        mReviewErrorMessageDisplay=(TextView) findViewById(R.id.tv_review_error_message_display);
        mReviewDetailErrorMessageDisplay = (TextView) findViewById(R.id.tv_review_detail_error_message_display);

        /*
         * LinearLayoutManager for Horizontal List.
         */
        LinearLayoutManager layoutManagerForTrailer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mTrailerRecyclerView.setLayoutManager(layoutManagerForTrailer);
        mTrailerRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecorVertical = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);
        mTrailerRecyclerView.addItemDecoration(itemDecorVertical);

        LinearLayoutManager layoutManagerForReview
                = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(layoutManagerForReview);
        mReviewRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecorHorizontal = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mReviewRecyclerView.addItemDecoration(itemDecorHorizontal);
        moviePosterIV = findViewById(R.id.iv_trailer_thumbnail);

        //        if (movie != null)
        MutableLiveData<Movie> favouriteMovie;
        DetailViewModelFactory detailViewModelFactory = InjectorUtils.provideDetailViewModelFactory(getApplicationContext(), movie.getId());
        mViewModel = ViewModelProviders.of(this, detailViewModelFactory).get(DetailActivityViewModel.class);
        favouriteMovie = mViewModel.getFavouriteMovieById();
        if (savedInstanceState == null) {
            isFavouriteMovie = mViewModel.isFavouriteFlag();
        } else {
            isFavouriteMovie = savedInstanceState.getBoolean(FAVOURITE_KEY);
            mTrailerPosition=savedInstanceState.getInt(TRAILER_VISIBLE_POSITION);
        }
        favouriteMovie.observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                // If entry removed or added, update the UI
                isFavouriteMovie = mViewModel.isFavouriteFlag();
                if (isFavouriteMovie) {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: mViewModel.isFavouriteMovie()");
                    favouriteButton.setImageResource(R.drawable.ic_favorite_red_34dp);
                } else if (!isFavouriteMovie) {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: NOT mViewModel.isFavouriteMovie()");
                    favouriteButton.setImageResource(R.drawable.ic_favorite_border_black_34dp);
                }
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavouriteMovie) {
                    //Remove from favourite_movie Tabale
                    mViewModel.deleteMovieFromFavouriteTable(movie);
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: deleteMovieFromFavourite()");
                } else if (!isFavouriteMovie) {
                    //Add to favourite_movie Tabale
                    mViewModel.insertMovieToFavouriteTable(movie);
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: insertMovieToFavourite()");
                }

            }
        });

        populateUI(movie);


        // Pass in 'this' as the TrailerDBAdapterOnClickHandler
        /*
         * The TrailerDBAdapter is responsible for linking trailer data with the Views that
         * will end up displaying our trailer data.
         */
        mTrailerDBAdapter = new TrailerDBAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mTrailerRecyclerView.setAdapter(mTrailerDBAdapter);


        /*
         * The ReviewDBAdapter is responsible for linking review data with the Views that
         * will end up displaying our review data.
         */
        mReviewDBAdapter = new ReviewDBAdapter();
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mReviewRecyclerView.setAdapter(mReviewDBAdapter);
        /* Once all of our views are setup, we can load the trailer and review data of movie. */
        loadTrailerDataInViewModel(movie.getId());
        loadReviewDataInViewModel(movie.getId());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FAVOURITE_KEY, isFavouriteMovie);
        mTrailerPosition = ((LinearLayoutManager)mTrailerRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(TRAILER_VISIBLE_POSITION, mTrailerPosition);
        super.onSaveInstanceState(outState);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    //** Populate UI with Movie details *//
    private void populateUI(Movie movie) {
        String posteUrl = ServiceGenerator.POSTER_URL + movie.getPoster_path();
        Picasso.get()
                .load(posteUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(moviePosterIV);

        setTitle(movie.getTitle());
        TextView movieReleaseDate_tv = findViewById(R.id.tv_movie_release_date);
        TextView movieVoteAverage_tv = findViewById(R.id.tv_vote_average_count);
        TextView moviePlotSynopsis_tv = findViewById(R.id.tv_plot_synopsis_content);

        if (TextUtils.isEmpty(movie.getRelease_date()))
            movieReleaseDate_tv.setText(getString(R.string.detail_release_date_text));
        else movieReleaseDate_tv.setText(movie.getRelease_date());
        if (TextUtils.isEmpty(Double.toString(movie.getVote_average())))
            movieVoteAverage_tv.setText(getString(R.string.detail_vote_average_text));
        else movieVoteAverage_tv.setText(Double.toString(movie.getVote_average()));
        if (TextUtils.isEmpty(movie.getOverview()))
            moviePlotSynopsis_tv.setText(getString(R.string.detail_plot_synopsis_text));
        else moviePlotSynopsis_tv.setText(movie.getOverview());
    }

    private void loadTrailerDataInViewModel(Long movieId) {
        LiveData<List<MovieTrailerResponse.Trailer>> trailerList = mViewModel.getTrailerList();
        trailerList.observe(this, new Observer<List<MovieTrailerResponse.Trailer>>() {
            @Override
            public void onChanged(@Nullable List<MovieTrailerResponse.Trailer> trailerList) {
                if(ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: swap Trailers");
                mTrailerDBAdapter.setTrailerData(trailerList);
                // Show the movie list or the loading screen based on whether the movie data exists
                // and is loaded
                if (trailerList != null && trailerList.size() != 0) {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "mRecyclerView mPosition");
                    if (mTrailerPosition == RecyclerView.NO_POSITION) mTrailerPosition = 0;
                    mTrailerRecyclerView.smoothScrollToPosition(mTrailerPosition);
                    showTrailerDataView();
                } else {
                    showTrailerErrorMessage(mViewModel.getTrailerErrorMessage().getValue());
                }
            }
        });
    }

    /**
     * This method will make the View for the Trailer data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showTrailerDataView() {

        /* First, make sure the error is invisible */
        mTrailerErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mTrailerDetailErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the trailer data is visible */
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }

     /**
     * This method will make the internet connection error message visible and hide the TrailerResponse
     * View.
     */
    private void showTrailerErrorMessage(boolean isInternetErrorForTrailer) {
        if(isInternetErrorForTrailer) {
            if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: showErrorMessage");
            /* First, hide the currently visible data */
            mTrailerRecyclerView.setVisibility(View.INVISIBLE);
            mTrailerDetailErrorMessageDisplay.setVisibility(View.INVISIBLE);
            /* Then, show the internet error */
            mTrailerErrorMessageDisplay.setVisibility(View.VISIBLE);
        }else{
            if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: showDetailErrorMessage");
                /* First, hide the currently visible data and internet connection error message*/
                mTrailerRecyclerView.setVisibility(View.INVISIBLE);
                mTrailerErrorMessageDisplay.setVisibility(View.INVISIBLE);
                /* Then, show the trailer data error */
                mTrailerDetailErrorMessageDisplay.setVisibility(View.VISIBLE);
        }

    }

    private void loadReviewDataInViewModel(Long movieId) {
        LiveData<List<MovieReviewResponse.Review>> reviewList = mViewModel.getReviewList();

        reviewList.observe(this, new Observer<List<MovieReviewResponse.Review>>() {
            @Override
            public void onChanged(@Nullable List<MovieReviewResponse.Review> reviewList) {
                if(ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: swap Trailers");
                mReviewDBAdapter.setReviewData(reviewList);
                // Show the movie list or the loading screen based on whether the movie data exists
                // and is loaded
                if (reviewList != null && reviewList.size() != 0) {
                    if(ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "mRecyclerView mPosition");
                    if (mReviewPosition == RecyclerView.NO_POSITION) mReviewPosition = 0;
                    mReviewRecyclerView.smoothScrollToPosition(mReviewPosition);
                    showReviewDataView();
                } else {
                    showReviewErrorMessage(mViewModel.getReviewErrorMessage().getValue());
                }
            }
        });
    }

    /**
     * This method will make the View for the Trailer data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showReviewDataView() {
        /* First, make sure the error is invisible */
        mReviewErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mReviewDetailErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the review data is visible */
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the internet connection error message visible and hide the TrailerResponse
     * View.
     */
    private void showReviewErrorMessage(boolean isInternetErrorForReview) {
        if(isInternetErrorForReview) {
//         First, hide the currently visible data
            mReviewRecyclerView.setVisibility(View.INVISIBLE);
            mReviewDetailErrorMessageDisplay.setVisibility(View.INVISIBLE);
//         Then, show the internet error
            mReviewErrorMessageDisplay.setVisibility(View.VISIBLE);
        }else{
            //        First, hide the currently visible data and internet connection error message
            mReviewRecyclerView.setVisibility(View.INVISIBLE);
            mReviewErrorMessageDisplay.setVisibility(View.INVISIBLE);
//        Then, show the review data error
            mReviewDetailErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void itemClickListener(MovieTrailerResponse.Trailer trailer) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));

        String errorMessage=getResources().getString(R.string.unable_to_play_trailer);
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            if(webIntent.resolveActivity(getPackageManager()) != null)
            startActivity(webIntent);
            else Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}