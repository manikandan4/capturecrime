package com.manikandan.capturecrime.data;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.manikandan.capturecrime.utils.ImageUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository class that handles data operations for Crime entities.
 * Uses Hilt dependency injection for better testability and separation of concerns.
 */
@Singleton
public class CrimeRepository {
    // TODO: Add unit tests for repository data operations and threading
    private final CrimeDao crimeDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Constructor with Hilt dependency injection.
     * The CrimeDao is provided by the DatabaseModule.
     */
    @Inject
    public CrimeRepository(CrimeDao crimeDao) {
        this.crimeDao = crimeDao;
    }

    public LiveData<List<CrimeEntity>> getAllCrimes() {
        return crimeDao.getAllCrimes();
    }

    public LiveData<CrimeEntity> getCrimeById(UUID id) {
        return crimeDao.getCrimeById(id);
    }

    public void insertCrime(CrimeEntity crime) {
        executor.execute(() -> crimeDao.insertCrime(crime));
    }

    public void updateCrime(CrimeEntity crime) {
        executor.execute(() -> crimeDao.updateCrime(crime));
    }

    public void deleteCrime(CrimeEntity crime) {
        executor.execute(() -> {
            // Delete associated image file if it exists using ImageUtils
            if (!TextUtils.isEmpty(crime.photoPath)) {
                ImageUtils.deleteImageFile(crime.photoPath);
            }
            crimeDao.deleteCrime(crime);
        });
    }
}
