package com.manikandan.capturecrime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.data.CrimeRepository;

import java.util.UUID;

public class CrimeDetailViewModel extends AndroidViewModel {
    // TODO: Add unit tests for ViewModel logic and LiveData transformations
    private final CrimeRepository repository;

    public CrimeDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new CrimeRepository(application);
    }

    public LiveData<CrimeEntity> getCrimeById(UUID id) {
        return repository.getCrimeById(id);
    }

    public void updateCrime(CrimeEntity crime) {
        repository.updateCrime(crime);
    }

    public void deleteCrime(CrimeEntity crime) {
        repository.deleteCrime(crime);
    }

    public void insertCrime(CrimeEntity crime) {
        repository.insertCrime(crime);
    }
}
