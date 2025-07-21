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
import com.manikandan.capturecrime.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * CrimeListFragment displays the list of all crimes in the app.
 * Business/Logical Flow:
 * - Central hub for users to view, add, delete, and share crime records.
 * - Supports swipe-to-delete with undo, and navigation to crime details for editing/viewing.
 * - FloatingActionButton allows users to add new crimes, supporting the main workflow.
 * Technical Aspects:
 * - Uses RecyclerView for efficient, scrollable list display.
 * - CrimeAdapter binds data to views and handles click events via RecyclerViewInterface.
 * - ViewModel (CrimeListViewModel) provides data and handles database operations using LiveData for reactive UI updates.
 * - Navigation Component (NavHostFragment) is used to navigate to CrimeFragment for details/editing.
 * - MenuProvider and ShareActionProvider enable sharing the crime list and adding new crimes from the menu.
 * - ItemTouchHelper enables swipe-to-delete with undo functionality.
 * Why is this needed?
 * - Provides the main interface for users to interact with crime data.
 * - Ensures robust, user-friendly management of crime records with modern Android architecture components.
 */
@AndroidEntryPoint
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

    /**
     * Inflates the layout for this fragment, initializes the RecyclerView, and sets up UI components.*
     * This method is called when the fragment's view is first created. It performs the following key tasks:
     * - Inflates the `fragment_crime_list` layout.
     * - Sets up the {@link RecyclerView} with a {@link LinearLayoutManager} and a {@link CrimeAdapter}.
     * - Adds vertical spacing between RecyclerView items using a custom {@link VerticalSpaceItemDecoration}.
     * - Initializes the {@link FloatingActionButton} for adding new crimes.
     * - Sets up a {@link SnapHelper} for a smoother scrolling experience in the RecyclerView.
     * - Attaches an {@link ItemTouchHelper} to handle swipe-to-delete gestures.
     * - Initializes the {@link CrimeListViewModel} and observes the list of crimes using {@link androidx.lifecycle.LiveData}.
     *   When the data changes, the adapter is updated.
     * - Sets up the menu using {@link MenuProvider}, including a {@link ShareActionProvider}.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.crime_recycle_view);
        recyclerView.setContentDescription("Crime list"); // Accessibility
        // Add vertical space between cards (16dp)
        int verticalSpace = (int) (16 * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpace));
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

    /**
     * Creates a new crime, inserts it into the database, and navigates to the detail screen.
     * This method generates a new {@link CrimeEntity} with a unique ID and default values.
     * It uses the {@link CrimeListViewModel} to persist the new crime and then navigates
     * to the {@link CrimeFragment} to allow the user to edit the details.
     * A {@link Snackbar} provides feedback to the user upon successful creation.
     */
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

    /**
     * Sets up swipe-to-delete functionality for the RecyclerView.
     * This method uses {@link ItemTouchHelper.SimpleCallback} to detect left or right swipes on a RecyclerView item.
     * When an item is swiped, the corresponding crime is marked for deletion, removed from the list,
     * and a Snackbar with an "UNDO" action is displayed.
     *
     * @param recyclerView The RecyclerView to attach the swipe handler to.
     */
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

    /**
     * Displays a Snackbar with an "UNDO" action to revert a deletion.
     * This provides a short window for the user to undo the delete operation, improving user experience.
     *
     * @param anchor The view to which the Snackbar will be anchored.
     */
    private void showUndoSnackbar(View anchor) {
        undoSnackbar = Snackbar.make(anchor, "Crime deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> undoDelete());
        undoSnackbar.show();
    }

    /**
     * Reverts the deletion of a crime.
     * This method re-inserts the {@code recentlyDeletedCrime} into the database and adds it back to the
     * RecyclerView at its original position. It is triggered by the "UNDO" action in the Snackbar.
     */
    private void undoDelete() {
        if (recentlyDeletedCrime != null) {
            viewModel.insert(recentlyDeletedCrime);
            crimeList.add(recentlyDeletedPosition, recentlyDeletedCrime);
            crimeAdapter.notifyItemInserted(recentlyDeletedPosition);
            Snackbar.make(requireView(), "Undo successful", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles click events on RecyclerView items.
     * This method is called when a user taps on a crime in the list. It navigates to the
     * {@link CrimeFragment} for the selected crime, passing the crime's ID in the arguments.
     *
     * @param position The position of the clicked item in the RecyclerView.
     */
    @Override
    public void onItemClick(int position) {
        Snackbar.make(requireView(), crimeList.get(position).title, Snackbar.LENGTH_LONG).show();
        NavController navController = NavHostFragment.findNavController(this);
        Bundle args = new Bundle();
        args.putString("crime_id", crimeList.get(position).id.toString());
        navController.navigate(R.id.action_crimeListFragment_to_crimeDetailFragment, args);
    }

    /**
     * Sets the share intent for the {@link ShareActionProvider}.
     * This method creates an {@link Intent#ACTION_SEND} intent with the crime list details
     * as plain text, allowing the user to share the list with other apps.
     *
     * @param text The text content to be shared.
     */
    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    /**
     * Generates a string summary of the crime list for sharing.
     * This method iterates through the list of crimes and concatenates their titles
     * into a single string, which can be shared via the {@link ShareActionProvider}.
     *
     * @return A string containing the details of all crimes in the list.
     */
    private String getCrimeListDetails() {
        StringBuilder crimeDetails = new StringBuilder("Crime List : ");
        for (CrimeEntity crime : crimeList) {
            crimeDetails.append("\n").append(crime.title);
        }
        return crimeDetails.toString();
    }
}
