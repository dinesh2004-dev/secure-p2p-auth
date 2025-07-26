package com.dinesh.auth.anonauthbackend.dtos;

public class FileItem {
    private String name;
    private long size;        // Size in bytes
    private String lastModified; // ISO 8601 format: "2024-07-24T10:30:00Z"

    // Constructors, getters, setters
    public FileItem(String name, long size, String lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
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
    public String getLastModified() {
        return lastModified;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

}