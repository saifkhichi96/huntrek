package com.aspirasoft.huntrek.core.characters;

import android.content.Context;
import android.location.Location;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aspirasoft.huntrek.bo.maps.LocationController;
import com.aspirasoft.huntrek.bo.maps.LocationReceiver;
import com.aspirasoft.huntrek.core.HunTrekGame;
import com.aspirasoft.huntrek.view.GamePlayerView;
import com.mapbox.mapboxsdk.geometry.LatLng;


public class GamePlayer implements LocationReceiver {

    private final GamePlayerView mGamePlayerView;
    private LocationController mLocationController;
    private LatLng mPlayerPosition = null;
    private float mPlayerDirection = 0.f;
    private double mPlayerSpeed = 0.0;
    private int mPlayerScore;
    private HunTrekGame mGame;

    public GamePlayer(Context context, GamePlayerView mGamePlayerView) {
        this.mGamePlayerView = mGamePlayerView;

        mLocationController = new LocationController(context);
        mLocationController.setLocationReceiver(this);
        mLocationController.start();
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
        this.mPlayerScore += score;
    }

    public int getScore() {
        return mPlayerScore;
    }

    @Nullable
    public LatLng getPosition() {
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
            mGamePlayerView.stop();
        } else {
            mGamePlayerView.start();
        }


    }

    public void finish() {
        if (mLocationController.isTracking()) {
            mLocationController.stopTracking();
        }
    }

    public void setGame(HunTrekGame game) {
        this.mGame = game;
    }

    public void hideView() {
        mGamePlayerView.hide();
    }

    public void showView() {
        mGamePlayerView.show();
        ;
    }
}