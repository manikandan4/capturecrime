package com.manikandan.capturecrime.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.data.CrimeRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for handling crime list operations.
 * Uses Hilt dependency injection for better testability and separation of concerns.
 */
@HiltViewModel
public class CrimeListViewModel extends ViewModel {
    // TODO: Add unit tests for ViewModel logic and LiveData transformations
    private final CrimeRepository repository;
    private final LiveData<List<CrimeEntity>> allCrimes;

    /**
     * Constructor with Hilt dependency injection.
     * The CrimeRepository is provided by Hilt automatically.
     */
    @Inject
    public CrimeListViewModel(CrimeRepository repository) {
        this.repository = repository;
        allCrimes = repository.getAllCrimes();
    }

    public LiveData<List<CrimeEntity>> getAllCrimes() {
        return allCrimes;
    }

    public void insert(CrimeEntity crime) {
        repository.insertCrime(crime);
    }

    public void delete(CrimeEntity crime) {
        repository.deleteCrime(crime);
    }

    public void update(CrimeEntity crime) {
        repository.updateCrime(crime);
    }
}
