package dev.aspirasoft.huntrek.data

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat.checkSelfPermission
import dev.aspirasoft.huntrek.data.LocationProvider.LocationProvider

/**
 * Manages available location providers and provides access to them.
 *
 * There are two types of location providers:
 *  - [LocationManager.GPS_PROVIDER]
 *  - [LocationManager.NETWORK_PROVIDER]
 *
 * This class is responsible for tracking which location providers are available and which are
 * not. It also provides a method to request the last known location of each provider. It is
 * also responsible for requesting the location updates from the location providers.
 *
 * @constructor Creates a new [LocationProvider] instance.
 * @param context The application context.
 */
class LocationProvider(context: Context) {

    init {
        // Get location service
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // At least one provider must be available
        val isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        check(isGPSAvailable || isNetworkAvailable) { "No location provider available." }

        // Subscribe to all available location providers
        if (isGPSAvailable) registerProvider(context, LocationManager.GPS_PROVIDER)
        if (isNetworkAvailable) registerProvider(context, LocationManager.NETWORK_PROVIDER)
    }

    /**
     * List of available location providers.
     */
    private val knownProviders = HashMap<String, LocationProvider>()

    /**
     * Currently active location provider.
     */
    private var currentProvider: String? = null

    /**
     * The last known device location.
     *
     * If an active provider is available, this is the last known location of that provider.
     * Otherwise, this is the last known location of the first available provider. If no providers
     * are available, or none of them have a last known location, this is `null`.
     */
    val lastKnownLocation: Location?
        get() {
            return when (currentProvider) {
                null -> knownProviders[currentProvider]?.lastKnownLocation
                else -> knownProviders.values.firstOrNull { it.lastKnownLocation != null }?.lastKnownLocation
            }
        }

    /**
     * Registers a new location provider.
     *
     * @param context The application context.
     * @param provider The location provider to register.
     * @throws IllegalArgumentException if location permission is not granted.
     */
    @Throws(IllegalStateException::class)
    private fun registerProvider(context: Context, provider: String) {
        // Are location permissions granted? If not, throw an exception.
        val fineLocationPermission = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_DENIED
        val coarseLocationPermission = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
        check(!fineLocationPermission || !coarseLocationPermission) { "Location permission not granted." }

        // Is the provider already registered? If so, throw an exception.
        check(!knownProviders.containsKey(provider)) { "Location provider already registered." }

        // Register the provider with the LocationManager.
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager.requestLocationUpdates(provider,
            dev.aspirasoft.huntrek.data.LocationProvider.Companion.REFRESH_DELAY,
            dev.aspirasoft.huntrek.data.LocationProvider.Companion.REFRESH_DISTANCE, object : LocationListener {
                /**
                 * Called when the location has changed.
                 *
                 * @param location The new location, as a [Location].
                 */
                @CallSuper
                override fun onLocationChanged(location: Location) {
                    this@LocationProvider.onLocationChanged(provider, location)
                }

                /**
                 * Called when a provider this listener is registered with becomes enabled.
                 *
                 * @param provider The name of the location provider.
                 */
                @CallSuper
                override fun onProviderEnabled(provider: String) {
                    this@LocationProvider.enableProvider(provider)
                }

                /**
                 * Called when a provider this listener is registered with becomes disabled.
                 *
                 * @param provider The name of the location provider.
                 */
                @CallSuper
                override fun onProviderDisabled(provider: String) {
                    this@LocationProvider.disableProvider(provider)
                }
            })

        // Add the provider to the list of known providers.
        knownProviders[provider] = LocationProvider(provider)
    }

    /**
     * Enables a location provider.
     *
     * @param provider The location provider to enable.
     * @throws IllegalStateException If the provider is not registered.
     */
    @Throws(IllegalStateException::class)
    private fun enableProvider(provider: String) {
        try {
            knownProviders[provider]!!.enabled = true
            Log.i(dev.aspirasoft.huntrek.data.LocationProvider.Companion.TAG, "Enabled $provider location provider.")
        } catch (ex: Exception) {
            throw IllegalStateException("Provider $provider is not available.")
        }
    }

    /**
     * Disables a location provider.
     *
     * @param provider The location provider to disable.
     */
    private fun disableProvider(provider: String) {
        knownProviders[provider]?.enabled = false
        Log.i(dev.aspirasoft.huntrek.data.LocationProvider.Companion.TAG, "Disabled $provider location provider.")

        // If this was the active provider, switch to the next best available provider.
        if (provider == currentProvider) autoSwitchProviders()
    }

    /**
     * Called when the location of a provider has changed.
     *
     * @param provider The location provider whose location has changed.
     * @param location The new location.
     */
    fun onLocationChanged(provider: String, location: Location) {
        knownProviders[provider]?.lastKnownLocation = location
        knownProviders[provider]?.accuracy = location.accuracy
        autoSwitchProviders() // auto-switch to the most accurate provider

        // If auto-switch failed to select a provider, set reporting provider as active provider
        if (currentProvider == null) currentProvider = provider
    }

    /**
     * Automatically switches to the next available location provider.
     *
     * Chooses the most accurate provider from the list of known location providers
     * that are enabled, and sets it as the active location provider.
     */
    private fun autoSwitchProviders() {
        // Get names of all enabled providers
        val enabledProviders = knownProviders.values.filter { it.enabled }

        // Get the most accurate provider
        val bestProvider = enabledProviders.maxWithOrNull { a, b ->
            when {
                a.accuracy < b.accuracy -> -1
                a.accuracy > b.accuracy -> 1
                else -> 0
            }
        }

        if (bestProvider != null && bestProvider.name != currentProvider) {
            Log.i(dev.aspirasoft.huntrek.data.LocationProvider.Companion.TAG,
                "Switched from $currentProvider to ${bestProvider.name} location provider.")
        }

        currentProvider = bestProvider?.name
    }

    companion object {
        private const val REFRESH_DISTANCE = 0.1f // Update location on 1m distance change
        private const val REFRESH_DELAY = 500L // Update location every 500ms

        private const val TAG = "LocationProviderTracker"
    }

    /**
     * Data class for storing the location provider.
     */
    data class LocationProvider(
        val name: String,
        var accuracy: Float = -1F,
        var enabled: Boolean = true,
        var lastKnownLocation: Location? = null,
    )

}