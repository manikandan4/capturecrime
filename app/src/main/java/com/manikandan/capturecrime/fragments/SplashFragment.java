package com.manikandan.capturecrime.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.manikandan.capturecrime.R;

/**
 * SplashFragment displays the splash screen when the app launches.
 *
 * Business/Logical Flow:
 * - Provides a branded entry point and short delay before showing the main content.
 * - Used to initialize resources or show a logo, improving user experience and perceived performance.
 *
 * Technical Aspects:
 * - Uses a Handler to delay navigation for SPLASH_DELAY_MS milliseconds.
 * - After the delay, navigates to CrimeListFragment using Navigation Component (NavHostFragment).
 * - NavHostFragment is used to manage navigation between fragments, ensuring a consistent back stack and transitions.
 * - No user interaction is allowed during splash; navigation is automatic.
 *
 * Why is this needed?
 * - Ensures users see a splash screen before accessing the main app features.
 * - Centralizes navigation logic for the app's entry point.
 */
public class SplashFragment extends Fragment {
    private static final long SPLASH_DELAY_MS = 1500; // 1.5 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_splashFragment_to_crimeListFragment);
        }, SPLASH_DELAY_MS);
    }
}
