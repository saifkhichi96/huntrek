package com.aspirasoft.huntrek;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "HunTrek";

    private static final float LOCATION_REFRESH_DISTANCE = 1.0f;   // Update location on 10m distance change
    private static final long LOCATION_REFRESH_TIME = 500L;        // Update location every 500ms

    private LatLng location;
    private float bearing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE,
                    this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        this.bearing = location.getBearing();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Remove any old markers
        map.clear();

        // TODO: Hide all labels except street names
        boolean success = map.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style_json)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        // Disable zoom and pan actions
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);

        // Hide 3D buildings
        map.setBuildingsEnabled(false);

        // Create a marker at current location
        MarkerOptions marker = new MarkerOptions().position(location).title("You are here!");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker));

        // Add marker at current location
        map.addMarker(marker);

        map.getUiSettings().setCompassEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Zoom camera on current location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(18)
                .tilt(67.5f)
                .bearing(bearing)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setMinZoomPreference(20);
        map.setMaxZoomPreference(20);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}