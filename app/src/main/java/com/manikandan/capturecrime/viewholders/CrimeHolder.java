package com.manikandan.capturecrime.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manikandan.capturecrime.R;

public class CrimeHolder extends RecyclerView.ViewHolder {
    private ImageView crimeImage;
    private TextView mTitleText;
    private TextView mDateText;
    private TextView mLocationText;
    private TextView mSolvedChip;
    private TextView mDescriptionText;
    private View parentView;

    public CrimeHolder(@NonNull View itemView) {
        super(itemView);
        crimeImage = itemView.findViewById(R.id.item_crime_image);
        mTitleText = itemView.findViewById(R.id.item_crime_title);
        mDateText = itemView.findViewById(R.id.item_crime_date);
        mLocationText = itemView.findViewById(R.id.item_crime_location);
        mSolvedChip = itemView.findViewById(R.id.item_crime_solved);
        mDescriptionText = itemView.findViewById(R.id.item_crime_description);
        parentView = itemView.findViewById(R.id.single_crime_item);
    }

    public ImageView getCrimeImage() { return crimeImage; }
    public TextView getmTitleText() { return mTitleText; }
    public TextView getmDateText() { return mDateText; }
    public TextView getmLocationText() { return mLocationText; }
    public TextView getmSolvedChip() { return mSolvedChip; }
    public TextView getmDescriptionText() { return mDescriptionText; }
    public View getParentView() { return parentView; }
}
