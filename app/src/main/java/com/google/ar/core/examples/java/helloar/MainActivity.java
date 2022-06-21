package com.google.ar.core.examples.java.helloar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // API

    public static final String BASE_URL = "http://192.168.0.107:8000";
    public static final String API_URL = BASE_URL + "/api/students/";

    // DATA

    static public Student currentStudent;

    public int progress = 0;

    // DataBase

    StudentDataBaseHelper dataBaseHelper;

    // Interface

    ProgressBar progressBar;

    // Permissions

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(progress);

        // Permissions
        CheckPermissions();

        httpRequests();

        // DataBaseHelper

    }

    public void updateProgressBar(int value, int size) {
        progress += value;
        progressBar.setProgress(progress * 100 / size);
        progressBar.setSecondaryProgress(progress * 100 / size);
    }

    private void httpRequests() {
        GetStudentListTask task = new GetStudentListTask(this);
        task.execute();
    }

    public void updateDB(ArrayList<Student> students) {
        dataBaseHelper = StudentDataBaseHelper.getInstance(this);
        dataBaseHelper.deleteAllStudents();
        Log.d("add_to_db", String.valueOf(students.size()));
        for (Student student : students) {
            dataBaseHelper.addStudent(student);
            Log.d("add_to_db", student.toString());
        }
        for (Student student : dataBaseHelper.getStudentList()) {
            Log.d("added_to_db", student.toString());
        }
        startActivity(new Intent(this, ARActivity.class));

    }

    private void CheckPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Start loading photos
                    httpRequests();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}