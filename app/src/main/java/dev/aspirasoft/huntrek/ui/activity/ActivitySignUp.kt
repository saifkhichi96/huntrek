package dev.aspirasoft.huntrek.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dev.aspirasoft.huntrek.R
import dev.aspirasoft.huntrek.data.repo.AuthRepository
import dev.aspirasoft.huntrek.data.repo.AuthRepository.RegistrationCallback
import dev.aspirasoft.huntrek.model.GameCharacterState

class ActivitySignUp : FullScreenActivity(), View.OnClickListener, RegistrationCallback {

    private var mNameInputView: TextInputEditText? = null
    private var mEmailInputView: TextInputEditText? = null
    private var mWaitingViews: ViewGroup? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Assign views
        mWaitingViews = findViewById(R.id.waitingViews)
        mNameInputView = findViewById(R.id.user_name)
        mEmailInputView = findViewById(R.id.userEmail)

        // Assign click listeners
        findViewById<View>(R.id.buttonSubmit).setOnClickListener(this)
        findViewById<View>(R.id.buttonFacebook).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSubmit -> submitDetails()
            R.id.buttonFacebook -> {
                // todo: fetch user details from Facebook
            }
        }
    }

    private fun submitDetails() {
        val name = mNameInputView!!.text.toString().trim { it <= ' ' }
        val email = mEmailInputView!!.text.toString().trim { it <= ' ' }
        if (validateName(name) && validateEmail(email)) {
            registerUser(name, email)
        }
    }

    private fun registerUser(name: String, email: String) {
        try {
            mWaitingViews!!.visibility = View.VISIBLE
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val user = GameCharacterState()
            user.firebaseId = userId
            user.name = name
            user.email = email
            AuthRepository.signUp(userId, user, this)
        } catch (ex: NullPointerException) {
            mWaitingViews!!.visibility = View.GONE
            Log.e(dev.aspirasoft.huntrek.HuntItApp.TAG, "No firebase user active. Cannot complete sign up.")
            ex.printStackTrace()
        }
    }

    private fun validateName(name: String): Boolean {
        return name.isNotEmpty()
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty()
    }

    override fun onRegistrationComplete(user: GameCharacterState) {
        mWaitingViews!!.visibility = View.GONE
        AuthRepository.signIn(user)
        startActivity(Intent(applicationContext, ActivityHunt::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

}