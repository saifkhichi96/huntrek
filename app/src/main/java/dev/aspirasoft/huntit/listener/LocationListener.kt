package dev.aspirasoft.huntit.listener

import android.location.Location

/**
 * A callback interface to receive location updates.
 */
interface LocationListener {

    /**
     * Called when a new location is available.
     *
     * @param location The new location, as a Location object.
     */
    fun onLocationUpdated(location: Location)

}