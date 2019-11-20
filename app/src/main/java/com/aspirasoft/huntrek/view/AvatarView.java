package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.aspirasoft.huntrek.R;


public class AvatarView extends RelativeLayout {

    private final ProgressBar mXPCounter;
    private final ImageView mAvatarImage;
    private final TextView mUserLevelView;
    private final TextView mUserNameView;

    public AvatarView(Context context) {
        super(context);
        inflate(context, R.layout.view_avatar, this);

        mXPCounter = findViewById(R.id.xp_counter);
        mAvatarImage = findViewById(R.id.avatar_image);
        mUserLevelView = findViewById(R.id.user_level);
        mUserNameView = findViewById(R.id.user_name);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_avatar, this);

        mXPCounter = findViewById(R.id.xp_counter);
        mAvatarImage = findViewById(R.id.avatar_image);
        mUserLevelView = findViewById(R.id.user_level);
        mUserNameView = findViewById(R.id.user_name);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_avatar, this);

        mXPCounter = findViewById(R.id.xp_counter);
        mAvatarImage = findViewById(R.id.avatar_image);
        mUserLevelView = findViewById(R.id.user_level);
        mUserNameView = findViewById(R.id.user_name);
    }

    public void setAvatar(Drawable drawable) {
        mAvatarImage.setImageDrawable(drawable);
    }

    public void setUserName(String name) {
        mUserNameView.setText(name);
    }

    public void setLevel(int level) {
        mUserLevelView.setText(String.valueOf(level));
    }

    public void setXP(int currentXP) {
        mXPCounter.setMax(100);
        mXPCounter.setProgress(currentXP);
    }

}