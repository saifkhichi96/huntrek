package dev.aspirasoft.huntit.data.source

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.huntit.model.GameCharacterInfo
import kotlinx.coroutines.tasks.await


/**
 * A data store from where authentication related data is retrieved.
 */
class AuthDataSource {

    private val auth = Firebase.auth
    private val db = FirebaseDatabase.getInstance()

    suspend fun signIn(email: String, password: String): GameCharacterInfo? {
        return auth.signInWithEmailAndPassword(email, password).await()
            .user?.uid?.let { userId ->
                db.getReference("users/$userId")
                    .get().await()
                    .getValue(GameCharacterInfo::class.java)
            }
    }

    suspend fun signUp(email: String, password: String, userData: GameCharacterInfo): String? {
        val userId = auth.createUserWithEmailAndPassword(email, password).await()?.user?.uid
        db.getReference("users/$userId").setValue(userData)
        return userId
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

}