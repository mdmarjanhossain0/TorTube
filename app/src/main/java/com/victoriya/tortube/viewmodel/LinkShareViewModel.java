package com.victoriya.tortube.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LinkShareViewModel extends ViewModel{

    private static final String TAG=LinkShareViewModel.class.getSimpleName();


    public MutableLiveData<String> shareMagnetLink=new MutableLiveData<>();

    public LiveData<String> getMagnetLink(){
        return shareMagnetLink;
    }

    public void setMagnetLink(String magnetLink){
        Log.d(TAG,magnetLink.toString());
        this.shareMagnetLink.setValue(magnetLink);
    }
}
