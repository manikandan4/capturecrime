package com.manikandan.capturecrime;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

/**
 * Custom Application class for Hilt dependency injection.
 * This class serves as the application-level dependency container.
 */
@HiltAndroidApp
public class CrimeTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Hilt will automatically initialize the dependency graph
    }
}
