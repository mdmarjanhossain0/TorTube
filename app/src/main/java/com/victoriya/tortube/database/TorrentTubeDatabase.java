package com.victoriya.tortube.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.victoriya.tortube.model.Files;

@Database(entities=Files.class,version=3)
@TypeConverters(DateConverter.class)
public abstract class TorrentTubeDatabase extends RoomDatabase{
    private static TorrentTubeDatabase instance;
    private static final String DATABASE_NAME="torrenttube.db";

    public abstract TorrentTubeDao dao();

    public static TorrentTubeDatabase getInstance(Context context){
        if(instance==null){
            synchronized(TorrentTubeDatabase.class){
                if (instance == null){
                    instance=Room.databaseBuilder(context.getApplicationContext(),TorrentTubeDatabase.class,DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }
}
