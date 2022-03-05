package dev.aspirasoft.huntrek.model

import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable

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

    private var chestsOpened = 0

    var characterType = 0

    fun getChestsOpened(): Int {
        return chestsOpened
    }

    fun setChestsOpened(chestsOpened: Int) {
        this.chestsOpened = chestsOpened
        if (firebaseId != null) {
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseId!!).child("chestsOpened")
                .setValue(chestsOpened)
        }
    }

    fun checkTotalXP(): Int {
        return chestsOpened * 5
    }

    fun checkCurrentXP(): Int {
        return checkTotalXP() % 100
    }

    fun checkLevel(): Int {
        return Math.ceil(checkTotalXP() / 100.0).toInt()
    }
}