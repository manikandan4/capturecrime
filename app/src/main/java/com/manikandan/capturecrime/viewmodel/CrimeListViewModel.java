package com.manikandan.capturecrime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.data.CrimeRepository;

import java.util.List;
import java.util.UUID;

public class CrimeListViewModel extends AndroidViewModel {
    private final CrimeRepository repository;
    private final LiveData<List<CrimeEntity>> allCrimes;

    public CrimeListViewModel(@NonNull Application application) {
        super(application);
        repository = new CrimeRepository(application);
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

    public LiveData<CrimeEntity> getCrimeById(UUID id) {
        return repository.getCrimeById(id);
    }
}

