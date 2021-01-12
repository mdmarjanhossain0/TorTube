package com.victoriya.tortube.ui.player;

//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ProgressBar;
//
//import com.github.se_bastiaan.torrentstream.StreamStatus;
//import com.github.se_bastiaan.torrentstream.Torrent;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.MergingMediaSource;
//import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;
//import com.victoriya.tortube.R;
//import com.victoriya.tortube.viewmodel.UpdateShareViewModel;

import androidx.fragment.app.Fragment;

public class PlayerFragment extends Fragment {
//
//    private Context streamingContext;
//    private Button externalPlayer;
//    private SimpleExoPlayer simplePlayer;
//    private MergingMediaSource mergingMediaSource;
//    private ExtractorMediaSource mediaSource;
//    private PlayerView moviePlayer;
//    private ProgressBar streamProgress;
//    private UpdateShareViewModel updateShareViewModel;
//    private static final String TAG=PlayerFragment.class.getSimpleName();
//    private PlayerFragmentArgs args;
//    private String streamingUrl;
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        updateShareViewModel=new ViewModelProvider(getActivity()).get(UpdateShareViewModel.class);
//        args=PlayerFragmentArgs.fromBundle(getArguments());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_player, container, false);
//        streamingContext=view.getContext();
//        moviePlayer=view.findViewById(R.id.exoplayerView);
//        externalPlayer=view.findViewById(R.id.external_player);
//        /*moviePlayer=view.findViewById(R.id.moviePlayer);
//        streamProgress=view.findViewById(R.id.streamProgress);*/
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        streamingUrl=args.getStreamingUrl();
////        initPlayers(streamingUrl);
//        initPlayer(streamingUrl);
//        subscribeObserver();
//        externalPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity)getActivity()).externalPlayer(streamingUrl);
//            }
//        });
//    }
//
//    private void initPlayer(String streamingUrl) {
//        simplePlayer=ExoPlayerFactory.newSimpleInstance(getContext());
//
//        moviePlayer.setPlayer(simplePlayer);
//        DataSource.Factory dataSourceFactory=new DefaultDataSourceFactory(getContext(),
//                Util.getUserAgent(getContext(), "TorTube"));
//        MediaSource mediaSource=new ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(streamingUrl));
//        simplePlayer.prepare(mediaSource);
//        simplePlayer.setPlayWhenReady(true);
//    }
//
//    /*private void initPlayers(String path){
//        DefaultLoadControl loadControl=new DefaultLoadControl();
//        BandwidthMeter bandwithMeter=new DefaultBandwidthMeter();
//        TrackSelector trackSelection=new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
//        simplePlayer= ExoPlayerFactory.newSimpleInstance(streamingContext, trackSelection,loadControl);
//        DefaultHttpDataSourceFactory dataSourceFactory=new DefaultHttpDataSourceFactory("exoplayer_video");
//        DefaultExtractorsFactory extratorsFactory=new DefaultExtractorsFactory();
//        MediaSource mediaSource=new ExtractorMediaSource(Uri.parse(path),dataSourceFactory,extratorsFactory,null,null);
//        moviePlayer.setPlayer(simplePlayer);
//        moviePlayer.setKeepScreenOn(true);
//        simplePlayer.prepare(mediaSource);
//        simplePlayer.setPlayWhenReady(true);
//        streamProgress.setVisibility(View.GONE);
//        simplePlayer.addListener(this);
//    }*/
//
//
//    private void subscribeObserver(){
//
//        updateShareViewModel.getState().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                Log.d(TAG,"observe "+s.toString());
//            }
//        });
//
//        updateShareViewModel.getProgressStatus().observe(getViewLifecycleOwner(), new Observer<StreamStatus>() {
//            @Override
//            public void onChanged(StreamStatus progress) {
//                Log.d(TAG,"observe "+ progress.toString());
//            }
//        });
//
//        updateShareViewModel.getTorrentFile().observe(getViewLifecycleOwner(), new Observer<Torrent>() {
//            @Override
//            public void onChanged(Torrent fileName) {
//                Log.d(TAG,"observe "+fileName.toString());
//            }
//        });
//
//        updateShareViewModel.getError().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                Log.d(TAG,"observe "+s.toString());
//            }
//        });
//
//        updateShareViewModel.getStreamingUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String url) {
//            }
//        });
//    }
}