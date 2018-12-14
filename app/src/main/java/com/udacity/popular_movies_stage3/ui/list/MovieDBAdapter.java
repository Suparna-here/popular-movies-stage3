package com.udacity.popular_movies_stage3.ui.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popular_movies_stage3.R;
import com.udacity.popular_movies_stage3.data.database.Movie;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;

import java.util.List;


/**
 * {@link MovieDBAdapter} exposes a list of movie details to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MovieDBAdapter extends RecyclerView.Adapter<MovieDBAdapter.MovieDBAdapterViewHolder>{
    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_FAVOURITE = 1;
    private List<Movie> mMovieData;
    private String sort_by;


    private final MovieDBAdapterOnClickHandler mClickHandler;

    public interface MovieDBAdapterOnClickHandler {
        void itemClickListener(Movie movie);
    }

    public MovieDBAdapter(MovieDBAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a movie grid item.
     */
    public class MovieDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView posterThumbnailIV;
        public final ImageView favourite_markedIV;
        public final TextView releaseDateTV;


        public MovieDBAdapterViewHolder(View view) {
            super(view);
            posterThumbnailIV = (ImageView) view.findViewById(R.id.iv_movie_thumbnail);
            favourite_markedIV=(ImageView)view.findViewById(R.id.iv_favourite_marked);
            releaseDateTV=(TextView)view.findViewById(R.id.tv_release_date);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked Movie's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mMovieData.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public MovieDBAdapter.MovieDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new MovieDBAdapterViewHolder(view);
    }

    /**
     * Returns the the layout id depending on whether the movie item is a normal item or the favourite movie item.
     * @param viewType
     * @return
     */
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {

            case VIEW_TYPE_REGULAR: {
                return R.layout.movie_list_item;
            }

            case VIEW_TYPE_FAVOURITE: {
                return R.layout.favourite_movie_list_item;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDBAdapter.MovieDBAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        String posteUrl= ServiceGenerator.POSTER_URL+movie.getPoster_path();
        Picasso.get()
               .load(posteUrl)
               .placeholder(R.mipmap.ic_launcher)
               .into(holder.posterThumbnailIV);
        holder.releaseDateTV.setText(movie.getRelease_date());
        holder.releaseDateTV.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(sort_by.equals(ServiceGenerator.ORDER_POPULARITY) || sort_by.equals(ServiceGenerator.ORDER_TOPRATED))
            return VIEW_TYPE_REGULAR;
        else
            return VIEW_TYPE_FAVOURITE;
    }

    /*
     * This method is used to set the movie data on a MovieDBAdapter
     * @param movieData The new movie data to be displayed.
     */
    public void setMovieData(List<Movie> movieData, String sort_by) {
        mMovieData = movieData;
        this.sort_by=sort_by;
        notifyDataSetChanged();
    }
}
