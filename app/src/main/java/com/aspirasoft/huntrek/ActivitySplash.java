package com.aspirasoft.huntrek;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.aspirasoft.huntrek.utils.Database;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ActivitySplash extends Activity {

    private static final int LOCATION_REQUEST_CODE = 200;
    ArrayList<String> locationPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_splash);
        Database.init(this);

        locationPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        locationPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, locationPermissions.toArray(new String[]{}), LOCATION_REQUEST_CODE);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), ActivityHunt.class));
                    finish();
                }
            }, 1500L);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), ActivityHunt.class));
                        finish();
                    }
                }, 1500L);
            } else {
                finish();
            }
        }
    }
}