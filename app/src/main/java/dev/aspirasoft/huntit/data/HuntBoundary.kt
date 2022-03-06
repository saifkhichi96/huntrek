package dev.aspirasoft.huntit.data

import android.graphics.PointF
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * The GPS boundary of a [Hunt].
 *
 * A boundary is a rectangular area defined by four GPS points which are the corners
 * of the rectangle. A [Hunt] is played within this boundary.
 */
object HuntBoundary {
    val POINT_A = PointF(49.490f, 7.625f)
    val POINT_B = PointF(49.355f, 7.625f)
    val POINT_C = PointF(49.355f, 7.870f)
    val POINT_D = PointF(49.490f, 7.870f)
    val CENTER = LatLng(49.444388, 7.766365)
}