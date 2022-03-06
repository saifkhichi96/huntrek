package dev.aspirasoft.huntit.model

import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable
import kotlin.math.ceil

/**
 * User is an entity class which represents a user of the application.
 *
 * @author saifkhichi96
 */
class GameCharacterState : Serializable {

    var firebaseId: String? = null

    var name: String? = null

    var email: String? = null

    var score = 0
        set(score) {
            field = score
            if (firebaseId != null) {
                FirebaseDatabase.getInstance().reference.child("users").child(firebaseId!!).child("score")
                    .setValue(score)
            }
        }

    var chestsOpened = 0
        set(chestsOpened) {
            field = chestsOpened
            if (firebaseId != null) {
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(firebaseId!!)
                    .child("chestsOpened")
                    .setValue(chestsOpened)
            }
        }

    var characterType = 0

    fun checkTotalXP(): Int {
        return chestsOpened * 5
    }

    fun checkCurrentXP(): Int {
        return checkTotalXP() % 100
    }

    fun checkLevel(): Int {
        return ceil(checkTotalXP() / 100.0).toInt()
    }
}