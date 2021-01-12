package com.victoriya.tortube.model;

public class SelectionFile{
    private String name;
    private long size;

    public SelectionFile() {
    }

    public SelectionFile(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SelectionFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
