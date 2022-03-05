package dev.aspirasoft.huntrek.listener

import com.mapbox.mapboxsdk.maps.MapboxMap

interface MapListener {
    fun onMapReady(map: MapboxMap?)
}