package dev.aspirasoft.huntrek.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dev.aspirasoft.huntrek.data.repo.AuthRepository
import dev.aspirasoft.huntrek.data.source.DataSource
import dev.aspirasoft.huntrek.model.GameCharacterState
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.concurrent.TimeUnit

class ActivitySignIn : FullScreenActivity(), View.OnClickListener {

    private var mVerificationInProgress = false
    private var mVerificationId: String? = null

    private var mResendToken: ForceResendingToken? = null

    /**
     * Callbacks for phone authentication.
     */
    private val mCallbacks: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        /**
         * For instance verification or auto-retrieval of verification code.
         *
         * This callback will be invoked in two situations:
         * 1.   Instant verification. In some cases the phone number can be instantly
         *      verified without needing to send or enter a verification code.
         * 2.   Auto-retrieval. On some devices Google Play services can automatically
         *      detect the incoming verification SMS and perform verification without
         *      user action.
         */
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            mVerificationInProgress = false
            updateUI(STATE_VERIFY_SUCCESS, null)
            signInWithPhoneAuthCredential(credential)
        }

        /**
         * This callback is invoked in an invalid request for verification is made,
         * for instance if the phone number format is not valid.
         */
        override fun onVerificationFailed(ex: FirebaseException) {
            mVerificationInProgress = false
            when (ex) {
                is FirebaseAuthInvalidCredentialsException -> {
                    fieldPhoneNumber.error = "Invalid phone number."
                }
                is FirebaseTooManyRequestsException -> {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show()
                }
                else -> Snackbar.make(findViewById(android.R.id.content),
                    ex.message ?: "Unknown error.", Snackbar.LENGTH_LONG).show()
            }

            // Show a message and update the UI
            updateUI(STATE_VERIFY_FAILED)
        }

        /**
         *  The SMS verification code has been sent to the provided phone number, we
         *  now need to ask the user to enter the code and then construct a credential
         *  by combining the code with a verification ID.
         */
        override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId
            mResendToken = token

            // Update UI
            updateUI(STATE_CODE_SENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Restore instance state
        savedInstanceState?.let { onRestoreInstanceState(it) }

        // Assign click listeners
        buttonStartVerification.setOnClickListener(this)
        buttonVerifyPhone.setOnClickListener(this)
        linkResend.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in and update UI accordingly.
        val currentUser = AuthRepository.currentFirebaseUser
        if (currentUser == null) {
            updateUI(STATE_INITIALIZED)
        } else {
            AuthRepository.isRegistered(currentUser.uid, object : AuthRepository.RegistrationQueryCallback {
                override fun onResponseReceived(isRegistered: Boolean) {
                    if (isRegistered) {
                        updateUI(STATE_SIGN_IN_SUCCESS, null)
                    } else {
                        updateUI(STATE_INITIALIZED)
                    }
                }
            })
        }
        // Resume phone number validation (if previously started)
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(fieldPhoneNumber!!.text.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonStartVerification -> {
                if (!validatePhoneNumber()) return
                startPhoneNumberVerification(fieldPhoneNumber!!.text.toString())
            }
            R.id.buttonVerifyPhone -> {
                val code = fieldVerificationCode!!.text.toString()
                if (TextUtils.isEmpty(code)) {
                    fieldVerificationCode!!.error = "Cannot be empty."
                    return
                }
                verifyPhoneNumberWithCode(mVerificationId, code)
            }
            R.id.linkResend -> resendVerificationCode(fieldPhoneNumber!!.text.toString(), mResendToken)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        updateUI(STATE_AWAITING_RESPONSE)
        val auth = FirebaseAuth.getInstance()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
            .build()
        auth.useAppLanguage()
        PhoneAuthProvider.verifyPhoneNumber(options)

        mVerificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken?) {
        updateUI(STATE_AWAITING_RESPONSE)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks,  // OnVerificationStateChangedCallbacks
            token) // ForceResendingToken from callbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        updateUI(STATE_AWAITING_RESPONSE)
        AuthRepository.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                // Sign in success, update UI with the signed-in user's information
                if (task.isSuccessful) {
                    val user = task.result!!.user
                    updateUI(STATE_SIGN_IN_SUCCESS, user)
                }

                // Sign in failed, display a message and update the UI
                else {
                    // The verification code entered was invalid
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        fieldVerificationCode!!.error = "Invalid code."
                    }

                    // Update UI
                    updateUI(STATE_SIGN_IN_FAILED)
                }
            }
    }

    private fun updateUI(uiState: Int, user: FirebaseUser? = AuthRepository.currentFirebaseUser) {
        when (uiState) {
            STATE_INITIALIZED -> {
                loginViews!!.visibility = View.VISIBLE
                waitingViews!!.visibility = View.GONE
                verificationViews!!.visibility = View.GONE
            }
            STATE_AWAITING_RESPONSE -> {
                loginViews!!.visibility = View.GONE
                waitingViews!!.visibility = View.VISIBLE
                verificationViews!!.visibility = View.GONE
            }
            STATE_CODE_SENT -> {
                loginViews!!.visibility = View.GONE
                waitingViews!!.visibility = View.GONE
                verificationViews!!.visibility = View.VISIBLE
            }
            STATE_VERIFY_FAILED -> {
                loginViews!!.visibility = View.GONE
                waitingViews!!.visibility = View.GONE
                verificationViews!!.visibility = View.VISIBLE
            }
            STATE_VERIFY_SUCCESS -> {
                val currentUser = AuthRepository.currentFirebaseUser
                if (currentUser != null) {
                    AuthRepository.isRegistered(currentUser.uid, object : AuthRepository.RegistrationQueryCallback {
                        override fun onResponseReceived(isRegistered: Boolean) {
                            if (isRegistered) {
                                updateUI(STATE_SIGN_IN_SUCCESS)
                            } else {
                                startActivity(Intent(applicationContext, ActivitySignUp::class.java))
                                overridePendingTransition(0, 0)
                                finish()
                            }
                        }
                    })
                }
            }
            STATE_SIGN_IN_FAILED -> {
                loginViews!!.visibility = View.VISIBLE
                waitingViews!!.visibility = View.GONE
                verificationViews!!.visibility = View.GONE
            }
            STATE_SIGN_IN_SUCCESS -> {
                if (user != null) {
                    // Signed in
                    DataSource.remoteDb.child("users").child(user.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                dataSnapshot.getValue(GameCharacterState::class.java)?.let { user ->
                                    AuthRepository.signIn(user)
                                    onSignedIn()
                                } ?: AuthRepository.signOut()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                AuthRepository.signOut()
                            }
                        })
                }
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = fieldPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            fieldPhoneNumber.error = "Invalid phone number."
            return false
        }

        return true
    }

    private fun onSignedIn() {
        startActivity(Intent(applicationContext, ActivityHunt::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    companion object {
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_AWAITING_RESPONSE = 2
        private const val STATE_CODE_SENT = 3
        private const val STATE_VERIFY_FAILED = 4
        private const val STATE_VERIFY_SUCCESS = 5
        private const val STATE_SIGN_IN_FAILED = 6
        private const val STATE_SIGN_IN_SUCCESS = 7
    }

}