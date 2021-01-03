package com.victoriya.tortube.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.victoriya.tortube.service.StreamingHandler;

public class UpdateShareViewModel extends ViewModel{

    private static final String TAG=UpdateShareViewModel.class.getSimpleName();

    /*private MutableLiveData<Torrent> torrentFile =new MutableLiveData<>();
    private MutableLiveData<StreamStatus> progressStatus =new MutableLiveData<>();
    private MutableLiveData<String> streamingUrl=new MutableLiveData<>();
    private MutableLiveData<String> state =new MutableLiveData<>();
    private MutableLiveData<String> error=new MutableLiveData<>();*/


    private MutableLiveData<Torrent> torrentFile;
    private MutableLiveData<StreamStatus> progressStatus;
    private MutableLiveData<String> streamingUrl;
    private MutableLiveData<String> state;
    private MutableLiveData<String> error;

    public UpdateShareViewModel(){
        StreamingHandler streamingHandler = StreamingHandler.getInstance();
        torrentFile= streamingHandler.torrentFile;
        progressStatus= streamingHandler.progressStatus;
        streamingUrl= streamingHandler.streamingUrl;
        state= streamingHandler.state;
        error= streamingHandler.error;
    }



    public LiveData<Torrent> getTorrentFile() {
        return torrentFile;
    }

    public void setTorrentFile(Torrent torrentFile) {
        this.torrentFile.setValue(torrentFile);
    }

    public LiveData<StreamStatus> getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(StreamStatus progressStatus) {
        this.progressStatus.setValue(progressStatus);
    }

    public LiveData<String> getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl.setValue(streamingUrl);
    }

    public LiveData<String> getState() {
        return state;
    }

    public void setState(String state) {
        this.state.setValue(state);
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setError(String error) {
        this.error.setValue(error);
    }
}
