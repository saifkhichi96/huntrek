package com.aspirasoft.huntrek.core;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Vibrator;

import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.CollectiblesManager;
import com.aspirasoft.huntrek.core.characters.GamePlayer;
import com.aspirasoft.huntrek.core.collectibles.TreasureChest;
import com.aspirasoft.huntrek.utils.Database;
import com.aspirasoft.huntrek.view.GamePlayerView;
import com.aspirasoft.huntrek.view.SkyBox;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import sfllhkhan95.game.FrameRefreshListener;
import sfllhkhan95.game.GameCore;
import sfllhkhan95.game.GameOverListener;
import sfllhkhan95.game.GamePauseListener;


public class HunTrekGame extends GameCore implements FrameRefreshListener,
        GamePauseListener, GameOverListener {

    private final CollectiblesManager mObjectSpawner = new CollectiblesManager();
    private final GamePlayer mGamePlayer;
    private final SkyBox skyBox;
    private Activity mGameActivity;
    private boolean started = false;
    private Database db = Database.getInstance();

    public HunTrekGame(Activity context) {
        super(33);
        this.mGameActivity = context;

        this.setPauseListener(this);
        this.setRefreshListener(this);
        this.setOverListener(this);

        GamePlayerView playerView = new GamePlayerView((SimpleDraweeView) context.findViewById(R.id.playerIcon));
        mGamePlayer = new GamePlayer(context, playerView);
        mGamePlayer.setGame(this);
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
        skyBox.update(elapsedTime);
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
                v.vibrate(500);
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
