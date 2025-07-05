package com.manikandan.capturecrime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.manikandan.capturecrime.Adapters.CrimeAdapter;
import com.manikandan.capturecrime.CrimeActivity;
import com.manikandan.capturecrime.CrimeViewPagerActivity;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeListViewModel;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.interfaces.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.List;

public class CrimeListFragment extends Fragment implements RecyclerViewInterface {
    private List<CrimeEntity> crimeList = new ArrayList<>();
    private CrimeAdapter crimeAdapter = null;
    private static final String EXTRA_CRIME_ID = "com.manikandan.capturecrime.crimeID";
    private ShareActionProvider shareActionProvider;
    private CrimeListViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.crime_recycle_view);
        FloatingActionButton fab = view.findViewById(R.id.add_new_crime_fab);
        fab.setOnClickListener(v -> createNewCrime());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        crimeAdapter = new CrimeAdapter(crimeList, getActivity(), this);
        recyclerView.setAdapter(crimeAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        handleItemSwipe(recyclerView);
        viewModel = new ViewModelProvider(this).get(CrimeListViewModel.class);
        viewModel.getAllCrimes().observe(getViewLifecycleOwner(), crimes -> {
            crimeList.clear();
            if (crimes != null) crimeList.addAll(crimes);
            crimeAdapter.notifyDataSetChanged();
        });
        return view;
    }

    private void createNewCrime() {
        CrimeEntity crime = new CrimeEntity(java.util.UUID.randomUUID(), "", new java.util.Date(), false, "", "", "", "");
        viewModel.insert(crime);
        Intent intent = new Intent(getActivity(), CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crime.id);
        startActivity(intent);
    }

    private void handleItemSwipe(RecyclerView recyclerView) {
        final CrimeEntity[] deletedCrime = {null};
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    deletedCrime[0] = crimeList.get(position);
                    viewModel.delete(deletedCrime[0]);
                    Snackbar.make(recyclerView, deletedCrime[0].title + " Deleted!", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> viewModel.insert(deletedCrime[0])).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemClick(int position) {
        Snackbar.make(requireView(), crimeList.get(position).title, Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), CrimeViewPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeList.get(position).id);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_menu, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareActionIntent(getCrimeListDetails());
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_new_crime) {
            createNewCrime();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getCrimeListDetails() {
        StringBuilder crimeDetails = new StringBuilder("Crime List : ");
        for (CrimeEntity crime : crimeList) {
            crimeDetails.append("\n").append(crime.title);
        }
        return crimeDetails.toString();
    }
}
