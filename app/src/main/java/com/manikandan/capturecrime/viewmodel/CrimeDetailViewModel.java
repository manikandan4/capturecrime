package com.manikandan.capturecrime.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.data.CrimeRepository;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for handling crime detail operations.
 * Uses Hilt dependency injection for better testability and separation of concerns.
 */
@HiltViewModel
public class CrimeDetailViewModel extends ViewModel {
    // TODO: Add unit tests for ViewModel logic and LiveData transformations
    private final CrimeRepository repository;

    /**
     * Constructor with Hilt dependency injection.
     * The CrimeRepository is provided by Hilt automatically.
     */
    @Inject
    public CrimeDetailViewModel(CrimeRepository repository) {
        this.repository = repository;
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
