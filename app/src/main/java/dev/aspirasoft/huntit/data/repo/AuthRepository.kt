package dev.aspirasoft.huntit.data.repo

import android.util.Log
import dev.aspirasoft.huntit.HuntItApp
import dev.aspirasoft.huntit.data.source.AuthDataSource
import dev.aspirasoft.huntit.data.source.DataSource
import dev.aspirasoft.huntit.model.GameCharacterInfo

/**
 * Created by saifkhichi96 on 03/01/2018.
 */
class AuthRepository {

    private val dataSource = AuthDataSource()

    private val cache: DataSource = DataSource

    val currentUser: GameCharacterInfo?
        get() = cache.get("activeUser", GameCharacterInfo::class.java)

    val isSignedIn: Boolean
        get() {
            val signedIn = cache.get("signedIn", Boolean::class.java, false) ?: false
            val userExists = currentUser != null
            return signedIn && userExists
        }

    fun saveSignIn(user: GameCharacterInfo) {
        cache.put("activeUser", user)
        cache.put("signedIn", true)
    }

    suspend fun signIn(email: String, password: String): GameCharacterInfo? {
        val userInfo = dataSource.signIn(email, password)
        if (userInfo != null) saveSignIn(userInfo)
        return userInfo
    }

    fun signOut() {
        dataSource.signOut()
        cache.remove("activeUser")
        cache.put("signedIn", false)
    }

    fun signUp(userId: String, user: GameCharacterInfo, callback: RegistrationCallback) {
        cache.remoteDb.child("users").child(userId).setValue(user)
            .addOnSuccessListener { callback.onRegistrationComplete(user) }
            .addOnFailureListener {
                Log.e(HuntItApp.TAG, it.message ?: "User registration failed.")
            }
    }

    fun setScore(score: Int) {
        if (currentUser != null) {
            cache.remoteDb
                .child("users")
                .child(currentUser!!.id)
                .child("score")
                .setValue(score)
        }
    }

    interface RegistrationCallback {
        fun onRegistrationComplete(user: GameCharacterInfo)
    }

}