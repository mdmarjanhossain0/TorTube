package com.victoriya.tortube.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victoriya.tortube.Adapter.TorrentAdapter;
import com.victoriya.tortube.R;
import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.viewmodel.MainViewModel;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String TAG=MainFragment.class.getSimpleName();
    private static final int PICK_TORRENT_FILE=2;

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottomEdit;
    private Animation fromBottomAddTorrent;
    private Animation toBottomEdit;
    private Animation toBottomAddTorrent;

    private FloatingActionButton fabAdd, fabEdit,fabAddTorrent;
    private RecyclerView mainRecyclerView;
    private AdView mAdView;

    private MainViewModel mainViewModel;
//    private LinkShareViewModel shareViewModel;

    private boolean clicked=false;
    private TorrentAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rotateOpen= AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        rotateClose= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close_anim);
        fromBottomEdit =AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_edit);
        fromBottomAddTorrent=AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_add_torrent);
        toBottomEdit =AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_edit);
        toBottomAddTorrent =AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_add_torrent);

        mainViewModel=new ViewModelProvider(getActivity()).get(MainViewModel.class);
//        shareViewModel=new ViewModelProvider(getActivity()).get(LinkShareViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fabAdd =view.findViewById(R.id.bnt_add);
        fabEdit =view.findViewById(R.id.bnt_edit);
        fabAddTorrent=view.findViewById(R.id.bnt_add_torrent);
        mainRecyclerView=view.findViewById(R.id.main_recycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();

        subscribeObserver();

        checkIntent();

        setListener();


        mAdView = view.findViewById(R.id.adView);
        initAds();
    }

    private void setListener() {

        fabAdd.setOnClickListener(v -> {
            onAddEvent();
        });

        fabEdit.setOnClickListener(v->{
            onEditEvent();
        });

        fabAddTorrent.setOnClickListener(view -> {
            onAddTorrentEvent();
        });
    }

    private void initAds() {

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG,"init Ad"+initializationStatus.getAdapterStatusMap().toString());
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG,"on Ad Loaded");
            }


            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG,"on Ad Opened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG,"on Ad Clicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG,"on Ad Left Application");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG,"on Ad Close");
            }
        });
    }

    private void checkIntent() {

        if(((MainActivity)getActivity()).streamingUrl!=null){
            navigateCheckingFragment(null);
        }
    }

    private void navigateCheckingFragment(String url) {

//        shareViewModel.setMagnetLink(((MainActivity)getActivity()).streamingUrl);

        if(url==null){
            startStreamingEvent(((MainActivity)getActivity()).streamingUrl);
        }
        else {
            startStreamingEvent(url);
        }

        NavDirections navDirections= MainFragmentDirections.actionMainFragmentToCheckingDialogFragment();
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(navDirections);
    }

    private void initRecyclerView() {

        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainRecyclerView.hasFixedSize();

        adapter=new TorrentAdapter(this);
        mainRecyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                swipeDelete(adapter.getTorrentItem(viewHolder.getAdapterPosition()));
            }
        });
        itemTouchHelper.attachToRecyclerView(mainRecyclerView);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        mainRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void swipeDelete(Files file) {
        mainViewModel.deleteTorrentItem(file);
    }

    private void subscribeObserver() {

        mainViewModel.getAllFiles().observe(getViewLifecycleOwner(), new Observer<List<Files>>() {
            @Override
            public void onChanged(List<Files> files) {

                adapter.submitList(files);
                if(files!=null&&files.size()>0){
//                    adapter.submitList(files);
                    Log.d(TAG,files.get(0).toString());
                }
            }
        });
    }

    private void onAddEvent() {

        setVisibility(clicked);
//        setAnimation(clicked);
        clicked=!clicked;
    }

    private void onEditEvent() {

        NavDirections action = MainFragmentDirections.actionMainFragmentToInputDialogFragment();
        Navigation
                .findNavController(getActivity(),R.id.nav_host_fragment)
                .navigate(action);
    }

    private void onAddTorrentEvent() {

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("application/x-bittorrent");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICK_TORRENT_FILE
        );
    }


    private void setAnimation(Boolean clicked) {

        if(!clicked){
            fabAdd.startAnimation(rotateOpen);
            fabEdit.startAnimation(fromBottomEdit);
            fabAddTorrent.startAnimation(fromBottomAddTorrent);
        }
        else{
            fabAdd.startAnimation(rotateClose);
            fabEdit.startAnimation(toBottomEdit);
            fabAddTorrent.startAnimation(toBottomAddTorrent);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setVisibility(Boolean clicked) {

        if(!clicked){
            fabEdit.setVisibility(View.VISIBLE);
            fabAddTorrent.setVisibility(View.VISIBLE);
            setAnimation(clicked);
        }
        else{
            setAnimation(clicked);
            fabEdit.setVisibility(View.INVISIBLE);
            fabAddTorrent.setVisibility(View.INVISIBLE);
        }
    }

    public void adapterClickEvent(String magnetLink) {

        NavDirections navDirections= MainFragmentDirections.actionMainFragmentToCheckingDialogFragment();
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(navDirections);
//        shareViewModel.setMagnetLink(magnetLink);
        startStreamingEvent(magnetLink);
    }

    private void startStreamingEvent(String magnetLink){
        ((MainActivity)getActivity()).serviceStarter(magnetLink);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==PICK_TORRENT_FILE && resultCode== Activity.RESULT_OK){

            Uri uri=data.getData();
            if(uri!=null){
                navigateCheckingFragment(uri.toString());
            }
        }
    }
}