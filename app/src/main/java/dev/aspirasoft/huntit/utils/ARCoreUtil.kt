package dev.aspirasoft.huntit.utils

import android.app.Activity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.FatalException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

object ARCoreUtil {

    /**
     * Check if ARCore is supported on this device.
     *
     * @param activity The activity to use to check ARCore support.
     */
    fun checkAndInstall(activity: Activity) {
        // TODO: Finish implementing this method.
        // ARCore requires camera permission to operate.
        if (!CameraPermissionUtil.hasCameraPermission(activity)) {
            if (!CameraPermissionUtil.shouldShowCameraRationale(activity)) {
                // Permission has not been requested before, request permission.
                CameraPermissionUtil.requestCameraPermission(activity)
            } else {
                // TODO: Permission has been requested before, show an explanation.
                // PermissionsUtil.showAppSettingsDialog(activity)
            }
            return
        }

        // Check for ARCore installation and request installation if necessary. Note, however, that
        // our app can still be launched if the ARCore APK is not installed. In that case, we will
        // use a lower-quality experience (i.e. an in-app 3D scene instead of a real-world experience)
        try {
            // Is ARCore supported and installed?
            val arcore = ArCoreApk.getInstance()
            val requestInstall = when (arcore.checkAvailability(activity)) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> false
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED,
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
                -> true
                else -> false
            }

            if (requestInstall) {
                when (arcore.requestInstall(activity, false)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        // Success: Safe to create the AR session.
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. This activity will be paused.
                        // 2. The user is prompted to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. This activity is resumed. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                    }
                    else -> {}
                }
            }
        } catch (ex: UnavailableUserDeclinedInstallationException) {
            // user previously declined installation
        } catch (ex: FatalException) {
            // error while checking compatibility or starting installation
        } catch (ex: UnavailableDeviceNotCompatibleException) {
            // not supported on this device.
        } catch (ex: Exception) {
            // other exception
        }
    }

}