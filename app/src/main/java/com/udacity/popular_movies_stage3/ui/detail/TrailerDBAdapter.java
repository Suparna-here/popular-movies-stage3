package com.udacity.popular_movies_stage3.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popular_movies_stage3.R;
import com.udacity.popular_movies_stage3.data.database.MovieTrailerResponse;
import com.udacity.popular_movies_stage3.data.network.ServiceGenerator;

import java.util.List;


/**
 * {@link TrailerDBAdapter} exposes a list of movie details to a
 * {@link RecyclerView}
 */
public class TrailerDBAdapter extends RecyclerView.Adapter<TrailerDBAdapter.TrailerDBAdapterViewHolder>{
    private List<MovieTrailerResponse.Trailer> mTrailerData;


    private final TrailerDBAdapterOnClickHandler mClickHandler;

    public interface TrailerDBAdapterOnClickHandler {
        void itemClickListener(MovieTrailerResponse.Trailer trailer);
    }

    public TrailerDBAdapter(TrailerDBAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a movie grid item.
     */
    public class TrailerDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView trailerThumbnailIV;


        public TrailerDBAdapterViewHolder(View view) {
            super(view);
            trailerThumbnailIV = (ImageView) view.findViewById(R.id.iv_trailer_thumbnail);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked Movie's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mTrailerData.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public TrailerDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerDBAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerDBAdapterViewHolder holder, int position) {
        MovieTrailerResponse.Trailer trailer = mTrailerData.get(position);
        String posteUrl= ServiceGenerator.YOUTUBE_URL+trailer.getKey()+"/mqdefault.jpg";
        Picasso.get()
               .load(posteUrl)
               .placeholder(R.mipmap.ic_launcher)
               .into(holder.trailerThumbnailIV);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    /*
     * This method is used to set the movie data on a MovieDBAdapter
     * @param trailerData The new movie data to be displayed.
     */
    public void setTrailerData(List<MovieTrailerResponse.Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

}
