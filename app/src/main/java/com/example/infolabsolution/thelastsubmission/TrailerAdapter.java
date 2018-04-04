package com.example.infolabsolution.thelastsubmission;



import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.List;

import com.example.infolabsolution.thelastsubmission.R;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private List<Trailer> mTrailerData;

    private String[] mKeyStrings;

    private Context mContext;

    private final TrailerAdapterOnClickHandler mClickHandler;


    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }


    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder trailerAdapterViewHolder, int position) {
        int currentColor = getTrailerFabBackgroundColor(position);
        trailerAdapterViewHolder.mPlayTrailerFab.setBackgroundTintList(ColorStateList
                .valueOf(currentColor));
        int trailerNumber = position + 1;
        trailerAdapterViewHolder.mPlayTrailerFab.setContentDescription(mContext.getString(R.string.a11y_trailer_fab_button) + trailerNumber);
    }


    @Override
    public int getItemCount() {
        if (mTrailerData == null) {
            return 0;
        }
        return mTrailerData.size();
    }


    public void setTrailerData(List<Trailer> trailerData) {
        mTrailerData = trailerData;
        String[] arrayKeyString = new String[trailerData.size()];
        for (int i = 0; i < trailerData.size(); i++) {
            String currentKeyString = trailerData.get(i).getKeyString();
            arrayKeyString[i] = currentKeyString;
        }
        mKeyStrings = arrayKeyString;
        notifyDataSetChanged();

    }

    // Cache of the children views for a trailer.
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final FloatingActionButton mPlayTrailerFab;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mPlayTrailerFab = (FloatingActionButton) view.findViewById(R.id.fab_trailer_icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String currentTrailerSourceKey = mKeyStrings[adapterPosition];
            mClickHandler.onClick(currentTrailerSourceKey);
        }
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerSourceKey);
    }

    protected int getTrailerFabBackgroundColor(int position) {
        int trailerFabBackgroundColorId;
        switch (position) {
            case 0:
                trailerFabBackgroundColorId = R.color.trailer0;
                break;
            case 1:
                trailerFabBackgroundColorId = R.color.trailer1;
                break;
            case 2:
                trailerFabBackgroundColorId = R.color.trailer2;
                break;
            case 3:
                trailerFabBackgroundColorId = R.color.trailer3;
                break;
            case 4:
                trailerFabBackgroundColorId = R.color.trailer4;
                break;
            case 5:
                trailerFabBackgroundColorId = R.color.trailer5;
                break;
            case 6:
                trailerFabBackgroundColorId = R.color.trailer6;
                break;
            case 7:
                trailerFabBackgroundColorId = R.color.trailer7;
                break;
            case 8:
                trailerFabBackgroundColorId = R.color.trailer8;
                break;
            case 9:
                trailerFabBackgroundColorId = R.color.trailer9;
                break;
            default:
                trailerFabBackgroundColorId = R.color.trailer10;
                break;
        }
        return ContextCompat.getColor(mContext, trailerFabBackgroundColorId);
    }
}
