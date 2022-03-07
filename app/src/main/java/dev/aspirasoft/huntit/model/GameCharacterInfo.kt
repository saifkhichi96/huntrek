package dev.aspirasoft.huntit.model

import com.google.firebase.database.FirebaseDatabase
import dev.aspirasoft.huntit.model.characters.CharacterType
import java.io.Serializable
import kotlin.math.ceil

/**
 * A class that represents detailed information about a game character.
 */
data class GameCharacterInfo(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var type: CharacterType = CharacterType.BARMAN,
) : Serializable {

    /**
     * Total score of the user in the game.
     *
     * This is the sum of experience points obtained at each level.
     */
    var totalXP = 0
        set(value) {
            field = value
            updateProperty("totalXP", value)
        }

    /**
     * Number of treasures found by the user.
     */
    var treasuresFound = 0
        set(value) {
            field = value
            updateProperty("treasuresFound", value)
        }

    /**
     * Target experience points to reach the next level.
     *
     * This is a constant value. The user advances to the next level when they reach this value,
     * and their current XP is reset to 0.
     */
    private val targetXP: Int
        get() = 100

    /**
     * Experience points obtained from the current level.
     *
     * Computed as the remainder of the total experience points divided by the target experience points.
     * For example, if the user has 120 XP, and the target XP is 100, the current level XP is 20, and
     * the user is on level 2.
     */
    val currentXP: Int
        get() = totalXP % targetXP

    /**
     * Current game level of the user.
     *
     * Computed as the ceiling of the total experience points divided by the target experience points.
     * For example, if the user has 120 XP, and the target XP is 100, the user is on level 2.
     */
    val level: Int
        get() = ceil(((totalXP + 1.0) / targetXP)).toInt()

    private fun updateProperty(property: String, value: Any) {
        if (id.isNotEmpty()) {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(id)
                .child(property)
                .setValue(value)
        }
    }

}