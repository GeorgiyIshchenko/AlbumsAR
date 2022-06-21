package com.google.ar.core.examples.java.helloar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StudentDataBaseHelper extends SQLiteOpenHelper {

    private static final int version = 1;
    private static final String DB_NAME = "Students.db";
    private static final String TABLE_NAME = "Data";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_IMAGE_URL = "image_url";
    private static final String COL_IMAGE_URI = "image_uri";
    private static final String COL_VIDEO_URL = "video_url";
    private static final String COL_YEAR = "year";

    private static StudentDataBaseHelper sInstance;
    
    private static final String TAG = "db_error";

    public StudentDataBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COL_ID + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT," +
                COL_IMAGE_URL + " TEXT," +
                COL_IMAGE_URI + " TEXT," +
                COL_VIDEO_URL + " TEXT," +
                COL_YEAR + " INTEGER" +
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public boolean tableExists(SQLiteDatabase db, String table) {
        boolean result = false;
        String sql = "select count(*) xcount from sqlite_master where type='table' and name='"
                + table + "'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0)
            result = true;
        cursor.close();
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i != i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public static synchronized StudentDataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StudentDataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void addStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(COL_ID, student.getId());
            values.put(COL_NAME, student.getName());
            values.put(COL_IMAGE_URL, student.getImageUrl());
            values.put(COL_IMAGE_URI, student.getImageUri().toString());
            values.put(COL_VIDEO_URL, student.getVideoUrl());
            values.put(COL_YEAR, student.getYear());

            db.insertOrThrow(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add student to database");
        } finally {
            db.endTransaction();
        }
    }

    public int updateStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, student.getName());
        values.put(COL_IMAGE_URL, student.getImageUrl());
        values.put(COL_IMAGE_URI, student.getImageUri().toString());
        values.put(COL_VIDEO_URL, student.getVideoUrl());
        values.put(COL_YEAR, student.getYear());

        return db.update(TABLE_NAME, values, COL_ID + " = ?",
                new String[] { String.valueOf(student.getId()) });
    }


    @SuppressLint("Range")
    public List<Student> getStudentList(){
        List<Student> students = new ArrayList<>();

        String STUDENTS_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_NAME);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(STUDENTS_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    Student newStudent = new Student();
                    newStudent.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                    newStudent.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                    newStudent.setImageUrl(cursor.getString(cursor.getColumnIndex(COL_IMAGE_URL)));
                    newStudent.setImageUri(Uri.parse(cursor.getString(cursor.getColumnIndex(COL_IMAGE_URI))));
                    newStudent.setVideoUrl(cursor.getString(cursor.getColumnIndex(COL_VIDEO_URL)));
                    newStudent.setYear(cursor.getInt(cursor.getColumnIndex(COL_YEAR)));
                    students.add(newStudent);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "Error while trying to get students from database");
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return students;
    }

    public void deleteAllStudents() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all students");
        } finally {
            db.endTransaction();
        }
    }

}
