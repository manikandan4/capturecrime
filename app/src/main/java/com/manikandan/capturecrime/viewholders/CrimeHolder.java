package com.manikandan.capturecrime.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.manikandan.capturecrime.R;

public class CrimeHolder extends RecyclerView.ViewHolder {
    private TextView mTitleText;
    private TextView mDateText;
    private ImageView mcrimeSolvedImg;
    private ConstraintLayout parentView;

    public CrimeHolder(@NonNull View itemView) {
        super(itemView);
        mTitleText = itemView.findViewById(R.id.item_crime_title);
        mDateText = itemView.findViewById(R.id.item_crime_date);
        mcrimeSolvedImg = itemView.findViewById(R.id.img_crimeSolved);
        parentView = itemView.findViewById(R.id.item_view_parent);
    }

    public TextView getmTitleText() {
        return mTitleText;
    }

    public TextView getmDateText() {
        return mDateText;
    }

    public ImageView getMcrimeSolvedImg() {
        return mcrimeSolvedImg;
    }

    public ConstraintLayout getParentView() {
        return parentView;
    }

}
