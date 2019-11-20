package com.aspirasoft.huntrek.bo.maps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.CallSuper;

class LocationTracker implements LocationListener {

    private final String provider;
    private final ProviderTracker providerTracker;

    LocationTracker(String provider, ProviderTracker providerTracker) {
        this.provider = provider;
        this.providerTracker = providerTracker;
    }

    @Override
    @CallSuper
    public void onLocationChanged(Location location) {
        Log.i("HuntItGame/LocationTracker", "Update received from " + this.provider + " provider.");
        providerTracker.reportAccuracy(this.provider, location.getAccuracy());
        providerTracker.reportLocation(this.provider, location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    @CallSuper
    public void onProviderEnabled(String provider) {
        providerTracker.registerProvider(provider);
    }

    @Override
    @CallSuper
    public void onProviderDisabled(String provider) {
        providerTracker.unregisterProvider(provider);
    }

}