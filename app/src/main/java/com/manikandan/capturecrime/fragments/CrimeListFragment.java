package com.manikandan.capturecrime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.snackbar.Snackbar;
import com.manikandan.capturecrime.Adapters.CrimeAdapter;
import com.manikandan.capturecrime.CrimeActivity;
import com.manikandan.capturecrime.CrimeLab;
import com.manikandan.capturecrime.CrimeViewPagerActivity;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.interfaces.RecyclerViewInterface;
import com.manikandan.capturecrime.models.Crime;

import java.util.List;

public class CrimeListFragment extends Fragment implements RecyclerViewInterface {
    private RecyclerView recyclerView = null;
    private RecyclerView.LayoutManager layoutManager = null;
    private List<Crime> crimeList;
    private CrimeAdapter crimeAdapter = null;
    private static final String EXTRA_CRIME_ID = "com.manikandan.capturecrime.crimeID";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        recyclerView = view.findViewById(R.id.crime_recycle_view);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        updateUI();
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        handleItemSwipe();
        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        crimeList = crimeLab.getCrimes();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimeList, getActivity(), this);
            recyclerView.setAdapter(crimeAdapter);
        } else {
            crimeAdapter.notifyDataSetChanged();
        }
    }

    private void handleItemSwipe() {
        final Crime[] deletedCrime = {new Crime()};
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    deletedCrime[0] = crimeList.get(position);
                    crimeList.remove(position);
                    crimeAdapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, deletedCrime[0].getmTitle() + " Deleted!", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                crimeList.add(position, deletedCrime[0]);
                                crimeAdapter.notifyItemInserted(position);
                            }).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemClick(int position) {
        Snackbar.make(recyclerView, crimeList.get(position).getmTitle(), Snackbar.LENGTH_LONG)
                .show();
        Intent intent = new Intent(getActivity(), CrimeViewPagerActivity.class);
        //Intent intent = new Intent(getActivity(), CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeList.get(position).getmID());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
