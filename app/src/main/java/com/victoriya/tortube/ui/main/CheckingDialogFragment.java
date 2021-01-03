package com.victoriya.tortube.ui.main;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.victoriya.tortube.R;
import com.victoriya.tortube.database.TorrentTubeDatabase;
import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.service.StreamingHandler;
import com.victoriya.tortube.viewmodel.UpdateShareViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CheckingDialogFragment extends DialogFragment {

    private static final String TAG=CheckingDialogFragment.class.getSimpleName();

//    private LinkShareViewModel shareViewModel;
    private UpdateShareViewModel updateShareViewModel;

    private View view;
    private ProgressBar waitingProgressBar,progressBar;
    private TextView fileSize, downloadSpeed;
    AlertDialog.Builder alertDialog;


    private StreamingHandler streamingHandler;
    private TorrentTubeDatabase database;
    private String magnet;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        streamingHandler = StreamingHandler.getInstance();
//        shareViewModel=new ViewModelProvider(getActivity()).get(LinkShareViewModel.class);
        updateShareViewModel=new ViewModelProvider((MainActivity)getActivity()).get(UpdateShareViewModel.class);
        Log.d(TAG,"check viewmodel object"+updateShareViewModel.hashCode());
        database=TorrentTubeDatabase.getInstance(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view=getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog,null);

        alertDialog=new AlertDialog.Builder(getContext());
        this.view=view;
        initView();
        subscribeObserver();

        alertDialog.setView(view)
                .setTitle("Checking Magnet Link...")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((MainActivity) getActivity()).serviceStopper();
                        updateShareViewModel.getTorrentFile().removeObservers(getActivity());
                        dismiss();
                    }
                })
                .setNegativeButtonIcon(getContext().getDrawable(R.drawable.ic_close))
                .setCancelable(false);

        AlertDialog create = alertDialog.create();
        create.setCanceledOnTouchOutside(false);
        create.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return create;
    }

    private void initView() {
        waitingProgressBar=view.findViewById(R.id.waiting_progress);
        progressBar=view.findViewById(R.id.progress_bar);
        fileSize=view.findViewById(R.id.file_size);
        downloadSpeed =view.findViewById(R.id.file_seeding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void subscribeObserver(){
        /*shareViewModel.shareMagnetLink.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String magnetLink) {
                magnet=magnetLink;
                Log.d(TAG,"passing data"+magnetLink.toString());
            }
        });*/


        updateShareViewModel.getState().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if(status!=null){
                    Log.d(TAG,"observe "+status.toString());
                }
            }
        });

        updateShareViewModel.getProgressStatus().observe(this, new Observer<StreamStatus>() {
            @Override
            public void onChanged(StreamStatus streamStatus) {
                if(streamStatus!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(streamStatus.bufferProgress);
                    waitingProgressBar.setVisibility(View.GONE);

                    downloadSpeed.setText("Streaming Speed: "+(streamStatus.downloadSpeed/(1024*1024))+" kiB");
                    Log.d(TAG,"observe "+ streamStatus.toString());
                }
            }
        });

        updateShareViewModel.getTorrentFile().observe(this, new Observer<Torrent>() {
            @Override
            public void onChanged(Torrent torrent) {

                if(torrent!=null){

                    String makeMagnetUri = torrent.getTorrentHandle().torrentFile().makeMagnetUri();
                    Log.d(TAG,makeMagnetUri.toString());
                    String fileName=torrent.getVideoFile().getName();
                    Long videoFileSize=torrent.getTorrentHandle().torrentFile().totalSize();

                    insertDatabase(fileName,makeMagnetUri,videoFileSize);
                    getDialog().setTitle(fileName.toString());
                    fileSize.setText("File Size: "+videoFileSize/(1024*1024)+" MB");

                    Log.d(TAG,"observe "+fileName.toString());
                }
            }
        });

        updateShareViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {

                if(error!=null){

                    updateShareViewModel.setError(null);
                    ((MainActivity)getActivity()).serviceStopper();
                    Toast.makeText(getContext(),"!Error..."+error+" | Network Problem",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"observe "+error.toString());
                    dismiss();
                }
            }
        });

        updateShareViewModel.getStreamingUrl().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String url) {
                if(url!=null){

                    ((MainActivity)getActivity()).externalPlayer(url);
                }
            }
        });
    }

    private void insertDatabase(String fileName,String magnetUri,long fileSize) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.dao().insertMagnetLink(new Files(fileName,magnetUri,(Long)(fileSize/(1024*1024)),getDate()));
            }
        }).start();
    }

    public static Date getDate(){
        GregorianCalendar calendar=new GregorianCalendar();
        calendar.add(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG,"Checking Dialog destroy");
        super.onDestroy();
    }
}