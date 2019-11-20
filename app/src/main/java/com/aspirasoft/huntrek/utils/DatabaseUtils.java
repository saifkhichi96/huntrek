package com.aspirasoft.huntrek.utils;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.HuntItApp;
import com.aspirasoft.huntrek.bo.UserManager;
import com.aspirasoft.huntrek.model.collectibles.TreasureChest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saifkhichi96 on 23/12/2017.
 */

public class DatabaseUtils {
    private static DatabaseUtils ourInstance;
    private final SharedPreferences preferences;

    private int MAX_CHEST;

    private DatabaseUtils() throws IllegalStateException {
        preferences = HuntItApp.PREFS_FILE;
        MAX_CHEST = preferences.getInt("MAX_CHEST", 0);
    }

    public static DatabaseUtils getInstance() {
        if (ourInstance == null) {
            ourInstance = new DatabaseUtils();
        }
        return ourInstance;
    }

    public void addChest(TreasureChest chest) {
        if (chest.getId() + 1 > MAX_CHEST) MAX_CHEST = chest.getId() + 1;

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chest);
        prefsEditor.putString("Chest #" + chest.getId(), json);
        prefsEditor.putInt("MAX_CHEST", MAX_CHEST);
        prefsEditor.apply();
    }

    @Nullable
    public TreasureChest getChest(int chestId) {
        String chestTag = "Chest #" + chestId;
        String json = preferences.getString(chestTag, null);
        if (json != null) {
            Log.v("HuntItApp/DatabaseUtils", "Successfully not retrieved " + chestTag);

            Gson gson = new Gson();
            return gson.fromJson(json, TreasureChest.class);
        }

        Log.e("HuntItApp/DatabaseUtils", "Could not retrieve " + chestTag);
        return null;
    }

    private void removeChest(int chestId) {
        preferences.edit()
                .putString("Chest #" + chestId, null)
                .apply();
    }

    @Nullable
    public TreasureChest popChest(int chestId) {
        TreasureChest chest = getChest(chestId);
        if (chest != null) {
            removeChest(chestId);
        }
        return chest;
    }

    public List<TreasureChest> getChests() {
        List<TreasureChest> chests = new ArrayList<>();
        for (int i = 0; i < MAX_CHEST; i++) {
            Gson gson = new Gson();
            String json = preferences.getString("Chest #" + i, null);
            if (json != null) {
                chests.add(gson.fromJson(json, TreasureChest.class));
            }
        }
        return chests;
    }

    public int getChestCount() {
        return getChests().size();
    }

    public int getScore() {
        try {
            return UserManager.getInstance().getActiveUser().getScore();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public void setScore(int score) {
        UserManager.getInstance().setScore(score);
    }

    public boolean checkDayPassedSinceLastSpawn() {
        long currentTimeMillis = System.currentTimeMillis();
        long lastSpawnTime = preferences.getLong("lastSpawnTime", 0);
        return (lastSpawnTime == 0 || currentTimeMillis - lastSpawnTime >= 24 * 60 * 60 * 1000);
    }

    public void updateLastSpawnTime() {
        preferences.edit()
                .putLong("lastSpawnTime", System.currentTimeMillis())
                .apply();
    }
}