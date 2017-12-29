package com.aspirasoft.huntrek;

import android.app.Application;
import android.graphics.PointF;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class App extends Application {

    private static final PointF A = new PointF(33.633622f, 72.989001f);
    private static final PointF B = new PointF(33.647925f, 72.978280f);
    private static final PointF C = new PointF(33.654916f, 72.995557f);
    private static final PointF D = new PointF(33.641953f, 73.005066f);

    public static PolylineOptions getBoundary() {
        return new PolylineOptions()
                .add(new LatLng(A.x, A.y))
                .add(new LatLng(B.x, B.y))
                .add(new LatLng(C.x, C.y))
                .add(new LatLng(D.x, D.y))
                .add(new LatLng(A.x, A.y));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

}