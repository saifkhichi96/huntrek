package dev.aspirasoft.huntit.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import dev.aspirasoft.huntit.model.AuthStatus
import dev.aspirasoft.huntit.utils.CameraPermissionUtil
import dev.aspirasoft.huntit.utils.PermissionUtil
import java.util.*


class ActivitySplash : FullScreenActivity() {

    private lateinit var repo: AuthRepository

    private val mGameLaunchTask = GameLaunchTask()

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        repo = AuthRepository()
    }

    override fun onResume() {
        super.onResume()
        // ARCoreUtil.checkAndInstall(this)
        when (PermissionUtil.checkAllGranted(this, permissions)) {
            true -> mGameLaunchTask.startDelayed()
            false -> ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> when (PermissionUtil.checkAllGranted(this, permissions)) {
                true -> mGameLaunchTask.startDelayed()
                false -> finish() // todo: permission denied. handle this situation
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

    private fun onLocationPermissionDenied() {
        Toast.makeText(this,
            "Location permission is needed to run this application",
            Toast.LENGTH_LONG).show()
        if (!CameraPermissionUtil.shouldShowRequestPermissionRationale(this)) {
            // Permission denied with checking "Do not ask again".
            CameraPermissionUtil.launchPermissionSettings(this)
        }
        finish()
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this,
            "Camera permission not granted",
            Toast.LENGTH_LONG).show()
        if (!CameraPermissionUtil.shouldShowRequestPermissionRationale(this)) {
            // Permission denied with checking "Do not ask again".
            CameraPermissionUtil.launchPermissionSettings(this)
        }
        finish()
    }

    private inner class GameLaunchTask : TimerTask() {

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
            when (repo.isSignedIn) {
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