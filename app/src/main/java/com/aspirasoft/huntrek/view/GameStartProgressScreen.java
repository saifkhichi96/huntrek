package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspirasoft.huntrek.R;

/**
 * Created by saifkhichi96 on 24/12/2017.
 */

public class GameStartProgressScreen extends RelativeLayout {

    private ImageView coverImage;
    private TextView messageView;
    private GameStartProgressBar gameStartProgressBar;
    private boolean finishing = false;

    public GameStartProgressScreen(Context context) {
        super(context);
        init(context);
    }

    public GameStartProgressScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameStartProgressScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameStartProgressScreen(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.screen_starting, this);
        coverImage = findViewById(R.id.cover);
        messageView = findViewById(R.id.message);
        gameStartProgressBar = findViewById(R.id.progress);
    }

    public void start() {
        gameStartProgressBar.start(getContext());
        new Handler().postDelayed(new Carousel(), 100);
    }

    public void finish() {
        if (!finishing) {
            finishing = true;
            gameStartProgressBar.finish();
        }
    }

    public boolean isFinished() {
        return finishing;
    }

    private class Carousel extends Thread {

        int coverIndex = 0;
        int messageIndex = 0;
        private int[] coverImages = {
                R.drawable.bg_starting_2,
                R.drawable.bg_starting_1,
                R.drawable.bg_starting_3,
                R.drawable.bg_starting_4
        };
        private String[] messages = {
                "Do not trespass while playing.",
                "Stay alert at all times.",
                "Do not drive and play.",
                "Beware of your surroundings."
        };

        @Override
        public void run() {
            if (!gameStartProgressBar.isFinished()) {
                // Update cover image
                coverIndex++;
                if (coverIndex >= coverImages.length) coverIndex = 0;
                coverImage.setImageResource(coverImages[coverIndex]);

                // Update message text
                messageIndex++;
                if (messageIndex >= messages.length) messageIndex = 0;
                messageView.setText(messages[messageIndex]);

                // Wait for 1 second
                new Handler().postDelayed(this, 3500);
            } else {
                setVisibility(GONE);
            }
        }
    }

}