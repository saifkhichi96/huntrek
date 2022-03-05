package dev.aspirasoft.huntrek.model

import android.content.Context
import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import dev.aspirasoft.huntrek.data.LocationProvider


/**
 * A character that can be located on the map.
 *
 * The character's location is provided by the [LocationProvider], which uses either
 * the device's GPS or the network location. The latest location is only queries from the
 * [LocationProvider] when the [location] property is accessed.
 *
 * @constructor Creates a new [LocatableCharacter] instance.
 * @param context The application context.
 */
open class LocatableCharacter(context: Context) {

    /**
     * The location provider used to query the character's location.
     */
    private val locationProvider = dev.aspirasoft.huntrek.data.LocationProvider(context)

    /**
     * The character's current location.
     */
    private val location: Location?
        get() = locationProvider.lastKnownLocation

    /**
     * The character's current location as a [LatLng].
     */
    val position: LatLng
        get() = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

    /**
     * The bearing of the character's current location.
     */
    val direction: Float
        get() = location?.bearing ?: 0f

    /**
     * The speed with which the character is moving in m/s, based on changes in its location.
     */
    val speed: Float
        get() = location?.speed ?: 0f

    /**
     * Is the character stationary?
     *
     * A character is considered stationary if its speed is less than or equal to 0.5 m/s.
     *
     * @return True if the character is stationary, false otherwise.
     */
    val isStationary: Boolean
        get() = speed <= 0.05

    /**
     * Is the character walking?
     *
     * A character is considered walking if its speed is greater than 0.05 m/s and less than or
     * equal to 1.5 m/s (i.e. 0.18-5.4 km/h).
     *
     * @return True if the character is walking, false otherwise.
     */
    val isWalking: Boolean
        get() = speed > 0.05 && speed <= 1.5

    /**
     * Is the character running?
     *
     * A character is considered running if its speed is greater than 1.5 m/s and less than or
     * equal to 2.75 m/s (i.e. 5.4-9.9 km/h).
     *
     * @return True if the character is running, false otherwise.
     */
    val isRunning: Boolean
        get() = speed > 1.5 && speed <= 2.75

    /**
     * Is the character on a bike?
     *
     * A character is considered on a bike if its speed is greater than 2.75 m/s and less than or
     * equal to 5.0 m/s (i.e. 9.9-18.0 km/h).
     *
     * @return True if the character is on a bike, false otherwise.
     */
    val isOnBike: Boolean
        get() = speed > 2.75 && speed <= 5.0

    /**
     * Is the character on a car?
     *
     * A character is considered on a car if its speed is greater than 5.0 m/s (i.e. 18.0+ km/h).
     */
    val isDriving: Boolean
        get() = speed > 5.0

}