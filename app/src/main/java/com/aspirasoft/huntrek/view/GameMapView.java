package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.listener.GameMapListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import static com.aspirasoft.huntrek.core.HuntItGame.*;

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

    private PolylineOptions getBoundary() {
        return new PolylineOptions()
                .add(new LatLng(A.x, A.y))
                .add(new LatLng(B.x, B.y))
                .add(new LatLng(C.x, C.y))
                .add(new LatLng(D.x, D.y))
                .add(new LatLng(A.x, A.y));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mMapboxMap) {
        mMapboxMap.getUiSettings().setAllGesturesEnabled(false);
        mMapboxMap.getUiSettings().setCompassEnabled(false);

        boolean DEBUG_MODE = true;
        if (DEBUG_MODE) {
            mMapboxMap.getUiSettings().setRotateGesturesEnabled(true);
            mMapboxMap.getUiSettings().setZoomGesturesEnabled(true);
            mMapboxMap.setMinZoomPreference(14);
            mMapboxMap.setMaxZoomPreference(19);
        }
        mMapboxMap.addPolyline(getBoundary());

        if (gameMapListener != null) {
            gameMapListener.onMapReady(mMapboxMap);
        }
    }

    public void setGameMapListener(GameMapListener gameMapListener) {
        this.gameMapListener = gameMapListener;
    }

}