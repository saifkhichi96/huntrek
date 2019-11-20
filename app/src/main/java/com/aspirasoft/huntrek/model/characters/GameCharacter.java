package com.aspirasoft.huntrek.model.characters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.Nullable;

import java.io.IOException;


public abstract class GameCharacter {

    private final static String DIR_ASSETS = "asset:///";
    private final static String DIR_CHARACTERS = "characters/";

    private final String faceImagePath;
    private final String fullImagePath;
    private final String walkingAnimPath;

    GameCharacter(String dir, String faceImage, String fullImage, String walkingAnim) {
        if (!dir.endsWith("/")) dir += "/";

        this.faceImagePath = dir + faceImage;
        this.fullImagePath = dir + fullImage;
        this.walkingAnimPath = dir + walkingAnim;
    }

    public Uri getFaceImageUri() {
        String uriString = DIR_ASSETS + DIR_CHARACTERS + faceImagePath;
        return Uri.parse(uriString);
    }

    public Uri getFullImageUri() {
        String uriString = DIR_ASSETS + DIR_CHARACTERS + fullImagePath;
        return Uri.parse(uriString);
    }

    public Uri getWalkingAnimUri() {
        String uriString = DIR_ASSETS + DIR_CHARACTERS + walkingAnimPath;
        return Uri.parse(uriString);
    }

    @Nullable
    public Drawable getFaceDrawable(Context context) {
        try {
            return Drawable.createFromStream(context.getAssets().open(DIR_CHARACTERS + faceImagePath), null);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public Drawable getFullDrawable(Context context) {
        try {
            return Drawable.createFromStream(context.getAssets().open(DIR_CHARACTERS + fullImagePath), null);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public Drawable getWalkDrawable(Context context) {
        try {
            return Drawable.createFromStream(context.getAssets().open(DIR_CHARACTERS + walkingAnimPath), null);
        } catch (IOException e) {
            return null;
        }
    }

}
