package dev.aspirasoft.huntit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dev.aspirasoft.huntit.HuntItApp
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import dev.aspirasoft.huntit.model.GameCharacterInfo
import dev.aspirasoft.huntit.model.characters.CharacterType

class ActivitySignUp : FullScreenActivity(), View.OnClickListener, AuthRepository.RegistrationCallback {

    private lateinit var repo: AuthRepository

    private var mNameInputView: TextInputEditText? = null
    private var mEmailInputView: TextInputEditText? = null
    private var mWaitingViews: ViewGroup? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        repo = AuthRepository()

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
            val user = GameCharacterInfo(
                id = userId,
                name = name,
                email = email,
                type = CharacterType.BARMAN
            )
            repo.signUp(userId, user, this)
        } catch (ex: NullPointerException) {
            mWaitingViews!!.visibility = View.GONE
            Log.e(HuntItApp.TAG, "No firebase user active. Cannot complete sign up.")
            ex.printStackTrace()
        }
    }

    private fun validateName(name: String): Boolean {
        return name.isNotEmpty()
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty()
    }

    override fun onRegistrationComplete(user: GameCharacterInfo) {
        mWaitingViews!!.visibility = View.GONE
        repo.saveSignIn(user)

        val intent = Intent(this@ActivitySignUp, ActivityHunt::class.java)
        startActivity(intent)
        finish()
    }

}