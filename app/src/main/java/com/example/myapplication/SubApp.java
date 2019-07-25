package com.example.myapplication;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class SubApp extends Application {
    SQLiteDatabase commentDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        commentDatabase = openOrCreateDatabase("comment", MODE_PRIVATE, null);
        commentDatabase.execSQL("drop table COMMENT");
        commentDatabase.execSQL("CREATE TABLE  if not exists COMMENT(postid String, id String, name String, email String, body String);");

    }
}
