package com.aspirasoft.huntrek.bo.maps;

import android.content.Context;


public class LocationController extends Thread {

    private ProviderTracker mProviderTracker = new ProviderTracker();
    private LocationReceiver locationReceiver;
    private boolean tracking = true;

    public LocationController(Context context) throws IllegalStateException {
        // Get location service
        android.location.LocationManager manager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Ensure availability of a location provider
        boolean isGPSAvailable = manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetworkAvailable = manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        if (!isGPSAvailable && !isNetworkAvailable) {
            throw new IllegalStateException("No location provider available.");
        }

        // Subscribe to all available location providers
        //noinspection StatementWithEmptyBody
        if (isGPSAvailable) {
            LocationTracker listener = new LocationTracker(android.location.LocationManager.GPS_PROVIDER, mProviderTracker);
            mProviderTracker.initProvider(context, android.location.LocationManager.GPS_PROVIDER, listener);
        } else {
            // TODO: Request user to turn on GPS
        }

        if (isNetworkAvailable) {
            LocationTracker listener = new LocationTracker(android.location.LocationManager.NETWORK_PROVIDER, mProviderTracker);
            mProviderTracker.initProvider(context, android.location.LocationManager.NETWORK_PROVIDER, listener);
        }
    }

    public void setLocationReceiver(LocationReceiver locationReceiver) {
        this.locationReceiver = locationReceiver;
    }

    @Override
    public void run() {
        while (tracking) {
            if (locationReceiver != null && mProviderTracker.getLastKnownLocation() != null) {
                locationReceiver.onLocationReceived(mProviderTracker.getLastKnownLocation());
            }
        }
    }

    public void stopTracking() {
        tracking = false;
    }

    public boolean isTracking() {
        return tracking;
    }

}
