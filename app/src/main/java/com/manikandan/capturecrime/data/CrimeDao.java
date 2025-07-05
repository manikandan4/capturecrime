package com.manikandan.capturecrime.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface CrimeDao {
    @Query("SELECT * FROM crimes ORDER BY date DESC")
    LiveData<List<CrimeEntity>> getAllCrimes();

    @Query("SELECT * FROM crimes WHERE id = :id LIMIT 1")
    LiveData<CrimeEntity> getCrimeById(UUID id);

    @Insert
    void insertCrime(CrimeEntity crime);

    @Update
    void updateCrime(CrimeEntity crime);

    @Delete
    void deleteCrime(CrimeEntity crime);
}

