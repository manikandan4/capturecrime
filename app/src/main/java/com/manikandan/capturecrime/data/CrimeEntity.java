package com.manikandan.capturecrime.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "crimes")
@TypeConverters({CrimeTypeConverters.class})
public class CrimeEntity {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public UUID id;
    public String title;
    public Date date;
    public boolean solved;
    public String description;
    public String location;
    public String suspect;
    public String photoPath;

    public CrimeEntity(UUID id, String title, Date date, boolean solved, String description, String location, String suspect, String photoPath) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.solved = solved;
        this.description = description;
        this.location = location;
        this.suspect = suspect;
        this.photoPath = photoPath;
    }
}
