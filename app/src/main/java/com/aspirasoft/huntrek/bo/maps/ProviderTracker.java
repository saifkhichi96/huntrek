package com.aspirasoft.huntrek.bo.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.*;

class ProviderTracker {

    private static final float REFRESH_DISTANCE = 0.1f;     // Update location on 1m distance change
    private static final long REFRESH_DELAY = 500L;         // Update location every 500ms

    private Context context;

    private List<String> availableProviders = new ArrayList<>();
    private String activeProvider = null;

    private Map<String, Float> providerAccuracy = new HashMap<>();
    private Location lastKnownLocation;

    void initProvider(Context context, String provider,
                      LocationTracker receiver) throws IllegalStateException {
        this.context = context;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            throw new IllegalStateException("Location permission not granted.");
        }

        android.location.LocationManager manager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(provider, REFRESH_DELAY, REFRESH_DISTANCE, receiver);

        registerProvider(provider);
    }

    void registerProvider(String provider) {
        availableProviders.add(provider);
        providerAccuracy.put(provider, -1.f);

        Log.i("HuntItApp/ProviderTracker", "Registered " + provider + " location provider.");
    }

    void unregisterProvider(String provider) {
        availableProviders.remove(provider);
        providerAccuracy.remove(provider);

        Log.i("HuntItApp/ProviderTracker", "Unregistered " + provider + " location provider.");

        if (provider.equals(this.activeProvider)) {
            this.activeProvider = chooseBestProvider();
        }
    }

    private String chooseBestProvider() {
        String oldBest = this.activeProvider;
        String newBest = oldBest;
        for (String provider : availableProviders) {
            if (newBest == null) newBest = provider;

            try {
                Float accuracy = providerAccuracy.get(provider);
                if (accuracy >= 0.f && (providerAccuracy.get(newBest) < 0.f || accuracy < providerAccuracy.get(newBest))) {
                    newBest = provider;
                }
            } catch (NullPointerException ex) {
                Log.e("HuntItApp/ProviderTracker", Arrays.toString(ex.getStackTrace()));
            }
        }
        if (context != null && newBest != null && !newBest.equals(activeProvider)) {
            Log.i("HuntItApp/ProviderTracker", "Switched from " + oldBest + " to " + newBest + " location provider.");
            Toast.makeText(context, "Switched to " + newBest + " location provider.", Toast.LENGTH_SHORT).show();
        }
        return newBest;
    }

    void reportAccuracy(String reportingProvider, float providerAccuracy) {
        this.providerAccuracy.put(reportingProvider, providerAccuracy);
        Log.i("HuntItApp/ProviderTracker", reportingProvider + " provider is accurate to within " + providerAccuracy + "m");

        this.activeProvider = chooseBestProvider();
        Log.i("HuntItApp/ProviderTracker", this.activeProvider + " provider is active.");
    }

    void reportLocation(String reportingProvider, Location location) {
        if (activeProvider == null || activeProvider.equals(reportingProvider)) {
            this.lastKnownLocation = location;
            this.activeProvider = reportingProvider;
        }
    }

    @Nullable
    Location getLastKnownLocation() {
        return lastKnownLocation;
    }
}
