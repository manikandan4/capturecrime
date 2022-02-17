package com.manikandan.capturecrime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.manikandan.capturecrime.Adapters.CrimeAdapter;
import com.manikandan.capturecrime.CrimeLab;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.models.Crime;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView recyclerView = null;
    private RecyclerView.LayoutManager layoutManager = null;
    private List<Crime> crimeList;

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

        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        crimeList = crimeLab.getCrimes();

        CrimeAdapter crimeAdapter = new CrimeAdapter(crimeList, getActivity());
        recyclerView.setAdapter(crimeAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}
