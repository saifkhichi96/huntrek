package dev.aspirasoft.huntit.model

import android.content.Context
import android.view.View
import dev.aspirasoft.huntit.ui.view.GameCharacterView

/**
 * The game character.
 *
 * This class is the main game character. It is responsible for handling the
 * character's state and the character's view. It also handles the character's
 * movement and the character's actions.
 *
 * The character's view is a [GameCharacterView] object. It is responsible for drawing
 * the character on the screen. The character's state is a [GameCharacterState] object.
 * It is responsible for handling the character's state.
 *
 * The character's movement depends on its GPS location, which is handled by the
 * [LocatableCharacter] class, which is a superclass of this class.
 *
 * @constructor Creates a new game character.
 * @param context The application context.
 * @param view The character view.
 * @param info The character info.
 */
class GameCharacter(
    context: Context,
    val view: GameCharacterView,
    val info: GameCharacterState,
) : LocatableCharacter(context) {

    init {
        view.setCharacterType(info.characterType)
    }

    /**
     * Updates the character's view.
     *
     * @param time The elapsed time since the last update.
     */
    fun update(time: Long) {
        when (this.isStationary) {
            true -> view.stopWalking()
            false -> view.startWalking()
        }
    }

    /**
     * Show the player's view.
     */
    fun show() {
        view.visibility = View.VISIBLE
    }

    /**
     * Hide the player's view.
     */
    fun hide() {
        view.visibility = View.GONE
    }

    /**
     * Add some points to the player's score.
     */
    fun addPoints(points: Int) {
        info.score += points
    }

}