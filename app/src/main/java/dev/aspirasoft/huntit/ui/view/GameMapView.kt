package dev.aspirasoft.huntit.ui.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import dev.aspirasoft.huntit.BuildConfig
import dev.aspirasoft.huntit.listener.MapListener

class GameMapView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : MapView(context, attrs, defStyleAttr),
    OnMapReadyCallback {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var mapListener: MapListener? = null

    private val boundary: PolylineOptions
        get() = PolylineOptions()
            .add(LatLng(dev.aspirasoft.huntit.data.HuntBoundary.POINT_A.x.toDouble(),
                dev.aspirasoft.huntit.data.HuntBoundary.POINT_A.y.toDouble()))
            .add(LatLng(dev.aspirasoft.huntit.data.HuntBoundary.POINT_B.x.toDouble(),
                dev.aspirasoft.huntit.data.HuntBoundary.POINT_B.y.toDouble()))
            .add(LatLng(dev.aspirasoft.huntit.data.HuntBoundary.POINT_C.x.toDouble(),
                dev.aspirasoft.huntit.data.HuntBoundary.POINT_C.y.toDouble()))
            .add(LatLng(dev.aspirasoft.huntit.data.HuntBoundary.POINT_D.x.toDouble(),
                dev.aspirasoft.huntit.data.HuntBoundary.POINT_D.y.toDouble()))
            .add(LatLng(dev.aspirasoft.huntit.data.HuntBoundary.POINT_A.x.toDouble(),
                dev.aspirasoft.huntit.data.HuntBoundary.POINT_A.y.toDouble()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getMapAsync(this)
    }

    override fun onMapReady(mMapboxMap: MapboxMap) {
        mMapboxMap.uiSettings.setAllGesturesEnabled(false)
        mMapboxMap.uiSettings.isCompassEnabled = false
        val DEBUG_MODE: Boolean = BuildConfig.DEBUG
        if (DEBUG_MODE) {
            mMapboxMap.uiSettings.isRotateGesturesEnabled = true
            mMapboxMap.uiSettings.isZoomGesturesEnabled = true
            mMapboxMap.setMinZoomPreference(14.0)
            mMapboxMap.setMaxZoomPreference(19.0)
        }
        mMapboxMap.addPolyline(boundary)
        mapListener?.onMapReady(mMapboxMap)
    }

    fun setGameMapListener(mapListener: MapListener?) {
        this.mapListener = mapListener
    }
}