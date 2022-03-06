package dev.aspirasoft.huntit.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.firebase.auth.FirebaseAuth
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import dev.aspirasoft.huntit.utils.CameraPermissionUtil
import dev.aspirasoft.huntit.utils.PermissionUtil
import java.util.*


class ActivitySplash : FullScreenActivity() {

    private var mUserRequestedInstall: Boolean = false
    private var mSession: Session? = null
    private val mGameLaunchTask = GameLaunchTask()

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        // ARCore requires camera permission to operate.
        if (!CameraPermissionUtil.hasCameraPermission(this)) {
            CameraPermissionUtil.requestCameraPermission(this)
            return
        }

        // Ensure that Google Play Services for AR and ARCore device profile data are
        // installed and up to date.
        try {
            if (mSession == null) {
                when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        // Success: Safe to create the AR session.
                        mSession = Session(this)
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
                        mUserRequestedInstall = false
                    }
                    else -> {}
                }
            } else {
                when {
                    PermissionUtil.checkAllGranted(this, permissions) -> mGameLaunchTask.startDelayed()
                    else -> ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE)
                }
            }
        } catch (ex: UnavailableUserDeclinedInstallationException) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception $ex", Toast.LENGTH_LONG)
                .show()
            return
        } catch (ex: Exception) {
            return  // mSession remains null, since session creation has failed.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> when {
                PermissionUtil.checkAllGranted(this, permissions) -> mGameLaunchTask.startDelayed()
                else -> finish() // todo: permission denied. handle this situation
            }
        }

        if (!CameraPermissionUtil.hasCameraPermission(this)) {
            Toast.makeText(
                this,
                "Camera permission is needed to run this application",
                Toast.LENGTH_LONG
            ).show()
            if (!CameraPermissionUtil.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionUtil.launchPermissionSettings(this)
            }
            finish()
        }
    }

    private enum class AuthStatus {
        SIGNED_IN,
        SIGNED_OUT,
        SIGNUP_INCOMPLETE
    }

    private inner class GameLaunchTask() : TimerTask() {

        private fun proceed(status: AuthStatus) {
            startActivity(Intent(applicationContext, when (status) {
                AuthStatus.SIGNED_IN -> ActivityHunt::class.java
                AuthStatus.SIGNUP_INCOMPLETE -> ActivitySignUp::class.java
                else -> ActivitySignIn::class.java
            }))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        override fun run() {
            when (AuthRepository.isSignedIn) {
                true -> proceed(AuthStatus.SIGNED_IN)
                else -> when (FirebaseAuth.getInstance().currentUser) {
                    null -> proceed(AuthStatus.SIGNED_OUT)
                    else -> proceed(AuthStatus.SIGNUP_INCOMPLETE)
                }
            }
        }

        fun startDelayed() {
            Timer().schedule(this, 1500L)
        }

    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 200
    }

}