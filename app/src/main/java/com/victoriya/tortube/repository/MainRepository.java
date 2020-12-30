package com.victoriya.tortube.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.victoriya.tortube.database.TorrentTubeDatabase;
import com.victoriya.tortube.model.Files;

import java.util.List;

public class MainRepository{

    private static MainRepository instance;
    private TorrentTubeDatabase database;
    public LiveData<List<Files>> filesLiveData;

    private MainRepository(Context context){
        database=TorrentTubeDatabase.getInstance(context);
        filesLiveData=getAllFiles();
    }


    public static MainRepository getInstance(Context context){
        if(instance==null){
            instance=new MainRepository(context);
        }
        return instance;
    }


    public LiveData<List<Files>> getAllFiles(){
        return database.dao().getAllFiles();
    }


    public LiveData<Files> getMagnetLink(String magnetLink){
        return database.dao().getMagnetLink(magnetLink);
    }

    public void deleteFile(Files file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.dao().deleteItem(file);
            }
        }).start();
    }

    public void deleteAllFiles(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.dao().deleteAllFiles();
            }
        }).start();
    }
}
