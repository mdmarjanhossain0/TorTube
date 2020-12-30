package com.victoriya.tortube.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.victoriya.tortube.R;
import com.victoriya.tortube.database.TorrentTubeDatabase;
import com.victoriya.tortube.service.ServiceHandler;
import com.victoriya.tortube.service.StreamingService;
import com.victoriya.tortube.ui.player.PlayerActivity;
import com.victoriya.tortube.ui.settings.SettingsActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity{

    private static final String TAG=MainActivity.class.getSimpleName();
    public static final String MAGNET_LINK="magnet_link";
    public static final String START_PLAYER_ACTIVITY="start_player_activity";
    public String streamingUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String action = getIntent().getAction();
        checkAction(action);
    }

    private void checkAction(String action) {
        Uri data = getIntent().getData();
        if (action != null && action.equals(Intent.ACTION_VIEW) && data != null) {
            try {
                String streamUrl = URLDecoder.decode(data.toString(), "utf-8");
                this.streamingUrl=streamUrl;
                getIntent().setData(null);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.settings_menu:{
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.menu_stop_streaming:{
                serviceStopper();
                break;
            }
            case R.id.menu_clear:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TorrentTubeDatabase.getInstance(MainActivity.this).dao().deleteAllFiles();
                    }
                }).start();
                break;
            }
            case R.id.menu_test:{
                externalPlayer("http://192.168.43.204:2000/video.mp4");
                break;
            }
        }
        return false;
    }


    public void serviceStarter(String magnetLink){

        ServiceHandler serviceHandler=ServiceHandler.getInstance(this);

        Log.d(TAG,"mainactivity service start");
        Intent intent=new Intent(this,StreamingService.class);
        intent.putExtra(MAGNET_LINK,magnetLink);
        startService(intent);
    }

    public void serviceStopper(){
        stopService(new Intent(this,StreamingService.class));
    }

    public void externalPlayer(String streamingLink){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(streamingLink), "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent,"Select Player"));
    }

    public void internalPlayer(String streamingLink){
        Intent intent=new Intent(this, PlayerActivity.class);
        intent.putExtra(START_PLAYER_ACTIVITY,streamingLink);
        startActivity(intent);
    }
}