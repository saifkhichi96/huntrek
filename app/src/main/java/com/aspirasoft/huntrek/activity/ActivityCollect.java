package com.aspirasoft.huntrek.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.UserManager;
import com.aspirasoft.huntrek.model.User;
import com.aspirasoft.huntrek.view.LoadingAnimation;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.unity3d.player.UnityPlayer;

public class ActivityCollect extends Activity implements View.OnClickListener {

    protected UnityPlayer mUnityPlayer;
    private FrameLayout mUnitPlayerContainer;
    private LoadingAnimation mLoadingView;

    private ImageButton mSettingsButton;
    private Switch mARSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_collect);

        // Check that user is logged in
        User currentUser = UserManager.getInstance().getActiveUser();
        if (currentUser == null) {
            finish();
            return;
        }

        // Assign views
        mUnitPlayerContainer = findViewById(R.id.unity_player_view);
        mSettingsButton = findViewById(R.id.button_menu);
        mLoadingView = findViewById(R.id.pauseScreen);
        mARSwitch = findViewById(R.id.ARSwitch);

        // Assign click listeners
        mSettingsButton.setOnClickListener(this);

        // Configure unity player
        mUnityPlayer = new UnityPlayer(this);
        mUnitPlayerContainer.addView(
                mUnityPlayer, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

        // Display 'loading' screen
        mLoadingView.setVisibility(View.VISIBLE);
        mUnityPlayer.requestFocus();

        // Schedule 'loading' screen to auto-hide after 3.5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
            }
        }, 3500L);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mSettingsButton)) {
            v.setVisibility(View.GONE);
            UnityPlayer.UnitySendMessage("Treasure Chest", "Open", "open");

            DraweeController controller =
                    Fresco.newDraweeControllerBuilder()
                            .setUri("res:///" + R.drawable.fireworks)
                            .setAutoPlayAnimations(true)
                            .build();

            final SimpleDraweeView mFallingCoinsView = findViewById(R.id.falling_coins);
            mFallingCoinsView.setController(controller);

            Handler mHandler = new Handler();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFallingCoinsView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, 1500L);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = getIntent();
                            int coins = i.getIntExtra("ChestValue", 0);

                            TextView coinsView = findViewById(R.id.coins_collected);
                            coinsView.setText("Coins: " + coins);
                            coinsView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, 2000L);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    });
                }
            }, 4000L);
        }
    }

    public void escape(View view) {
        finish();
        overridePendingTransition(0, 0);
    }

}