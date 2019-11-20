package com.aspirasoft.huntrek.bo;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.gson.Gson;

/**
 * Created by saifkhichi96 on 03/01/2018.
 */

public class UserManager {

    private static UserManager ourInstance = new UserManager();
    private final DatabaseReference mDatabase;
    private final SharedPreferences preferences;

    private UserManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        preferences = HuntItApp.PREFS_FILE;
    }

    public static UserManager getInstance() {
        return ourInstance;
    }

    public void register(String userId, User user, final RegistrationCallback callback) {
        DatabaseReference ref = mDatabase.child("users").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Object value = dataSnapshot.getValue();
                    callback.onRegistrationComplete(dataSnapshot.getValue(User.class));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("HunTrek", ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HunTrek", databaseError.getDetails());
            }
        });
        ref.setValue(user);
    }

    @Nullable
    public User getActiveUser() {
        User user = null;
        String json = preferences.getString("activeUser", null);
        if (json != null) {
            user = new Gson().fromJson(json, User.class);
        }
        return user;
    }

    public boolean isSignedIn() {
        return preferences.getBoolean("signedIn", false) &&
                preferences.getString("activeUser", null) != null;
    }

    public void refreshSession(User user) {
        signIn(user);
    }

    public void signIn(User user) {
        preferences.edit()
                .putString("activeUser", new Gson().toJson(user))
                .putBoolean("signedIn", true)
                .apply();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        preferences.edit()
                .putString("activeUser", null)
                .putBoolean("signedIn", false)
                .apply();
    }

    public void isRegistered(String userId, final RegistrationQueryCallback callback) {
        mDatabase.child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onResponseReceived(dataSnapshot.getValue() != null && dataSnapshot.getChildren() != null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setScore(int score) {
        if (getActiveUser() != null) {
            mDatabase.child("users").child(getActiveUser().getFirebaseId()).child("score").setValue(score);
        }
    }

    public interface RegistrationCallback {
        void onRegistrationComplete(User user);
    }

    public interface RegistrationQueryCallback {
        void onResponseReceived(boolean isRegistered);
    }

}
