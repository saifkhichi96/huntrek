package com.aspirasoft.huntrek.core;

import android.location.Location;
import android.util.Log;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.bo.maps.LocationController;
import com.aspirasoft.huntrek.bo.maps.LocationReceiver;
import com.aspirasoft.huntrek.model.User;
import com.aspirasoft.huntrek.view.GamePlayerView;
import com.mapbox.mapboxsdk.geometry.LatLng;


public class GamePlayer implements LocationReceiver {

    private final GamePlayerView mView;
    private final User mUser;

    private HuntItGame mGame;

    private LocationController mLocationController;

    private LatLng mPlayerPosition = null;
    private float mPlayerDirection = 0.f;
    private double mPlayerSpeed = 0.0;


    public GamePlayer(GamePlayerView mView, User mUser) {
        this.mView = mView;
        this.mUser = mUser;

        this.mView.setCharacterType(this.mUser.getCharacterType());
    }

    public void setLocationController(LocationController locationController) {
        this.mLocationController = locationController;
        this.mLocationController.setLocationReceiver(this);
        this.mLocationController.start();
    }

    @Override
    @CallSuper
    public void onLocationReceived(@NonNull Location location) {
        mPlayerPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mPlayerDirection = location.getBearing();
        mPlayerSpeed = location.getSpeed();

        if (!this.mGame.isStarted()) {
            mGame.start();
        }
    }

    public void addScore(int score) {
        mUser.setScore(getScore() + score);
    }

    public int getScore() {
        return mUser.getScore();
    }

    @Nullable
    public LatLng getPosition() {
        if (mPlayerPosition == null) {
            Log.i(HuntItApp.TAG, "No location information.");
        }
        return mPlayerPosition;
    }

    public float getDirection() {
        return mPlayerDirection;
    }

    private double getSpeed() {
        return mPlayerSpeed;
    }

    private boolean isStationary() {
        return getSpeed() <= 0.07;
    }

    /**
     * Indicates whether the mGame player is walking or not. Motion speed in range
     * 0.25 km/h to 7 km/h is considered as walking.
     *
     * @return true if player is walking
     */
    private boolean isWalking() {
        return getSpeed() > 0.07 && getSpeed() <= 1.95;
    }

    /**
     * Indicates whether the mGame player is running or not. Motion speed in range
     * 7 km/h to 15 km/h is considered as running.
     *
     * @return true if player is running.
     */
    private boolean isRunning() {
        return getSpeed() > 1.95 && getSpeed() <= 4.15;
    }

    /**
     * Indicates whether the mGame player is driving or not. Motion speed in greater than
     * 15 km/h is considered as driving.
     *
     * @return true if player is driving
     */
    private boolean isDriving() {
        return getSpeed() > 4.15;
    }

    public void update(long elapsedTime) {
        if (this.isStationary()) {
            mView.stopWalking();
        } else {
            mView.startWalking();
        }
    }

    public void finish() {
        if (mLocationController.isTracking()) {
            mLocationController.stopTracking();
        }
    }

    public void setGame(HuntItGame game) {
        this.mGame = game;
    }

    public GamePlayerView getView() {
        return mView;
    }

    public void hideView() {
        mView.setVisibility(View.GONE);
    }

    public void showView() {
        mView.setVisibility(View.VISIBLE);
    }

    public User getUser() {
        return mUser;
    }
}