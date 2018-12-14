package com.udacity.popular_movies_stage3.data.database;

import java.util.List;

public class MovieResponse {
    private List<Movie> results;

    public void setMovielist(List<Movie> results) {
        this.results = results;
    }

    public List<Movie> getMovielist() {
        return results;
    }
}
