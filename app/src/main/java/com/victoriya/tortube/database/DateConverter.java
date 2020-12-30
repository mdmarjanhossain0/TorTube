package com.victoriya.tortube.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Long toTimeStamp(Date date){
        return date==null?null:date.getTime();
    }
    @TypeConverter
    public static Date toDate(Long toTimeStamp){
        return toTimeStamp==null?null:new Date(toTimeStamp);
    }
}
