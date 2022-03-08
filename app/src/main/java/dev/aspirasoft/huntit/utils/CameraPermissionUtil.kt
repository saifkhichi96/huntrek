package dev.aspirasoft.huntit.utils

import android.Manifest
import android.app.Activity
import android.content.Context

/**
 * Helper to ask camera permission.
 */
object CameraPermissionUtil {

    const val CAMERA_PERMISSION_CODE = 0
    private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    /**
     * Does the app have permission to access the device camera?
     *
     * @param context the context
     * @return true if it has permission, false otherwise
     */
    fun hasCameraPermission(context: Context): Boolean {
        return PermissionsUtil.hasPermission(context, CAMERA_PERMISSION)
    }

    /**
     * Request camera permission.
     */
    fun requestCameraPermission(activity: Activity) {
        PermissionsUtil.requestPermission(activity, CAMERA_PERMISSION, CAMERA_PERMISSION_CODE)
    }

    /**
     * Check to see if we need to show the rationale for camera permission.
     */
    fun shouldShowCameraRationale(activity: Activity): Boolean {
        return PermissionsUtil.shouldShowRationale(activity, CAMERA_PERMISSION)
    }

}