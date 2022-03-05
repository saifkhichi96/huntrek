package dev.aspirasoft.huntrek.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dev.aspirasoft.huntrek.data.source.DataSource
import dev.aspirasoft.huntrek.model.GameCharacterState

/**
 * Created by saifkhichi96 on 03/01/2018.
 */
object AuthRepository {

    private val dataSource: DataSource = DataSource

    val firebaseAuth = FirebaseAuth.getInstance()

    val currentUser: GameCharacterState?
        get() = dataSource.get("activeUser", GameCharacterState::class.java)

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isSignedIn: Boolean
        get() {
            val signedIn = dataSource.get("signedIn", Boolean::class.java, false) ?: false
            val userExists = currentUser != null && currentFirebaseUser != null
            return signedIn.and(userExists)
        }

    fun isRegistered(userId: String, callback: RegistrationQueryCallback) {
        dataSource.remoteDb.child("users").orderByKey().equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    callback.onResponseReceived(dataSnapshot.value != null)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    fun signIn(user: GameCharacterState) {
        dataSource.put("activeUser", user)
        dataSource.put("signedIn", true)
    }

    fun signOut() {
        firebaseAuth.signOut()
        dataSource.remove("activeUser")
        dataSource.put("signedIn", false)
    }

    fun signUp(userId: String, user: GameCharacterState, callback: RegistrationCallback) {
        dataSource.remoteDb.child("users").child(userId).setValue(user)
            .addOnSuccessListener { callback.onRegistrationComplete(user) }
            .addOnFailureListener {
                Log.e(dev.aspirasoft.huntrek.HuntItApp.TAG,
                    it.message ?: "User registration failed.")
            }
    }

    fun setScore(score: Int) {
        if (currentUser != null) {
            dataSource.remoteDb
                .child("users")
                .child(currentUser?.firebaseId!!)
                .child("score")
                .setValue(score)
        }
    }

    interface RegistrationCallback {
        fun onRegistrationComplete(user: GameCharacterState)
    }

    interface RegistrationQueryCallback {
        fun onResponseReceived(isRegistered: Boolean)
    }

}