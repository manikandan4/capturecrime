package com.manikandan.capturecrime.di;

import android.content.Context;
import androidx.room.Room;
import com.manikandan.capturecrime.data.AppDatabase;
import com.manikandan.capturecrime.data.CrimeDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt module that provides database-related dependencies.
 * This module is installed in the SingletonComponent, making dependencies available app-wide.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    /**
     * Provides the Room database instance.
     * The database is created as a singleton to ensure single source of truth.
     */
    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "crime_database"
        ).build();
    }

    /**
     * Provides the CrimeDao from the database.
     * Since the database is a singleton, the DAO will also be effectively singleton.
     */
    @Provides
    public CrimeDao provideCrimeDao(AppDatabase database) {
        return database.crimeDao();
    }
}
