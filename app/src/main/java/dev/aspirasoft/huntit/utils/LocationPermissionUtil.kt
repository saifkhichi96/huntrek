/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.aspirasoft.huntit.utils

import android.Manifest
import android.app.Activity
import android.content.Context

/**
 * Helper to ask location permission.
 */
object LocationPermissionUtil {

    const val LOCATION_PERMISSION_CODE = 1
    private val LOCATION_PERMISSION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Does the app have permission to access the device location?
     *
     * @param context the context
     * @return true if it has permission, false otherwise
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return PermissionsUtil.hasPermissions(context, LOCATION_PERMISSION)
    }

    /**
     * Request location permissions.
     */
    fun askLocationPermissions(activity: Activity) {
        PermissionsUtil.requestPermissions(activity, LOCATION_PERMISSION, LOCATION_PERMISSION_CODE)
    }

    /**
     * Check to see if we need to show the rationale for location permissions.
     */
    fun shouldShowLocationRationale(activity: Activity): Boolean {
        return PermissionsUtil.shouldShowRationale(activity, LOCATION_PERMISSION[0])
    }

}