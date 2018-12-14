package com.udacity.popular_movies_stage3.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "favourite_movies", indices = {@Index(value = {"title"}, unique = true)})
public class Movie implements Parcelable {
    private long vote_count;
    @PrimaryKey
    private long id;
    private boolean video;
    private double vote_average;
    private String title;
    private double popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    @Ignore
    private ArrayList<Integer> genre_ids;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private String release_date;


    public Movie(long id, long vote_count, boolean video, double vote_average, String title, double popularity, String poster_path, String original_language, String original_title, String backdrop_path, boolean adult, String overview, String release_date) {
        this.id = id;

        this.vote_count = vote_count;

        this.video = video;
        this.vote_average = vote_average;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
//        this.genre_ids = genre_ids;
        this.backdrop_path = backdrop_path;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public long getVote_count() {
        return vote_count;
    }

    public long getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public Movie(Parcel in) {
        id = in.readLong();

        vote_count = in.readLong();

        //boolean video
        video = (Boolean) in.readValue(null);
        vote_average = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        poster_path = in.readString();
        original_language = in.readString();
        original_title = in.readString();
//        List<Integer> genre_ids
        genre_ids = (ArrayList<Integer>) in.readSerializable();
        backdrop_path = in.readString();
        //boolean adult
        adult = (Boolean) in.readValue(null);
        overview = in.readString();
        release_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeLong(id);

        destParcel.writeLong(vote_count);

        destParcel.writeValue(video);
        destParcel.writeDouble(vote_average);
        destParcel.writeString(title);
        destParcel.writeDouble(popularity);
        destParcel.writeString(poster_path);
        destParcel.writeString(original_language);
        destParcel.writeString(original_title);
        destParcel.writeSerializable(genre_ids);
        destParcel.writeString(backdrop_path);
        destParcel.writeValue(adult);
        destParcel.writeString(overview);
        destParcel.writeString(release_date);
    }

}
