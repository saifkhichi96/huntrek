package com.aspirasoft.huntrek;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aspirasoft.huntrek.bo.CollectiblesManager;
import com.aspirasoft.huntrek.bo.EnvironmentController;
import com.aspirasoft.huntrek.core.HunTrekGame;
import com.aspirasoft.huntrek.core.collectibles.TreasureChest;
import com.aspirasoft.huntrek.utils.Database;
import com.aspirasoft.huntrek.utils.GameTimeListener;
import com.aspirasoft.huntrek.view.GameStartProgressScreen;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

import sfllhkhan95.game.GameStartListener;

public class ActivityHunt extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMarkerClickListener, GameTimeListener {

    private HunTrekGame mGame;

    private Database db = Database.getInstance();

    private TextView chestCountView;
    private TextView scoreView;

    private GameStartProgressScreen mGameStartProgressScreen;

    private EnvironmentController mEnvironmentController;

    private MapView mMapView;
    private MapboxMap mGameMap;
    private MarkerOptions mPlayerMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_hunt);

        // Asynchronously request Google Maps fragment
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Database.init(this);

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_token));

        // Get UI references
        chestCountView = (TextView) findViewById(R.id.chests);
        scoreView = (TextView) findViewById(R.id.score);

        mEnvironmentController = new EnvironmentController(this);

        // Configure loading screen
        mGameStartProgressScreen = (GameStartProgressScreen) findViewById(R.id.loadingScreen);
        mGameStartProgressScreen.start();
    }

    @Override
    public void onMapReady(MapboxMap mMapboxMap) {
        this.mGameMap = mMapboxMap;

        // Stylize map, hide 3D buildings, disable compass and all gestures
        mGameMap.getUiSettings().setAllGesturesEnabled(false);
        mGameMap.getUiSettings().setCompassEnabled(false);
        mGameMap.addPolyline(App.getBoundary());

        // Register click handlers
        mGameMap.setOnMarkerClickListener(this);

        mGame = new HunTrekGame(this);
        mGame.setStartListener(new GameUIThread());

        for (TreasureChest chest : db.getChests()) {
            LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title("Chest #" + String.valueOf(chest.getId()))
                    .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(R.drawable.marker_treasure_chest));

            mGameMap.addMarker(marker);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTitle().startsWith("Chest #")) {
            float distance = distanceBetween(mGame.getPlayer().getPosition(), marker.getPosition());

            if (!mGame.isPaused() && distance <= 50) {
                int id = Integer.valueOf(marker.getTitle().split("#")[1]);
                collectTreasure(id);
            } else if (!mGame.isPaused()) {
                new AlertDialog.Builder(ActivityHunt.this)
                        .setMessage("You must be within 50m of the treasure to collect it. " +
                                "Current distance is " + String.valueOf(distance) + "m.")
                        .create()
                        .show();
            } else {
                startActivity(new Intent(getApplicationContext(), ActivityCollect.class));
            }
            return true;
        } else if (marker.getTitle().equals("You are here!")) {
            marker.showInfoWindow(mGameMap, mMapView);
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void pause() {
        findViewById(R.id.pauseScreen).setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pauseScreen).setVisibility(View.GONE);
            }
        }, 1500);

        for (Marker marker : mGameMap.getMarkers()) {
            mGameMap.removeMarker(marker);
        }

        for (TreasureChest chest : db.getChests()) {
            LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title("Chest #" + String.valueOf(chest.getId()))
                    .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(R.drawable.marker_target));

            mGameMap.addMarker(marker);
        }

        if (mGame.getPlayer().getPosition() != null) {
            mPlayerMarker = new MarkerOptions()
                    .position(mGame.getPlayer().getPosition())
                    .title("You are here!")
                    .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(R.drawable.marker_player));

            mGameMap.addMarker(mPlayerMarker);
        }
        mGame.pause();
    }

    private void resume() {
        findViewById(R.id.pauseScreen).setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pauseScreen).setVisibility(View.GONE);
            }
        }, 2500);

        for (Marker marker : mGameMap.getMarkers()) {
            mGameMap.removeMarker(marker);
        }
        if (mPlayerMarker != null) {
            mGameMap.removeMarker(mPlayerMarker.getMarker());
        }

        for (TreasureChest chest : db.getChests()) {
            LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title("Chest #" + String.valueOf(chest.getId()))
                    .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(R.drawable.marker_treasure_chest));

            mGameMap.addMarker(marker);
        }
        mGame.resume();
    }

    private void end() {
        mGame.end();
    }

    public void toggleOverview(View v) {
        if (!mGame.isPaused()) pause();
        else resume();
    }

    private void collectTreasure(int id) {
        TreasureChest chest = db.popChest(id);
        if (chest != null) {
            Log.i("App/Treasure", "Received " + chest.getValue() + " coins from chest # " + String.valueOf(id));
            db.setScore(db.getScore() + chest.getValue());
        } else {
            Log.e("App/Treasure", "Could not read chest # " + String.valueOf(id));
        }
    }

    private int distanceBetween(LatLng a, LatLng b) {
        float[] distance = new float[1];
        Location.distanceBetween(
                a.getLatitude(), a.getLongitude(),
                b.getLatitude(), b.getLongitude(),
                distance);

        return Math.round(distance[0]);
    }

    @Override
    public void onBackPressed() {
        if (!mGame.isOver() && mGame.isPaused()) {
            resume();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDayBreak() {
        mMapView.setStyleUrl(getString(R.string.mapbox_style_night));
        findViewById(R.id.skybox).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNight));
    }

    @Override
    public void onNightArrived() {
        mMapView.setStyleUrl(getString(R.string.mapbox_style_day));
        findViewById(R.id.skybox).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDawn));
    }


    @UiThread
    private class GameUIThread implements Runnable, GameStartListener {

        @Override
        public void onStart() {
            runOnUiThread(this);
        }

        @Override
        public void run() {
            if (!mGameStartProgressScreen.isFinished()) {
                mGameStartProgressScreen.finish();
            }

            // If there are no treasure chests or a day has passed since last spawn, spawn new chests
            if (db.getChests().size() == 0 || db.checkDayPassedSinceLastSpawn()) {
                new CollectiblesManager().spawnTreasureChests();
                for (TreasureChest chest : db.getChests()) {
                    LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
                    MarkerOptions marker = new MarkerOptions()
                            .position(latLng)
                            .title("Chest #" + String.valueOf(chest.getId()))
                            .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(
                                    mGame.isPaused() ? R.drawable.marker_target : R.drawable.marker_treasure_chest));

                    mGameMap.addMarker(marker);
                }
            }

            // Update UI elements
            findViewById(R.id.game_controls).setVisibility(mGame.isPaused() ? View.GONE : View.VISIBLE);
            findViewById(R.id.skybox).setVisibility(mGame.isPaused() ? View.GONE : View.VISIBLE);

            ((ImageButton) findViewById(R.id.pause_button)).setImageResource(mGame.isPaused() ? R.drawable.button_resume_game : R.drawable.button_hunt_map);

            // Display player's score and chests collected
            scoreView.setText(String.valueOf(db.getScore()));

            List<TreasureChest> chests = db.getChests();
            chestCountView.setText(String.valueOf(CollectiblesManager.MAX_CHESTS - chests.size())
                    + " / " + String.valueOf(CollectiblesManager.MAX_CHESTS));

            // Display correct map
            if (mGameMap != null) {
                if (!mGame.isPaused()) {
                    if (mGame.getPlayer().getPosition() != null) {
                        mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(mGame.getPlayer().getPosition())
                                .zoom(18.5f)
                                .tilt(60.0f)
                                .bearing(mGame.getPlayer().getDirection())
                                .build()));
                    }
                } else {
                    mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(33.645239, 72.991708))
                            .zoom(14.0f)
                            .tilt(0)
                            .build()));
                }
            }

            if (!mGame.isOver()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(GameUIThread.this);
                    }
                }, 100);
            }
        }

    }

}