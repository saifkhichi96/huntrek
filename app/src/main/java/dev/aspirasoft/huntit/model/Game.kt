package dev.aspirasoft.huntit.model

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Vibrator
import com.mapbox.mapboxsdk.geometry.LatLng
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.TreasureRepository.all
import dev.aspirasoft.huntit.ui.view.GameCharacterView
import sfllhkhan95.game.FrameRefreshListener
import sfllhkhan95.game.GameCore
import sfllhkhan95.game.GameOverListener
import sfllhkhan95.game.GamePauseListener
import kotlin.math.roundToInt


/**
 * The main game class.
 *
 * @constructor Creates a new game instance.
 */
class Game(
    private val context: Activity,
    currentUser: GameCharacterInfo,
) : GameCore(), FrameRefreshListener, GamePauseListener, GameOverListener {

    /**
     * The game player.
     */
    val player: GameCharacter

    init {
        // Create the player
        val playerView: GameCharacterView = context.findViewById(R.id.game_player)
        player = GameCharacter(context, playerView, currentUser)
    }

    /**
     * Has the game started?
     */
    var isStarted = false
        private set

    /**
     * The skybox.
     */
    private val skyBox = SkyBox(context)

    /**
     * Starts the game and initializes the game environment.
     */
    override fun start() {
        // Initialize game environment
        // skyBox.initialize()

        // Register game listeners
        setPauseListener(this)
        setRefreshListener(this)
        setOverListener(this)

        // Start the game loop
        super.start()
        isStarted = true
    }

    override fun onUpdate(time: Long) {
        checkForNearbyTreasure()
        player.update(time)
        skyBox.update(time)
    }

    override fun onPaused() {
        player.hide()
    }

    override fun onResume() {
        player.show()
    }

    override fun onOver() {

    }

    private fun checkForNearbyTreasure() {
        for (chest in all) {
            val myLocation = player.position
            val chestLocation = LatLng(chest.latitude, chest.longitude)
            if (distanceBetween(myLocation, chestLocation) < 50) {
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                v?.vibrate(500)
            }
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

}