package dev.aspirasoft.huntit.data

import android.content.Context
import dev.aspirasoft.huntit.listener.LocationListener


/**
 * An asynchronous task that listens for location updates.
 *
 * Periodically, this class queries the [LocationProvider] for the most recent known
 * location, and notifies the [listener] of the new location. This class is a [Thread]
 * which should be started by the client when location updates are required.
 *
 * @constructor Creates a new instance of the [LocationReceiver] class.
 * @param context The application context.
 * @param listener The listener to be notified when the location changes.
 * @throws IllegalArgumentException if no location providers are available (i.e. location services turned off).
 */
class LocationReceiver(context: Context, private val listener: LocationListener) : Thread() {

    /**
     * The location provider.
     */
    private val locationProvider = dev.aspirasoft.huntit.data.LocationProvider(context)

    /**
     * Is the location listener running?
     *
     * Call [LocationReceiver.stopTracking] to stop the listener.
     * @see [run]
     */
    var isTracking = true
        private set

    /**
     * Periodically queries the location provider for new location updates.
     */
    override fun run() {
        while (isTracking) {
            // Get last known location from provider manager
            locationProvider.lastKnownLocation?.let {
                listener.onLocationUpdated(it)
            }

            // Sleep for a while
            try {
                sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Stops the location listener.
     */
    fun stopTracking() {
        isTracking = false
    }

}