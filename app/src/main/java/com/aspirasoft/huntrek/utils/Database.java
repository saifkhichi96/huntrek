package com.aspirasoft.huntrek.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aspirasoft.huntrek.core.collectibles.TreasureChest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saifkhichi96 on 23/12/2017.
 */

public class Database {
    private static Database ourInstance;
    private static SharedPreferences preferences;

    private int MAX_CHEST;

    private Database() throws IllegalStateException {
        if (preferences == null) {
            throw new IllegalStateException("Database not initialised. Call Database.init()");
        }

        MAX_CHEST = preferences.getInt("MAX_CHEST", 0);
    }

    public static Database getInstance() {
        if (ourInstance == null) {
            ourInstance = new Database();
        }
        return ourInstance;
    }

    public static void init(Activity context) {
        Database.preferences = context.getSharedPreferences("HUNTREK_DB", Context.MODE_PRIVATE);
    }

    public void addChest(TreasureChest chest) {
        if (chest.getId() + 1 > MAX_CHEST) MAX_CHEST = chest.getId() + 1;

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chest);
        prefsEditor.putString("Chest #" + String.valueOf(chest.getId()), json);
        prefsEditor.putInt("MAX_CHEST", MAX_CHEST);
        prefsEditor.apply();
    }

    @Nullable
    private TreasureChest getChest(int chestId) {
        String chestTag = "Chest #" + String.valueOf(chestId);
        String json = preferences.getString(chestTag, null);
        if (json != null) {
            Log.v("App/Database", "Successfully not retrieved " + chestTag);

            Gson gson = new Gson();
            return gson.fromJson(json, TreasureChest.class);
        }

        Log.e("App/Database", "Could not retrieve " + chestTag);
        return null;
    }

    private void removeChest(int chestId) {
        preferences.edit()
                .putString("Chest #" + String.valueOf(chestId), null)
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
            String json = preferences.getString("Chest #" + String.valueOf(i), null);
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
        return preferences.getInt("Score", 0);
    }

    public void setScore(int score) {
        preferences.edit().putInt("Score", score).apply();
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