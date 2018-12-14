package com.udacity.popular_movies_stage3.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.udacity.popular_movies_stage3.AppExecutors;
import com.udacity.popular_movies_stage3.BuildConfig;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.database.MovieResponse;
import com.udacity.popular_movies_stage3.data.database.MovieReviewResponse;
import com.udacity.popular_movies_stage3.data.database.MovieTrailerResponse;
import com.udacity.popular_movies_stage3.data.database.MoviesDao;
import com.udacity.popular_movies_stage3.data.network.MovieService;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TheMovieDBRepository {
    private static final String LOG_TAG = TheMovieDBRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    private static TheMovieDBRepository mInstance;
    private final MoviesDao mMoviesDao;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;
    private MutableLiveData<List<Movie>> movieData;
    private MutableLiveData<List<MovieTrailerResponse.Trailer>> trailerData;
    private MutableLiveData<List<MovieReviewResponse.Review>> reviewData;
    private MutableLiveData<Boolean> isInternetErrorForTrailer;
    private MutableLiveData<Boolean> isInternetErrorForReview;
    private MutableLiveData<Movie> movieMutableLiveData;
    private String sort_by;

    private TheMovieDBRepository(MoviesDao moviesDao, AppExecutors executors) {
        mMoviesDao = moviesDao;
        mExecutors = executors;
        movieData = new MutableLiveData<List<Movie>>();
        movieMutableLiveData = new MutableLiveData<Movie>();
        isInternetErrorForTrailer = new MutableLiveData<Boolean>();
        setTrailerErrorMessage();
        isInternetErrorForReview = new MutableLiveData<Boolean>();
        setReviewErrorMessage();
        trailerData = new MutableLiveData<List<MovieTrailerResponse.Trailer>>();
        reviewData = new MutableLiveData<List<MovieReviewResponse.Review>>();
    }

    private void setTrailerErrorMessage() {

        isInternetErrorForTrailer.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInternetError) {

            }
        });
    }

    private void setReviewErrorMessage() {

        isInternetErrorForReview.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInternetError) {

            }
        });
    }

    public MutableLiveData<List<Movie>> getMoviesListFromRepository() {
        return movieData;
    }

    public MutableLiveData<List<MovieTrailerResponse.Trailer>> getTrailerListFromRepository(long movieId) {
        setTrailerListByMovieId(movieId);
        return trailerData;
    }

    public MutableLiveData<List<MovieReviewResponse.Review>> getReviewListFromRepository(long movieId) {
        setReviewListByMovieId(movieId);
        return reviewData;
    }

    public synchronized static TheMovieDBRepository getInstance(MoviesDao mMoviesDao, AppExecutors executors) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: Getting the repository");
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new TheMovieDBRepository(mMoviesDao, executors);
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: Made new repository");
            }
        }
        return mInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    public synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;
    }

    /**
     * Database related operations
     **/
    public void insertMovieToFavourite(final Movie movie) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mMoviesDao.insertMovieToFavourite(movie);
                movieMutableLiveData.postValue(movie);
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: movie Insertion");
            }
        });

    }

    public void deleteMovieFromFavourite(final long id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mMoviesDao.countInFavourite(id);
                if (count > 0) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: count =" + count + " deleteMovieFromFavourite " + id);
                    mMoviesDao.deleteMovieFromFavourite(id);
                    movieMutableLiveData.postValue(null);
                } else {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: No movie for Deletion");
                }
            }
        });
    }

    public void setFavouriteMovieById(long movieId) {
        initializeData();
        final LiveData<Movie> movieLiveData = mMoviesDao.getFavouriteMovieById(movieId);
        movieLiveData.observeForever(new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                movieMutableLiveData.postValue(movieLiveData.getValue());
            }
        });

    }

    public MutableLiveData<Movie> getFavouriteMovieById(long movieId) {
        return movieMutableLiveData;
    }

    private void setMoviesBasedOnFavourite(final String sort_order) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mMoviesDao.countAllFavouriteMovies();
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: number of Favourite movies " + count);
                final LiveData<List<Movie>> movieList = mMoviesDao.getFavouriteMovies();
                movieList.observeForever(new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> movies) {
                        if (sort_order.equals(ServiceGenerator.ORDER_FAVOURITE)) {
                            movieData.postValue(movieList.getValue());
                            if (ServiceGenerator.LOCAL_LOGD)
                                Log.d(LOG_TAG, "su: Select LiveData Number of Favourite movies=" + movieList.getValue().size());
                        }
                    }
                });
            }
        });
    }

    private void setMoviesBasedOnPopularity() {
        setMoviesFromTMDBServer(ServiceGenerator.ORDER_POPULARITY);
    }

    private void setMoviesBasedOnTopRated() {
        setMoviesFromTMDBServer(ServiceGenerator.ORDER_TOPRATED);
    }

    public void setMoviesBasedOnSortOrder(String sort_order) {
        sort_by = sort_order;
        initializeData();
        if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY)) {
            setMoviesBasedOnPopularity();
        } else if (sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            setMoviesBasedOnTopRated();
        } else if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
            setMoviesBasedOnFavourite(sort_by);
            if (ServiceGenerator.LOCAL_LOGD)
                Log.d(LOG_TAG, "su: returning setMoviesBasedOnSortOrder " + sort_by);
        }
    }


    /**
     * This is used to get the Movies response from TMDB Server
     *
     * @param sort_by popular or top rated
     * @return Response of retrofit
     */
    public void setMoviesFromTMDBServer(String sort_by) {
        MovieService.MovieAPI client = ServiceGenerator.createService(MovieService.MovieAPI.class);
        Call<MovieResponse> call = null;
        if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY)) {
            call = client.getPopularMovieList(BuildConfig.API_KEY);
        } else if (sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            call = client.getTopRatedMovieList(BuildConfig.API_KEY);
        }
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<Movie> movielist = response.body().getMovielist();
                    movieData.postValue(movielist);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network Error");
                    movieData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d("Network", "No Internet" + t.getMessage());
                movieData.postValue(null);
            }
        });

    }

    private void setTrailerListByMovieId(long movieId) {
        MovieService.MovieAPI client = ServiceGenerator.createService(MovieService.MovieAPI.class);
        Call<MovieTrailerResponse> call = null;

        call = client.fetchTrailers(Long.toString(movieId), BuildConfig.API_KEY);

        call.enqueue(new Callback<MovieTrailerResponse>() {
            @Override
            public void onResponse(Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<MovieTrailerResponse.Trailer> trailerList = response.body().getMovieTrailerlist();
                    isInternetErrorForTrailer.postValue(false);//No internet Error
                    trailerData.postValue(trailerList);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Data Error");
                    isInternetErrorForTrailer.postValue(false);
                    trailerData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: No Internet" + t.getMessage());
                isInternetErrorForTrailer.postValue(true);
                trailerData.postValue(null);

            }
        });
    }

    private void setReviewListByMovieId(long movieId) {
        MovieService.MovieAPI client = ServiceGenerator.createService(MovieService.MovieAPI.class);
        Call<MovieReviewResponse> call = null;
        call = client.fetchReviews(Long.toString(movieId), BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<MovieReviewResponse.Review> reviewList = response.body().getMovieReviewlist();
                    isInternetErrorForReview.postValue(false);//No internet Error
                    reviewData.postValue(reviewList);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Data Error");
                    isInternetErrorForReview.postValue(false);
                    reviewData.postValue(null);
                }

            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: No Internet" + t.getMessage());
                isInternetErrorForReview.postValue(true);
                reviewData.postValue(null);

            }
        });
    }

    public MutableLiveData<Boolean> getIsInternetErrorForTrailer() {
        return isInternetErrorForTrailer;
    }

    public MutableLiveData<Boolean> getIsInternetErrorForReview() {
        return isInternetErrorForReview;
    }
}
