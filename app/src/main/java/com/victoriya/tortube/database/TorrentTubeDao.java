package com.victoriya.tortube.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.victoriya.tortube.model.Files;

import java.util.List;

@Dao
public interface TorrentTubeDao {


    @Insert(onConflict=OnConflictStrategy.REPLACE)
    public void insertMagnetLink(Files files);

    @Query("SELECT * FROM Files ORDER BY date DESC")
    public LiveData<List<Files>> getAllFiles();

    @Query("SELECT * FROM Files WHERE magnetLink=:magnetLink")
    public LiveData<Files> getMagnetLink(String magnetLink);

    @Query("DELETE FROM Files WHERE magnetLink=:magnetLink")
    public void deleteFile(String magnetLink);

    @Query("DELETE FROM Files")
    public void deleteAllFiles();

    @Delete
    public void deleteItem(Files files);
}
