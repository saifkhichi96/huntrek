package dev.aspirasoft.huntit.listener

import com.mapbox.mapboxsdk.maps.MapboxMap

interface MapListener {
    fun onMapReady(map: MapboxMap?)
}