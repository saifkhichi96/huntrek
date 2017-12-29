package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.Random;

/**
 * Created by saifkhichi96 on 24/12/2017.
 */

public class GameStartProgressBar extends ProgressBar {
    private final Handler mLoadHandler = new Handler();
    private final Random mRandomGenerator = new Random();
    private boolean finishing = false;

    public GameStartProgressBar(Context context) {
        super(context);
    }

    public GameStartProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameStartProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameStartProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void start(Context context) {
        setProgress(0);

        int initialDelay = 250 + mRandomGenerator.nextInt(250);
        mLoadHandler.postDelayed(new ProgressUpdater(), initialDelay);
    }

    void finish() {
        finishing = true;
    }

    boolean isFinished() {
        return getProgress() == getMax();
    }

    private class ProgressUpdater extends Thread {
        @Override
        public void run() {
            // Advance one step at every iteration. Cap at 90% progress.
            int currentProgress = getProgress();
            if (!finishing && currentProgress < 0.90 * getMax()) {
                int newProgress = (int) (currentProgress + 0.01 * getMax());
                setProgress(newProgress);

                // Schedule next iteration
                int delay = 250 + mRandomGenerator.nextInt(250);
                mLoadHandler.postDelayed(this, delay);
            } else if (finishing && !isFinished()) {
                int newProgress = (int) (currentProgress + 0.01 * getMax());
                setProgress(newProgress);

                // Schedule next iteration
                int delay = 25;
                mLoadHandler.postDelayed(this, delay);
            }
        }
    }

}
