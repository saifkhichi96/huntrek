package com.aspirasoft.huntrek.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.model.User;

/**
 * Created by saifkhichi96 on 04/01/2018.
 */

public class DialogUserDetails extends Dialog implements View.OnClickListener {

    private User user;
    private Drawable mCharacterImage = null;

    public DialogUserDetails(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_details);

        findViewById(R.id.dismissButton).setOnClickListener(this);
        updateUI();
    }

    public void updateUI() {
        if (user != null) {
            TextView mUserNameView = findViewById(R.id.user_name);
            mUserNameView.setText(user.getName());

            TextView mUserEmailView = findViewById(R.id.userEmail);
            mUserEmailView.setText(user.getEmail());

            TextView mChestCountView = findViewById(R.id.chestsOpened);
            mChestCountView.setText(String.valueOf(user.getChestsOpened()));

            TextView mCoinCountView = findViewById(R.id.coinsCollected);
            mCoinCountView.setText(String.valueOf(user.getScore()));

            int totalXP = user.checkTotalXP();
            int targetXP = 100;
            int userXP = totalXP % targetXP;
            int level = (totalXP / targetXP) + 1;

            TextView mUserXPView = findViewById(R.id.userXP);
            mUserXPView.setText(String.valueOf(userXP));

            TextView mTargetXPView = findViewById(R.id.targetXP);
            mTargetXPView.setText(" / " + targetXP + " XP");

            TextView mUserLevelView = findViewById(R.id.user_level);
            mUserLevelView.setText(String.valueOf(level));

            TextView mTotalXPView = findViewById(R.id.totalXP);
            mTotalXPView.setText(String.valueOf(totalXP));

            ProgressBar mUserProgress = findViewById(R.id.userProgress);
            mUserProgress.setMax(targetXP);
            mUserProgress.setProgress(userXP);
        }

        if (mCharacterImage != null) {
            ImageView mCharacterImage = findViewById(R.id.characterImage);
            mCharacterImage.setImageDrawable(this.mCharacterImage);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismissButton:
                dismiss();
                break;
        }
    }

    public void setCharacterImage(Drawable characterRes) {
        this.mCharacterImage = characterRes;
    }

}
