package com.manikandan.capturecrime;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

/**
 * CrimeTrackerApplication is the custom Application class for the CaptureCrime app.
 * <p>
 * Business/Logical Flow:
 * - This class is the root of the app lifecycle and is instantiated before any activity or service.
 * - It enables Hilt dependency injection, which is used throughout the app for managing dependencies (e.g., repositories, database, network clients).
 * - By using Hilt, the app achieves better modularity, testability, and separation of concerns.
 * <p>
 * Technical Aspects:
 * - Annotated with @HiltAndroidApp, which triggers Hilt's code generation and sets up the DI graph.
 * - All app-wide dependencies (such as singletons) are provided here or in Hilt modules.
 * - Required for Hilt to inject dependencies into Android classes (activities, fragments, services).
 * <p>
 * Why is this needed?
 * - Without this class and annotation, Hilt cannot be used for DI in the app.
 * - Ensures that dependencies are available wherever needed, supporting business logic like crime reporting, user authentication, and data persistence.
 */
@HiltAndroidApp
public class CrimeTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Hilt initializes the dependency graph here, making DI available app-wide
    }
}
