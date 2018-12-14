package com.udacity.popular_movies_stage3.data.network;

import com.udacity.popular_movies_stage3.data.database.MovieResponse;
import com.udacity.popular_movies_stage3.data.database.MovieReviewResponse;
import com.udacity.popular_movies_stage3.data.database.MovieTrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieService {
    public interface MovieAPI {

        @GET("3/movie/top_rated?")
        Call<MovieResponse> getTopRatedMovieList(
        @Query("api_key") String api_key );


        @GET("3/movie/popular?")
        Call<MovieResponse> getPopularMovieList(
                @Query("api_key") String api_key );

        @GET("3/movie/{movie_id}/videos?")
        Call<MovieTrailerResponse> fetchTrailers(
                @Path("movie_id") String movie_id,
                @Query("api_key") String api_key );

        @GET("3/movie/{movie_id}/reviews?")
        Call<MovieReviewResponse> fetchReviews(
                @Path("movie_id") String movie_id,
                @Query("api_key") String api_key );

    }
}
