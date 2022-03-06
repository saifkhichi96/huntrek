package dev.aspirasoft.huntit.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionUtil {

    fun checkIsGranted(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkAllGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!checkIsGranted(context, permission)) return false
        }

        return true
    }

    fun checkOneGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (checkIsGranted(context, permission)) return true
        }

        return false
    }

}