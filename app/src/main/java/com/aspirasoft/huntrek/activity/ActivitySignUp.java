package com.aspirasoft.huntrek.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.bo.UserManager;
import com.aspirasoft.huntrek.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ActivitySignUp extends AppCompatActivity
        implements View.OnClickListener, UserManager.RegistrationCallback {

    private TextInputEditText mNameInputView;
    private TextInputEditText mEmailInputView;

    private Button mSubmitButton;
    private AppCompatButton mFacebookButton;

    private ViewGroup mWaitingViews;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_sign_up);

        // Assign views
        mWaitingViews = findViewById(R.id.waitingViews);

        mNameInputView = findViewById(R.id.user_name);
        mEmailInputView = findViewById(R.id.userEmail);
        mSubmitButton = findViewById(R.id.buttonSubmit);
        mFacebookButton = findViewById(R.id.buttonFacebook);

        // Assign click listeners
        mSubmitButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                submitDetails();
                break;

            case R.id.buttonFacebook:
                break;
        }
    }

    private void submitDetails() {
        String name = mNameInputView.getText().toString().trim();
        String email = mEmailInputView.getText().toString().trim();

        if (validateName(name) && validateEmail(email)) {
            registerUser(name, email);
        }
    }

    private void registerUser(String name, String email) {
        try {
            mWaitingViews.setVisibility(View.VISIBLE);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            User user = new User();
            user.setFirebaseId(userId);
            user.setName(name);
            user.setEmail(email);

            UserManager.getInstance().register(userId, user, this);
        } catch (NullPointerException ex) {
            mWaitingViews.setVisibility(View.GONE);
            Log.e(HuntItApp.TAG, "No firebase user active. Cannot complete sign up.");
            ex.printStackTrace();
        }
    }

    private boolean validateName(String name) {
        return !name.isEmpty();
    }

    private boolean validateEmail(String email) {
        return !email.isEmpty();
    }

    @Override
    public void onRegistrationComplete(User user) {
        mWaitingViews.setVisibility(View.GONE);

        UserManager.getInstance().signIn(user);
        startActivity(new Intent(getApplicationContext(), ActivityHunt.class));
        overridePendingTransition(0, 0);
        finish();
    }

}