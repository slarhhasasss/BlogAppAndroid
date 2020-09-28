package ru.kolesnikovdmitry.blogapp;

import android.app.Application;

import androidx.room.Room;

import ru.kolesnikovdmitry.blogapp.db.AccountsDB;
import ru.kolesnikovdmitry.blogapp.db.PostsDB;

public class MyApp extends Application {

    public static AccountsDB accountsDB = null;
    public static String CUR_USER = "";
    public static PostsDB postsDB = null;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.accountsDB = Room.databaseBuilder(this, AccountsDB.class, "AccountsDatabase")
                .allowMainThreadQueries()
                .build();
        MyApp.postsDB = Room.databaseBuilder(this, PostsDB.class, "PostsDatabase")
                .allowMainThreadQueries()
                .build();
    }
}
