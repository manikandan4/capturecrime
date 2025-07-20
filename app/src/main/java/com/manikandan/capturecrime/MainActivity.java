package com.manikandan.capturecrime;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * MainActivity is the entry point and host for all fragments in the app.
 * <p>
 * Purpose:
 * - Hosts the Navigation Component's NavHostFragment, enabling navigation between screens.
 * - Sets up the MaterialToolbar as the app's main toolbar.
 * - Manages toolbar visibility based on navigation destination (e.g., hides toolbar on splash screen).
 * - Integrates Hilt for dependency injection, allowing dependencies to be injected into this activity and its fragments.
 * <p>
 * Why Navigation Component?
 * - Simplifies navigation and back stack management between fragments.
 * - Provides type-safe navigation and argument passing.
 * <p>
 * Why Hilt?
 * - Enables easy dependency injection for activity and fragments, improving modularity and testability.
 * <p>
 * Usage:
 * - All navigation logic and toolbar management should be handled here.
 * - Fragments are swapped in/out via the NavHostFragment.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the main layout for the activity. This hosts the NavHostFragment, which manages navigation between screens.
        setContentView(R.layout.activity_main);

        // Find and set up the MaterialToolbar as the app's main toolbar.
        // Business: Provides consistent navigation and actions across screens.
        // Technical: Required for NavigationUI to handle up navigation and toolbar title updates.
        MaterialToolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // NavHostFragment is the core of the Navigation Component.
        // Business: Allows users to move between different screens (fragments) such as reporting a crime, viewing details, etc.
        // Technical: Manages the navigation graph, fragment transactions, and back stack automatically.
        // Integration: Defined in activity_main.xml, it is retrieved and used to get the NavController.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            // NavigationUI integrates the NavController with the toolbar for up navigation and title management.
            NavigationUI.setupActionBarWithNavController(this, navController);
            // Hide toolbar on splash fragment for a cleaner onboarding experience.
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Business: Splash screen should not show app bar for better UX.
                // Technical: Dynamically hides/shows toolbar based on navigation destination.
                if (destination.getId() == R.id.splashFragment) {
                    toolbar.setVisibility(android.view.View.GONE);
                } else {
                    toolbar.setVisibility(android.view.View.VISIBLE);
                }
            });
        }
    }

    /**
     * Handles up navigation when the user presses the back arrow in the toolbar.
     * Business: Ensures users can navigate back through the app's screens intuitively.
     * Technical: Delegates navigation to the NavController, which manages the back stack.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            return navHostFragment.getNavController().navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
