package com.manikandan.capturecrime.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manikandan.capturecrime.R;

public class CrimeHolder extends RecyclerView.ViewHolder {
    private TextView mTitleText;
    private TextView mDateText;
    private ImageView mcrimeSolvedImg;

    public CrimeHolder(@NonNull View itemView) {
        super(itemView);
        mTitleText = itemView.findViewById(R.id.item_crime_title);
        mDateText = itemView.findViewById(R.id.item_crime_date);
        mcrimeSolvedImg = itemView.findViewById(R.id.img_crimeSolved);
    }

    public TextView getmTitleText() {
        return mTitleText;
    }

    public void setmTitleText(TextView mTitleText) {
        this.mTitleText = mTitleText;
    }

    public TextView getmDateText() {
        return mDateText;
    }

    public void setmDateText(TextView mDateText) {
        this.mDateText = mDateText;
    }

    public ImageView getMcrimeSolvedImg() {
        return mcrimeSolvedImg;
    }

    public void setMcrimeSolvedImg(ImageView mcrimeSolvedImg) {
        this.mcrimeSolvedImg = mcrimeSolvedImg;
    }
}
