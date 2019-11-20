package com.aspirasoft.huntrek.core;

import android.app.Activity;
import android.widget.ImageView;
import com.aspirasoft.huntrek.R;

/**
 * Created by saifkhichi96 on 29/12/2017.
 */

public class SkyBox {

    private final int CLOUD_COUNT = 3;

    private ImageView[] clouds = new ImageView[CLOUD_COUNT];
    private float[] cloudSpeed = new float[CLOUD_COUNT];

    public SkyBox(Activity context) {
        clouds[0] = context.findViewById(R.id.cloud_1);
        clouds[1] = context.findViewById(R.id.cloud_2);
        clouds[2] = context.findViewById(R.id.cloud_3);

        cloudSpeed[0] = 0.010f;
        cloudSpeed[1] = 0.050f;
        cloudSpeed[2] = 0.025f;
    }

    public void update(long t) {
        for (int i = 0; i < CLOUD_COUNT; i++) {
            float v = cloudSpeed[i];
            float s = v * t;
            clouds[i].setX(clouds[i].getX() + s);
        }
    }

}
