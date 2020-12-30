package com.victoriya.tortube.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName="Files")
public class Files{

    private String fileName;
    private Long fileSize;
    private Date date;


    @PrimaryKey
    @NonNull
    private String magnetLink;

    @Ignore
    public Files() {
    }

    public Files(String fileName,String magnetLink,Long fileSize,Date date) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.magnetLink = magnetLink;
        this.date=date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMagnetLink() {
        return magnetLink;
    }

    public void setMagnetLink(String magnetLink) {
        this.magnetLink = magnetLink;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Files{" +
                "fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", date=" + date +
                ", magnetLink='" + magnetLink + '\'' +
                '}';
    }
}
