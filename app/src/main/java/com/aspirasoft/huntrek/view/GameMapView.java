package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.aspirasoft.huntrek.App;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.utils.GameMapListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class GameMapView extends MapView implements OnMapReadyCallback {

    private GameMapListener gameMapListener;

    public GameMapView(@NonNull Context context) {
        super(context);
        Mapbox.getInstance(context, context.getString(R.string.mapbox_token));
    }

    public GameMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Mapbox.getInstance(context, context.getString(R.string.mapbox_token));
    }

    public GameMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Mapbox.getInstance(context, context.getString(R.string.mapbox_token));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mMapboxMap) {
        // Stylize map, hide 3D buildings, disable compass and all gestures
        mMapboxMap.getUiSettings().setAllGesturesEnabled(false);
        mMapboxMap.getUiSettings().setCompassEnabled(false);
        mMapboxMap.addPolyline(App.getBoundary());

        if (gameMapListener != null) {
            gameMapListener.onMapReady(mMapboxMap);
        }
    }

    public void setGameMapListener(GameMapListener gameMapListener) {
        this.gameMapListener = gameMapListener;
    }

}