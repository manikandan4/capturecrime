package com.manikandan.capturecrime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.fragments.DatePickerFragment;
import com.manikandan.capturecrime.interfaces.RecyclerViewInterface;
import com.manikandan.capturecrime.viewholders.CrimeHolder;

import java.util.List;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
    List<CrimeEntity> mCrimeList;
    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public CrimeAdapter(List<CrimeEntity> mCrimeList, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.mCrimeList = mCrimeList;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_crime, parent, false);
        view.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new CrimeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
        CrimeEntity mCrime = mCrimeList.get(position);
        holder.getmTitleText().setText(mCrime.title);
        int[] dateArr = DatePickerFragment.getDateFormatted(mCrime.date);
        dateArr[1] = dateArr[1] + 1;
        // Use string resource for date formatting
        String dateStr = context.getString(R.string.crime_date_format, dateArr[0], dateArr[1], dateArr[2]);
        holder.getmDateText().setText(dateStr);
        holder.getMcrimeSolvedImg().setVisibility((mCrime.solved) ? View.VISIBLE : View.GONE);
        holder.getParentView().setOnClickListener(v -> recyclerViewInterface.onItemClick(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mCrimeList.size();
    }
}
