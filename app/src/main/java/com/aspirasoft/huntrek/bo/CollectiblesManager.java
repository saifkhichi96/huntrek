package com.aspirasoft.huntrek.bo;

import android.graphics.PointF;

import com.aspirasoft.huntrek.core.collectibles.TreasureChest;
import com.aspirasoft.huntrek.utils.Database;

import java.util.Random;

/**
 * Created by saifkhichi96 on 23/12/2017.
 */

public class CollectiblesManager {

    public static final int MAX_CHESTS = 20;

    private PointF A = new PointF(33.633622f, 72.989001f);
    private PointF B = new PointF(33.647925f, 72.978280f);
    private PointF C = new PointF(33.654916f, 72.995557f);
    private PointF D = new PointF(33.641953f, 73.005066f);

    public void spawnTreasureChests() {
        for (int i = 0; i < MAX_CHESTS; i++) {
            TreasureChest chest = new TreasureChest(i);
            chest.setValue(100 + new Random().nextInt(400));

            double latitude, longitude;
            double r1 = Math.random();
            double r2 = Math.random();

            if (new Random().nextInt(2) == 0) {
                // P = (1 - sqrt(r1)) * A + (sqrt(r1) * (1 - r2)) * B + (sqrt(r1) * r2) * C
                latitude = (1 - Math.sqrt(r1)) * A.x + (Math.sqrt(r1) * (1 - r2)) * B.x + (Math.sqrt(r1) * r2) * C.x;
                longitude = (1 - Math.sqrt(r1)) * A.y + (Math.sqrt(r1) * (1 - r2)) * B.y + (Math.sqrt(r1) * r2) * C.y;
            } else {
                latitude = (1 - Math.sqrt(r1)) * A.x + (Math.sqrt(r1) * (1 - r2)) * D.x + (Math.sqrt(r1) * r2) * C.x;
                longitude = (1 - Math.sqrt(r1)) * A.y + (Math.sqrt(r1) * (1 - r2)) * D.y + (Math.sqrt(r1) * r2) * C.y;
            }
            chest.setLatitude(latitude);
            chest.setLongitude(longitude);

            Database db = Database.getInstance();
            db.addChest(chest);
            db.updateLastSpawnTime();
        }
    }

}