package com.victoriya.tortube.ui.main.checking;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.frostwire.jlibtorrent.FileStorage;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.victoriya.tortube.Adapter.SelectionAdapter;
import com.victoriya.tortube.R;
import com.victoriya.tortube.database.TorrentTubeDatabase;
import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.model.SelectionFile;
import com.victoriya.tortube.service.StreamingHandler;
import com.victoriya.tortube.ui.main.MainActivity;
import com.victoriya.tortube.viewmodel.UpdateShareViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CheckingDialogFragment extends DialogFragment {

    private static final String TAG=CheckingDialogFragment.class.getSimpleName();

//    private LinkShareViewModel shareViewModel;
    private UpdateShareViewModel updateShareViewModel;

    private View view;
    private ProgressBar waitingProgressBar,progressBar;
    private TextView fileSize, downloadSpeed;
    private AlertDialog.Builder alertDialog;
    private RecyclerView recyclerView;
    private SelectionAdapter adapter;
    TextView title;


    private StreamingHandler streamingHandler;
    private TorrentTubeDatabase database;
    private String magnet;

    private Torrent startStreamingTorrent;


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
        initRecyclerView();
        subscribeObserver();


        LinearLayout titleLinearLayout = customDialogTitle();
        alertDialog.setCustomTitle(titleLinearLayout);



        alertDialog.setView(view)
//                .setTitle("Checking Magnet Link...")
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

    private LinearLayout customDialogTitle() {
        LinearLayout titleLinearLayout = new LinearLayout(getContext());

        titleLinearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 25));
        titleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        title = new TextView(getContext());
        title.setText(R.string.select_a_font);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        title.setPadding(40, 40, 40, 40);
        title.setGravity(3);

        titleLinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_cyan));
        titleLinearLayout.addView(title);
        titleLinearLayout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        return titleLinearLayout;
    }

    private void initRecyclerView() {
        adapter=new SelectionAdapter(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void initView() {
        waitingProgressBar=view.findViewById(R.id.waiting_progress);
        progressBar=view.findViewById(R.id.progress_bar);
        fileSize=view.findViewById(R.id.file_size);
        downloadSpeed =view.findViewById(R.id.file_seeding);
        recyclerView=view.findViewById(R.id.selection_recycler_view);
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


                    /*if(status.equals(StreamingHandler.ON_STREAM_PREPARED)){
                        startStreamingTorrent.startDownload();
                    }*/
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
                    title.setText("Fetching Buffer... "+streamStatus.bufferProgress+"%");
                    Log.d(TAG,"observe "+ streamStatus.toString());
                }
            }
        });

        updateShareViewModel.getTorrentFile().observe(this, new Observer<Torrent>() {
            @Override
            public void onChanged(Torrent torrent) {

                if(torrent!=null){


                    startStreamingTorrent=torrent;

                    title.setText("Select a File For Streaming");
                    waitingProgressBar.setVisibility(View.GONE);

                    String makeMagnetUri = torrent.getTorrentHandle().torrentFile().makeMagnetUri();
                    Log.d(TAG,makeMagnetUri.toString());
                    String fileName=torrent.getVideoFile().getName();
                    Long videoFileSize=torrent.getTorrentHandle().torrentFile().totalSize();

                    insertDatabase(fileName,makeMagnetUri,videoFileSize);
                    getDialog().setTitle(fileName.toString());
                    fileSize.setText("File Size: "+videoFileSize/(1024*1024)+" MB");



                    FileStorage fileStorage=torrent.getTorrentHandle().torrentFile().files();
                    List<SelectionFile> selectionFiles=new ArrayList<>();
                    for(int i=0;i<fileStorage.numFiles();i++){
                        selectionFiles.add(new SelectionFile(fileStorage.fileName(i),fileStorage.fileSize(i)));
                        Log.d(TAG,fileStorage.fileName(i));
                    }
                    adapter.submitList(selectionFiles);

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


    public void onIndexSelectionEvent(int index){
        startStreamingTorrent.setSelectedFileIndex(index);
        startStreamingTorrent.startDownload();
        waitingProgressBar.setVisibility(View.VISIBLE);
        title.setText("Please Wait...");
    }


    @Override
    public void onDestroy() {
        Log.d(TAG,"Checking Dialog destroy");
        super.onDestroy();
    }
}