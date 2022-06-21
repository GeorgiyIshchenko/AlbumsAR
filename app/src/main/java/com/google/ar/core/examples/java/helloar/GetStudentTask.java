package com.google.ar.core.examples.java.helloar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetStudentTask extends AsyncTask<Void, Void, String> {

    private String message;
    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;
    private int id;

    public GetStudentTask(MainActivity mainActivity, int id) {
        super();
        this.mainActivity = mainActivity;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = MainActivity.API_URL + id;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(new URL(url)).build();
            Response response = client.newCall(request).execute();
            String requestString = Objects.requireNonNull(response.body()).string();
            Log.d("student_object", requestString);
            return requestString;
        } catch (IOException e) {
            e.printStackTrace();
            message = "Ошибка подключения к серверу";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);

            Student student = new Student();
            student.setId(jsonObject.getInt("id"));
            student.setName(jsonObject.getString("name"));
            student.setImageUrl(jsonObject.getString("image"));
            student.setVideoUrl(jsonObject.getString("video"));
            student.setYear(jsonObject.getInt("year"));

            Log.d("student_object", student.toString());
            MainActivity.currentStudent = student;
        } catch (Exception e) {
            e.printStackTrace();
            if (s != null) message = "Ошибка чтения данных";
        }
        if (message != null) {
            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

}
