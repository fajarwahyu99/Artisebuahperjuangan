package com.example.infolabsolution.thelastsubmission;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.example.infolabsolution.thelastsubmission.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> mReviewData;

    private String[] mAuthorStrings;

    private String[] mContentStrings;

    public ReviewAdapter() {
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        reviewAdapterViewHolder.mReviewAuthor.setText(mAuthorStrings[position]);
        reviewAdapterViewHolder.mReviewContent.setText(mContentStrings[position]);
    }


    @Override
    public int getItemCount() {
        if (mReviewData == null) {
            return 0;
        }
        return mReviewData.size();
    }

    public void setReviewData(List<Review> reviewData) {
        mReviewData = reviewData;
        String[] arrayAuthor = new String[reviewData.size()];
        String[] arrayContent = new String[reviewData.size()];

        for (int i = 0; i < reviewData.size(); i++) {
            String currentReviewAuthor = reviewData.get(i).getAuthor();
            String currentReviewContent = reviewData.get(i).getReviewContent();
            arrayAuthor[i] = currentReviewAuthor;
            arrayContent[i] = currentReviewContent;
        }

        mAuthorStrings = arrayAuthor;
        mContentStrings = arrayContent;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewAuthor = (TextView) view.findViewById(R.id.list_item_user_review_author_name);
            mReviewContent = (TextView) view.findViewById(R.id.list_item_user_review_content);
        }
    }
}
