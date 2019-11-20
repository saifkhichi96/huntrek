package com.aspirasoft.huntrek;

import android.app.Application;
import android.content.SharedPreferences;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

public class HuntItApp extends Application {

    public static final String TAG = "HunTrek";
    public static final String PREFS_FILENAME = "HUNTREK_DB";
    public static SharedPreferences PREFS_FILE;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);

        PREFS_FILE = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE);
    }

}