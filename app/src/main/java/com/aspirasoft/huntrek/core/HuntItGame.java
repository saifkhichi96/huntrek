package com.aspirasoft.huntrek.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.os.Vibrator;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.CollectibleManager;
import com.aspirasoft.huntrek.bo.maps.LocationController;
import com.aspirasoft.huntrek.model.User;
import com.aspirasoft.huntrek.model.collectibles.TreasureChest;
import com.aspirasoft.huntrek.utils.DatabaseUtils;
import com.aspirasoft.huntrek.view.GamePlayerView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import sfllhkhan95.game.FrameRefreshListener;
import sfllhkhan95.game.GameCore;
import sfllhkhan95.game.GameOverListener;
import sfllhkhan95.game.GamePauseListener;


public class HuntItGame extends GameCore implements FrameRefreshListener, GamePauseListener, GameOverListener {

    // Game only playable inside the bounded-region defined by following GPS coordinates
    public static final PointF A = new PointF(49.432949f, 7.745114f);
    public static final PointF B = new PointF(49.436359f, 7.772657f);
    public static final PointF C = new PointF(49.418511f, 7.762780f);
    public static final PointF D = new PointF(49.419604f, 7.732946f);

    private final CollectibleManager collectibleManager = new CollectibleManager();
    private final GamePlayer mGamePlayer;
    private final SkyBox skyBox;

    private Activity mGameActivity;
    private boolean started = false;
    private DatabaseUtils db = DatabaseUtils.getInstance();

    public HuntItGame(Activity context, User currentUser) {
        this.mGameActivity = context;

        // Assign listeners
        this.setPauseListener(this);
        this.setRefreshListener(this);
        this.setOverListener(this);

        // Set up game player
        GamePlayerView playerView = context.findViewById(R.id.game_player);
        mGamePlayer = new GamePlayer(playerView, currentUser);
        mGamePlayer.setLocationController(new LocationController(context));
        mGamePlayer.setGame(this);

        // Set up game environment
        skyBox = new SkyBox(context);
    }

    public GamePlayer getPlayer() {
        return mGamePlayer;
    }

    @Override
    public void start() throws RuntimeException {
        super.start();
        this.started = true;
    }

    @Override
    public void onPaused() {
        mGamePlayer.hideView();
    }

    @Override
    public void onResume() {
        mGamePlayer.showView();
    }

    @Override
    public void onOver() {
        mGamePlayer.finish();
    }

    @Override
    public void onUpdate(long elapsedTime) {
        checkForNearbyTreasure();

        mGamePlayer.update(elapsedTime);
        // skyBox.update(elapsedTime);
    }

    public boolean isStarted() {
        return started;
    }

    private void checkForNearbyTreasure() {
        if (mGamePlayer.getPosition() == null) return;
        for (TreasureChest chest : db.getChests()) {
            LatLng myLocation = mGamePlayer.getPosition();
            LatLng chestLocation = new LatLng(chest.getLatitude(), chest.getLongitude());

            if (distanceBetween(myLocation, chestLocation) < 50) {
                Vibrator v = (Vibrator) mGameActivity.getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(500);
                }
            }
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

}
