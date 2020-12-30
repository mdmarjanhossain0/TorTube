package com.victoriya.tortube.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.repository.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel{


    private MainRepository mainRepository;
    private LiveData<List<Files>> fileLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository=MainRepository.getInstance(application.getApplicationContext());
        fileLiveData=mainRepository.filesLiveData;
    }


    public LiveData<List<Files>> getAllFiles(){
        return fileLiveData;
    }

    public void deleteTorrentItem(Files file) {
        mainRepository.deleteFile(file);
    }
}
