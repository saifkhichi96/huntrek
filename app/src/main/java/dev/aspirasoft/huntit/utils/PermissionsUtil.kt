package dev.aspirasoft.huntit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

object PermissionsUtil {

    /**
     * Checks if the app has the given permission.
     *
     * @param context the context
     * @param permission the permission to check
     * @return true if the app has the permission, false otherwise
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the app has all the given permissions.
     *
     * @param context the context
     * @param permissions the permissions to check
     * @return true if the app has all the permissions, false otherwise
     */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!hasPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * Checks if the app has at least one of the given permissions.
     *
     * @param context the context
     * @param permissions the permissions to check
     * @return true if the app has at least one of the permissions, false otherwise
     */
    fun hasSomePermission(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (hasPermission(context, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * Requests the given permission.
     *
     * @param activity the activity
     * @param permission the permission to request
     * @param requestCode the request code
     */
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        requestPermissions(activity, arrayOf(permission), requestCode)
    }

    /**
     * Requests all the given permissions.
     *
     * @param activity the activity
     * @param permissions the permissions to request
     * @param requestCode the request code
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * Check to see if we need to show the rationale for a permission.
     *
     * Sometimes, if the user denied a permission, we want to show them a dialog explaining
     * why the permission is needed. This method checks to see if we need to show the rationale
     * for a permission. It will return true if we need to show the rationale, false otherwise.
     *
     * When it returns true, the caller should show a dialog to the user explaining why this
     * permission is needed. The caller should then redirect the user to the app settings to
     * manually grant the permission. This can be done by calling the [showAppSettingsDialog]
     * method.
     *
     * @param activity the activity
     * @param permission the permission to check
     * @return true if the permission rationale should be shown, false otherwise
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Launch the App Settings screen.
     *
     * This will open the App Settings screen where the user can manually grant permissions.
     * We need this when the user has previously denied a permission, and we need to get the
     * permission again after explaining to the user why the permission is needed.
     *
     * @param activity the activity
     */
    fun showAppSettingsDialog(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

}