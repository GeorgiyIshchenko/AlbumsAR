package com.google.ar.core.examples.java.helloar;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetStudentListTask extends AsyncTask<Void, Void, ArrayList<Student>> {

    private String message;
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;

    public GetStudentListTask(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    public String getMessage() {
        return message;
    }

    @SuppressLint("WrongThread")
    @Override
    protected ArrayList<Student> doInBackground(Void... voids) {
        String url = MainActivity.API_URL;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(new URL(url)).build();
            Response response = client.newCall(request).execute();
            String requestString = Objects.requireNonNull(response.body()).string();
            Log.d("student_list", requestString);

            ArrayList<Student> students = deserializeStudentsJSON(requestString);

            for (Student student : students) {
                loadPhoto(student, students.size());
            }

            return students;
        } catch (IOException e) {
            e.printStackTrace();
            message = "Ошибка подключения к серверу";
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(ArrayList<Student> students) {
        super.onPostExecute(students);
        mainActivity.updateDB(students);
        if (message != null) {
            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Student> deserializeStudentsJSON(String s) {
        ArrayList<Student> students = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Student student = new Student();
                student.setId(jsonObject.getInt("id"));
                student.setName(jsonObject.getString("name"));
                student.setImageUrl(MainActivity.BASE_URL + jsonObject.getString("image"));
                student.setVideoUrl(MainActivity.BASE_URL + jsonObject.getString("video"));
                student.setYear(jsonObject.getInt("year"));

                //Save image
                //FileOutputStream out = new FileOutputStream();

                Log.d("student_list", student.toString());
                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (s != null) message = "Ошибка чтения данных";
        }
        return students;
    }

    private void loadPhoto(Student student, int size) {
        String url = student.getImageUrl();
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("student_load_photo", e.getMessage());
        }

        mainActivity.updateProgressBar(1, size);

        Log.d("media_url", student.getImageUrl());

        String root = Environment.getExternalStorageDirectory().toString();
        File albumsDir = new File(root + "/AlbumsAR");
        albumsDir.mkdirs();

        File file = new File(albumsDir, String.format("%s%s.jpg", student.getName(), student.getId()));
        if (file.exists()) {
            student.setImageUri(Uri.parse(file.toString()));
        }
        else{
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                student.setImageUri(Uri.parse(file.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}