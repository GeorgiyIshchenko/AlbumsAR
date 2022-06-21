package com.google.ar.core.examples.java.helloar;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Formatter;

public class Student {

    private long id;
    private String name;
    private String imageUrl;
    private Uri imageUri;
    private String videoUrl;
    private int year;

    public Student(){

    }

    public Student(long id, String name, String imageUrl, Uri imageUri, String videoUrl, int year) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.imageUri = imageUri;
        this.videoUrl = videoUrl;
        this.year = year;
    }

    @NonNull
    @Override
    public String toString() {
        return new Formatter().format("Id: %d | Name: %s | ImageUrl: %s | ImageUri: %s | VideoUrl: %s | Year: %d",
                id, name, imageUrl, imageUri, videoUrl, year).toString();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
