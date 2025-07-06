package com.manikandan.capturecrime.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.manikandan.capturecrime.utils.ImageUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CrimeRepository {
    // TODO: Add unit tests for repository data operations and threading
    private final CrimeDao crimeDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public CrimeRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        crimeDao = db.crimeDao();
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
