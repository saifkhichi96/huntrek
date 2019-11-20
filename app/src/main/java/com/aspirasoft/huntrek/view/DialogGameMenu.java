package com.aspirasoft.huntrek.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.activity.ActivityHunt;

/**
 * Created by saifkhichi96 on 04/01/2018.
 */

public class DialogGameMenu extends Dialog implements View.OnClickListener {


    private ActivityHunt gameActivity;

    public DialogGameMenu(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_menu);

        // Assign click listeners
        findViewById(R.id.dismissButton).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);
        findViewById(R.id.pauseButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismissButton:
                dismiss();
                break;

            case R.id.button_sign_out:
                if (gameActivity != null) gameActivity.signOut();
                dismiss();
                break;

            case R.id.pauseButton:
                if (gameActivity != null) gameActivity.toggleOverview(v);
                dismiss();
                break;
        }
    }

    public void setGameActivity(ActivityHunt gameActivity) {
        this.gameActivity = gameActivity;
    }

}
