package com.aspirasoft.huntrek.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.UserManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class ActivitySplash extends Activity {

    private static final int LOCATION_REQUEST_CODE = 200;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FirebaseAuth mAuth;
    private GameLaunchTask mGameLaunchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        HuntItApp.PREFS_FILE = getSharedPreferences(HuntItApp.PREFS_FILENAME, MODE_PRIVATE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        mGameLaunchTask = new GameLaunchTask();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGameLaunchTask.startDelayed();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGameLaunchTask.startDelayed();
            } else {
                finish();
            }
        }
    }

    private enum AuthStatus {
        SIGNED_IN,
        SIGNED_OUT,
        SIGNUP_INCOMPLETE
    }

    private class GameLaunchTask extends TimerTask {

        private final Timer mTimer = new Timer();

        private GameLaunchTask() {
        }

        private void proceed(AuthStatus mAuthStatus) {
            switch (mAuthStatus) {
                case SIGNED_IN:
                    startActivity(new Intent(getApplicationContext(), ActivityHunt.class));
                    finish();
                    overridePendingTransition(0, 0);
                    break;
                case SIGNED_OUT:
                    startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
                    finish();
                    overridePendingTransition(0, 0);
                    break;
                case SIGNUP_INCOMPLETE:
                    startActivity(new Intent(getApplicationContext(), ActivitySignUp.class));
                    finish();
                    overridePendingTransition(0, 0);
                    break;
            }
        }

        @Override
        public void run() {
            UserManager manager = UserManager.getInstance();
            if (manager.isSignedIn()) {
                proceed(AuthStatus.SIGNED_IN);
            } else {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    proceed(AuthStatus.SIGNED_OUT);
                } else {
                    proceed(AuthStatus.SIGNUP_INCOMPLETE);
                }
            }
        }

        void startDelayed() {
            mTimer.schedule(this, 1500L);
        }

    }

}