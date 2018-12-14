package com.udacity.popular_movies_stage3.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popular_movies_stage3.R;
import com.udacity.popular_movies_stage3.data.database.MovieReviewResponse;

import java.util.List;

public class ReviewDBAdapter extends RecyclerView.Adapter<ReviewDBAdapter.ReviewDBAdapterViewHolder>{
    private List<MovieReviewResponse.Review> mReviewData;


    /*private final ReviewDBAdapter.ReviewDBAdapterOnClickHandler mClickHandler;

    public interface ReviewDBAdapterOnClickHandler {
        void itemClickListener(MovieTrailerResponse.Trailer trailer);
    }*/

    public ReviewDBAdapter() {
//        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a movie grid item.
     */
    public class ReviewDBAdapterViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener
        public final TextView reviewerNameTV, reviewContentTV;



        public ReviewDBAdapterViewHolder(View view) {
            super(view);
            reviewerNameTV = (TextView) view.findViewById(R.id.tv_reviewer_name);
            reviewContentTV = (TextView) view.findViewById(R.id.tv_review_content);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
//            view.setOnClickListener(this);
        }

        /*// Override onClick, passing the clicked Movie's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mReviewData[getAdapterPosition()]);
        }*/
    }

    @NonNull
    @Override
    public ReviewDBAdapter.ReviewDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_list_item, parent, false);
        return new ReviewDBAdapter.ReviewDBAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewDBAdapter.ReviewDBAdapterViewHolder holder, int position) {
        MovieReviewResponse.Review review = mReviewData.get(position);
        holder.reviewerNameTV.setText(review.getAuthor());
        holder.reviewContentTV.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.size();
    }

    /*
     * This method is used to set the movie data on a MovieDBAdapter
     * @param trailerData The new movie data to be displayed.
     */
    public void setReviewData(List<MovieReviewResponse.Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

}
