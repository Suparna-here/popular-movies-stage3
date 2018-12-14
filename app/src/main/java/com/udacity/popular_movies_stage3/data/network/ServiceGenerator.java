package com.udacity.popular_movies_stage3.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String BASE_URL = "https://api.themoviedb.org";
    public static final String POSTER_URL="http://image.tmdb.org/t/p/w500";
    public static final String YOUTUBE_URL="http://img.youtube.com/vi/";
    public static final String ORDER_POPULARITY="popularity.desc";
    public static final String ORDER_TOPRATED="vote_average.desc";
    public static final String ORDER_FAVOURITE="favourite.desc";
    public static final boolean LOCAL_LOGD=false;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}