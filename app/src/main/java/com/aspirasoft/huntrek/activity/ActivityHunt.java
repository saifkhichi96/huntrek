package com.aspirasoft.huntrek.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.CollectibleManager;
import com.aspirasoft.huntrek.bo.UserManager;
import com.aspirasoft.huntrek.core.HuntItGame;
import com.aspirasoft.huntrek.listener.GameTimeListener;
import com.aspirasoft.huntrek.model.User;
import com.aspirasoft.huntrek.model.collectibles.TreasureChest;
import com.aspirasoft.huntrek.utils.DatabaseUtils;
import com.aspirasoft.huntrek.view.AvatarView;
import com.aspirasoft.huntrek.view.DialogGameMenu;
import com.aspirasoft.huntrek.view.DialogUserDetails;
import com.aspirasoft.huntrek.view.GameStartingView;
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
import sfllhkhan95.game.GameStartListener;

public class ActivityHunt extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, MapboxMap.OnMarkerClickListener, GameTimeListener {

    private final DatabaseUtils db = DatabaseUtils.getInstance();
    private User currentUser;

    private DialogGameMenu mDialogGameMenu;
    private DialogUserDetails mDialogUserDetails;

    private GameStartingView mGameStartingView;
    private AvatarView mAvatarView;
    private MapView mMapView;

    private HuntItGame mGame;
    private MapboxMap mGameMap;
    private MarkerOptions mPlayerMarker;
    private LatLng lastKnownPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_hunt);

        // Check that user is logged in
        currentUser = UserManager.getInstance().getActiveUser();
        if (currentUser == null) {
            finish();
            return;
        }

        try {
            lastKnownPosition = savedInstanceState.getParcelable("lastLocation");
        } catch (Exception ignored) {

        }

        // Assign views
        mGameStartingView = findViewById(R.id.loadingScreen);
        mAvatarView = findViewById(R.id.avatar);

        // Assign click listeners
        findViewById(R.id.avatar).setOnClickListener(this);
        findViewById(R.id.button_menu).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        // Configure maps
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_token));
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Display 'game starting' screen
        mGameStartingView.start();

        // Set up dialog boxes
        mDialogGameMenu = new DialogGameMenu(this, R.style.DialogTheme);
        mDialogGameMenu.setGameActivity(this);

        mDialogUserDetails = new DialogUserDetails(this, R.style.DialogTheme);
        mDialogUserDetails.setUser(currentUser);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onMapReady(MapboxMap mMapboxMap) {
        this.mGameMap = mMapboxMap;

        // Register click handlers
        mGameMap.setOnMarkerClickListener(this);

        mGame = new HuntItGame(this, currentUser);
        mGame.setStartListener(new GameUIThread());

        // Display user details
        mDialogUserDetails.setCharacterImage(mGame.getPlayer().getView().getCharacterDrawable());

        for (TreasureChest chest : db.getChests()) {
            LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
            MarkerOptions marker = new MarkerOptions()
                    .position(latLng)
                    .title("Chest #" + chest.getId())
                    .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(R.drawable.marker_treasure_chest));

            mGameMap.addMarker(marker);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTitle().startsWith("Chest #")) {
            float distance = distanceBetween(mGame.getPlayer().getPosition(), marker.getPosition());

            if (!mGame.isPaused() && distance <= TreasureChest.RANGE) {
                int id = Integer.valueOf(marker.getTitle().split("#")[1]);
                TreasureChest chest = db.getChest(id);
                if (chest != null) {
                    Intent i = new Intent(getApplicationContext(), ActivityCollect.class);
                    i.putExtra("ChestValue", chest.getValue());

                    collectTreasure(id);

                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            } else {
                new AlertDialog.Builder(ActivityHunt.this)
                        .setMessage("You must be within " + TreasureChest.RANGE +
                                "m of the treasure to collect it. Current distance is " +
                                distance + "m.")
                        .create()
                        .show();
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
        if (mMapView != null) {
            mMapView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.pauseScreen).setVisibility(View.VISIBLE);
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // Save the user's current game state
        outState.putParcelable("lastLocation", mGame.getPlayer().getPosition());

        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                mDialogUserDetails.show();
                mDialogUserDetails.updateUI();
                break;

            case R.id.button_menu:
                mDialogGameMenu.show();
                break;

            case R.id.button_sign_out:
                UserManager.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    public void signOut() {
        UserManager.getInstance().signOut();

        startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
        overridePendingTransition(0, 0);
        finish();
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
                    .title("Chest #" + chest.getId())
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
                    .title("Chest #" + chest.getId())
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
            Log.i(HuntItApp.TAG, "Received " + chest.getValue() + " coins from chest # " + id);
            mGame.getPlayer().addScore(chest.getValue());
            currentUser.setChestsOpened(currentUser.getChestsOpened() + 1);
        } else {
            Log.e(HuntItApp.TAG, "Could not read chest # " + id);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Display user's avatar and name
                    mAvatarView.setAvatar(mGame.getPlayer().getView().getFaceDrawable());
                    mAvatarView.setUserName(currentUser.getName());
                }
            });

            // Start game loop
            runOnUiThread(this);
        }

        @Override
        public void run() {
            if (!mGameStartingView.isFinished()) {
                mGameStartingView.finish();
            }

            // Update displayed user info
            mAvatarView.setXP(currentUser.checkCurrentXP());
            mAvatarView.setLevel(currentUser.checkLevel());

            UserManager.getInstance().refreshSession(currentUser);

            // If there are no treasure chests or a day has passed since last spawn, spawn new chests
            if (db.getChests().size() == 0 || db.checkDayPassedSinceLastSpawn()) {
                new CollectibleManager().spawnTreasureChests();
                for (TreasureChest chest : db.getChests()) {
                    LatLng latLng = new LatLng(chest.getLatitude(), chest.getLongitude());
                    MarkerOptions marker = new MarkerOptions()
                            .position(latLng)
                            .title("Chest #" + chest.getId())
                            .icon(IconFactory.getInstance(ActivityHunt.this).fromResource(
                                    mGame.isPaused() ? R.drawable.marker_target : R.drawable.marker_treasure_chest));

                    mGameMap.addMarker(marker);
                }
            }

            // Update UI elements
            findViewById(R.id.skybox).setVisibility(mGame.isPaused() ? View.GONE : View.VISIBLE);

            findViewById(R.id.pause_button).setVisibility(mGame.isPaused() ? View.VISIBLE : View.GONE);
            findViewById(R.id.avatar).setVisibility(mGame.isPaused() ? View.GONE : View.VISIBLE);
            findViewById(R.id.button_menu).setVisibility(mGame.isPaused() ? View.GONE : View.VISIBLE);

            // Display correct map
            try {
                if (!mGame.isPaused()) {
                    LatLng position = mGame.getPlayer().getPosition();
                    if (position == null && lastKnownPosition != null) {
                        position = lastKnownPosition;
                    }
                    if (position != null) {
                        findViewById(R.id.pauseScreen).setVisibility(View.GONE);
                        mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(position)
                                // .zoom(18.5f)
                                .tilt(60.0f)
                                // .bearing(mGame.getPlayer().getDirection())
                                .build()));
                    }
                } else {
                    mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(33.645239, 72.991708))
                            .zoom(14.0f)
                            .tilt(0)
                            .build()));
                }
            } catch (NullPointerException ignored) {
                Log.e(HuntItApp.TAG, "Map not initialized");
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