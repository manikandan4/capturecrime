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
import androidx.core.view.MenuHost;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.manikandan.capturecrime.Adapters.CrimeAdapter;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeListViewModel;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.interfaces.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.List;

public class CrimeListFragment extends Fragment implements RecyclerViewInterface {
    // TODO: Add search and filter functionality for crimes
    // TODO: Add export (CSV/PDF) and notification features
    private List<CrimeEntity> crimeList = new ArrayList<>();
    private CrimeAdapter crimeAdapter = null;
    private ShareActionProvider shareActionProvider;
    private CrimeListViewModel viewModel;
    private View progressBar; // Loading indicator
    private Snackbar undoSnackbar; // For undo feedback
    private CrimeEntity recentlyDeletedCrime;
    private int recentlyDeletedPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.crime_recycle_view);
        recyclerView.setContentDescription("Crime list"); // Accessibility
        FloatingActionButton fab = view.findViewById(R.id.add_new_crime_fab);
        fab.setContentDescription("Add new crime"); // Accessibility
        progressBar = view.findViewById(R.id.progress_bar); // Loading indicator
        progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            crimeList.clear();
            if (crimes != null) crimeList.addAll(crimes);
            crimeAdapter.notifyDataSetChanged();
            if (crimeList.isEmpty()) {
                Snackbar.make(recyclerView, "No crimes found.", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Modern menu handling
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.crime_menu, menu);
                MenuItem item = menu.findItem(R.id.action_share);
                shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                setShareActionIntent(getCrimeListDetails());
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.add_new_crime) {
                    createNewCrime();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    private void createNewCrime() {
        try {
            CrimeEntity crime = new CrimeEntity(java.util.UUID.randomUUID(), "", new java.util.Date(), false, "", "", "", "");
            viewModel.insert(crime);
            Snackbar.make(requireView(), "Crime added", Snackbar.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            Bundle args = new Bundle();
            args.putString("crime_id", crime.id.toString());
            navController.navigate(R.id.action_crimeListFragment_to_crimeDetailFragment, args);
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error adding crime", Snackbar.LENGTH_LONG).show();
        }
    }

    private void handleItemSwipe(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                recentlyDeletedCrime = crimeList.get(position);
                recentlyDeletedPosition = position;
                viewModel.delete(recentlyDeletedCrime);
                crimeList.remove(position);
                crimeAdapter.notifyItemRemoved(position);
                showUndoSnackbar(recyclerView);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showUndoSnackbar(View anchor) {
        undoSnackbar = Snackbar.make(anchor, "Crime deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> undoDelete());
        undoSnackbar.show();
    }

    private void undoDelete() {
        if (recentlyDeletedCrime != null) {
            viewModel.insert(recentlyDeletedCrime);
            crimeList.add(recentlyDeletedPosition, recentlyDeletedCrime);
            crimeAdapter.notifyItemInserted(recentlyDeletedPosition);
            Snackbar.make(requireView(), "Undo successful", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        Snackbar.make(requireView(), crimeList.get(position).title, Snackbar.LENGTH_LONG).show();
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("crime_id", crimeList.get(position).id.toString());
        navController.navigate(R.id.action_crimeListFragment_to_crimeDetailFragment, args);
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    private String getCrimeListDetails() {
        StringBuilder crimeDetails = new StringBuilder("Crime List : ");
        for (CrimeEntity crime : crimeList) {
            crimeDetails.append("\n").append(crime.title);
        }
        return crimeDetails.toString();
    }
}
