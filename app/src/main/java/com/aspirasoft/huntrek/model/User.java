package com.aspirasoft.huntrek.model;

import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * User is an entity class which represents a user of the application.
 *
 * @author saifkhichi96
 */
public class User implements Serializable {

    private String firebaseId;
    private String name;
    private String email;
    private int score;
    private int chestsOpened;
    private int characterType;

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        if (firebaseId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(firebaseId)
                    .child("score")
                    .setValue(score);
        }
    }

    public int getChestsOpened() {
        return chestsOpened;
    }

    public void setChestsOpened(int chestsOpened) {
        this.chestsOpened = chestsOpened;
        if (firebaseId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(firebaseId)
                    .child("chestsOpened")
                    .setValue(chestsOpened);
        }
    }

    public int getCharacterType() {
        return characterType;
    }

    public void setCharacterType(int characterType) {
        this.characterType = characterType;
    }

    public int checkTotalXP() {
        return chestsOpened * 5;
    }

    public int checkCurrentXP() {
        return checkTotalXP() % 100;
    }

    public int checkLevel() {
        return (int) Math.ceil(checkTotalXP() / 100.0);
    }

}