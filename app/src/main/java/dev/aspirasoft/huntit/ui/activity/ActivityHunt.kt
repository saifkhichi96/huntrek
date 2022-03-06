package dev.aspirasoft.huntit.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import dev.aspirasoft.huntit.HuntItApp
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import dev.aspirasoft.huntit.data.repo.AuthRepository.signIn
import dev.aspirasoft.huntit.data.repo.TreasureRepository.all
import dev.aspirasoft.huntit.data.repo.TreasureRepository.get
import dev.aspirasoft.huntit.data.repo.TreasureRepository.pop
import dev.aspirasoft.huntit.data.repo.TreasureRepository.size
import dev.aspirasoft.huntit.listener.TimeListener
import dev.aspirasoft.huntit.model.Game
import dev.aspirasoft.huntit.model.collectibles.TreasureChest
import dev.aspirasoft.huntit.ui.dialog.DialogGameMenu
import dev.aspirasoft.huntit.ui.dialog.DialogUserDetails
import dev.aspirasoft.huntit.ui.view.AvatarView
import dev.aspirasoft.huntit.ui.view.GameStartingView
import dev.aspirasoft.huntit.utils.SpawnUtil.checkDaySinceLastSpawn
import dev.aspirasoft.huntit.utils.SpawnUtil.spawnTreasureChests
import sfllhkhan95.game.GameStartListener
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class ActivityHunt : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback, OnMarkerClickListener,
    TimeListener {

    private lateinit var mGame: Game
    private lateinit var mGameMap: MapboxMap
    private lateinit var mMapView: MapView

    private var mPositionMarker: MarkerOptions? = null
    private var mLastLocation: LatLng? = null

    private lateinit var mDialogGameMenu: DialogGameMenu
    private lateinit var mDialogUserDetails: DialogUserDetails

    private lateinit var mGameStartingView: GameStartingView
    private lateinit var mPlayerStatsView: AvatarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_hunt)

        // Check that user is logged in
        val currentUser = AuthRepository.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, ActivitySignIn::class.java))
            finish()
            return
        }

        // Create the game
        mGame = Game(this, currentUser)
        mGame.setStartListener(GameInitializer())

        // Get saved user location (if any)
        mLastLocation = savedInstanceState?.getParcelable("lastLocation")

        // Get UI elements
        mGameStartingView = findViewById(R.id.loadingScreen)
        mPlayerStatsView = findViewById(R.id.avatar)

        // Display the game starting view while the game is loading
        mGameStartingView.start()

        // Assign click listeners
        findViewById<View>(R.id.avatar).setOnClickListener(this)
        findViewById<View>(R.id.settingsButton).setOnClickListener(this)
        findViewById<View>(R.id.button_sign_out).setOnClickListener(this)

        // Configure the Mapbox map
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_token))
        mMapView = findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        // Create dialogs
        mDialogGameMenu = DialogGameMenu(this, R.style.DialogTheme)
        mDialogGameMenu.setGameActivity(this)

        mDialogUserDetails = DialogUserDetails(this, R.style.DialogTheme)
        mDialogUserDetails.setUser(currentUser)
    }

    override fun onNewIntent(intent: Intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onMapReady(mMapboxMap: MapboxMap) {
        mGameMap = mMapboxMap

        // Register click handlers
        mGameMap.setOnMarkerClickListener(this)

        // Start the game
        mGame.start()

        // Display user details
        mDialogUserDetails.setCharacterImage(mGame.player.view.characterDrawable)
        showChests(R.drawable.marker_treasure_chest)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.title.startsWith("Chest #")) {
            val distance = distanceBetween(mGame.player.position, marker.position).toFloat()
            if (!mGame.isPaused && distance <= TreasureChest.RANGE) {
                val id = Integer.valueOf(marker.title.split("#".toRegex()).toTypedArray()[1])
                val chest = get(id)
                if (chest != null) {
                    val i = Intent(applicationContext, ActivityCollect::class.java)
                    i.putExtra("ChestValue", chest.value)
                    collectTreasure(id)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                }
            } else {
                AlertDialog.Builder(this@ActivityHunt)
                    .setMessage("You must be within " + TreasureChest.RANGE +
                            "m of the treasure to collect it. Current distance is " +
                            distance + "m.")
                    .create()
                    .show()
            }
            return true
        } else if (marker.title == "You are here!") {
            marker.showInfoWindow(mGameMap, mMapView)
            return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        if (::mMapView.isInitialized) {
            mMapView.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<View>(R.id.pauseScreen).visibility = View.VISIBLE
        if (::mMapView.isInitialized) {
            mMapView.onResume()
        }
    }

    override fun onPause() {
        if (::mMapView.isInitialized) {
            mMapView.onPause()
        }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        // Save the user's current game state
        outState.putParcelable("lastLocation", mGame.player.position)
        if (::mMapView.isInitialized) {
            mMapView.onSaveInstanceState(outState)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (::mMapView.isInitialized) {
            mMapView.onLowMemory()
        }
    }

    override fun onDestroy() {
        if (::mMapView.isInitialized) {
            mMapView.onDestroy()
        }
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.avatar -> {
                mDialogUserDetails.show()
                mDialogUserDetails.updateUI()
            }
            R.id.settingsButton -> mDialogGameMenu.show()
            R.id.button_sign_out -> {
                AuthRepository.signOut()
                startActivity(Intent(applicationContext, ActivitySignIn::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }

    fun signOut() {
        AuthRepository.signOut()
        startActivity(Intent(applicationContext, ActivitySignIn::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    fun toggleOverview(view: View) {
        if (!mGame.isPaused) pause() else resume()
    }

    private fun pause() {
        findViewById<View>(R.id.pauseScreen).visibility = View.VISIBLE
        Handler().postDelayed({ findViewById<View>(R.id.pauseScreen).visibility = View.GONE }, 1500)
        for (marker in mGameMap.markers) {
            mGameMap.removeMarker(marker)
        }
        showChests(R.drawable.marker_target)
        mPositionMarker = MarkerOptions()
            .position(mGame.player.position)
            .title("You are here!")
            .icon(IconFactory.getInstance(this@ActivityHunt).fromResource(R.drawable.marker_player))
        mPositionMarker?.let { mGameMap.addMarker(it) }
        mGame.pause()
    }

    private fun showChests(resId: Int) {
        for (chest in all) {
            val latLng = LatLng(chest.latitude, chest.longitude)
            val marker = MarkerOptions()
                .position(latLng)
                .title("Chest #" + chest.id)
                .icon(IconFactory.getInstance(this@ActivityHunt).fromResource(resId))
            mGameMap.addMarker(marker)
        }
    }

    private fun resume() {
        findViewById<View>(R.id.pauseScreen).visibility = View.VISIBLE
        Handler().postDelayed({ findViewById<View>(R.id.pauseScreen).visibility = View.GONE }, 2500)
        for (marker in mGameMap.markers) {
            mGameMap.removeMarker(marker)
        }
        if (mPositionMarker != null) {
            mGameMap.removeMarker(mPositionMarker!!.marker)
        }
        showChests(R.drawable.marker_treasure_chest)
        mGame.resume()
    }

    private fun end() {
        mGame.end()
    }

    private fun collectTreasure(id: Int) {
        val chest = pop(id)
        if (chest != null) {
            Log.i(HuntItApp.TAG, "Received " + chest.value + " coins from chest # " + id)
            mGame.player.addPoints(chest.value)
            mGame.player.info.chestsOpened++
        } else {
            Log.e(HuntItApp.TAG, "Could not read chest # $id")
        }
    }

    private fun distanceBetween(a: LatLng?, b: LatLng): Int {
        val distance = FloatArray(1)
        Location.distanceBetween(
            a!!.latitude, a.longitude,
            b.latitude, b.longitude,
            distance)
        return distance[0].roundToInt()
    }

    override fun onBackPressed() {
        if (!mGame.isOver && mGame.isPaused) {
            resume()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDayBreak() {
        mMapView.getMapAsync {
            it.setStyle(getString(R.string.mapbox_style_night)) {
                runOnUiThread {
                    val bg = ContextCompat.getColor(applicationContext, R.color.colorNight)
                    findViewById<View>(R.id.skybox).setBackgroundColor(bg)
                }
            }
        }
    }

    override fun onNightArrived() {
        mMapView.getMapAsync {
            it.setStyle(getString(R.string.mapbox_style_day)) {
                runOnUiThread {
                    val bg = ContextCompat.getColor(applicationContext, R.color.colorDawn)
                    findViewById<View>(R.id.skybox).setBackgroundColor(bg)
                }
            }
        }
    }

    private inner class GameInitializer : GameStartListener {

        override fun onStart() = runOnUiThread {
            initialize()
            loop()
        }

        @UiThread
        private fun initialize() {
            // Display user's avatar and name
            mPlayerStatsView.setAvatar(mGame.player.view.faceDrawable)
            mPlayerStatsView.setUserName(mGame.player.info.name)
            updateUserStats()
            signIn(mGame.player.info)
        }

        @UiThread
        private fun loop() {
            update()

            if (!mGame.isOver) {
                Handler().postDelayed({ runOnUiThread(::update) }, 100)
            }
        }

        @UiThread
        private fun update() {
            // Hide the loading screen when the game is ready
            if (!mGameStartingView.isFinished) {
                mGameStartingView.finish()
            }

            updateUserStats()

            // If there are no treasure chests or a day has passed since last spawn, spawn new chests
            if (size == 0 || checkDaySinceLastSpawn()) {
                spawnTreasureChests()
                showChests(if (mGame.isPaused) R.drawable.marker_target else R.drawable.marker_treasure_chest)
            }

            // Update UI elements
            findViewById<View>(R.id.skybox).visibility = if (mGame.isPaused) View.GONE else View.VISIBLE
            findViewById<View>(R.id.pause_button).visibility = if (mGame.isPaused) View.VISIBLE else View.GONE
            findViewById<View>(R.id.avatar).visibility = if (mGame.isPaused) View.GONE else View.VISIBLE
            findViewById<View>(R.id.settingsButton).visibility = if (mGame.isPaused) View.GONE else View.VISIBLE

            // Show the correct game state
            when (mGame.isPaused) {
                true -> showPaused()
                false -> showGame()
            }
        }

        private fun getPlayerLocation(): LatLng {
            // Query current location from GPS
            var position = mGame.player.position

            // If no GPS location is available, use the last known location
            if (position.latitude == 0.0 && position.longitude == 0.0 && mLastLocation != null) {
                position = mLastLocation!!
                mLastLocation = null
            }

            return position
        }

        private fun showGame() {
            findViewById<View>(R.id.pauseScreen).visibility = View.GONE

            mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                .target(getPlayerLocation())
                .zoom(18.5)
                .tilt(60.0)
                .bearing(mGame.player.direction.toDouble())
                .build()))
        }

        private fun showPaused() {
            mGameMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                .target(dev.aspirasoft.huntit.data.HuntBoundary.CENTER)
                .zoom(14.0)
                .tilt(0.0)
                .build()))
        }

        private fun updateUserStats() {
            mPlayerStatsView.setXP(mGame.player.info.checkCurrentXP())
            mPlayerStatsView.setLevel(mGame.player.info.checkLevel())
        }
    }

}