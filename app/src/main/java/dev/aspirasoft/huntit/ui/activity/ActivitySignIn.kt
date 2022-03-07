package dev.aspirasoft.huntit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import dev.aspirasoft.huntit.databinding.ActivitySignInBinding
import dev.aspirasoft.huntit.model.GameCharacterInfo
import kotlinx.coroutines.launch

class ActivitySignIn : FullScreenActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var repo: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AuthRepository()
        binding.signInButton.setOnClickListener { onSignInClicked() }
        updateUI(STATE_INITIALIZED)
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in and update UI accordingly.
        when (val currentUser = repo.currentUser) {
            null -> updateUI(STATE_INITIALIZED)
            else -> updateUI(STATE_SIGN_IN_SUCCESS, currentUser)
        }
    }

    private fun onSignInClicked() {
        // Get email and password from the UI
        val email = binding.emailField.text.toString()
        val password = binding.passwordField.text.toString()

        // Validate that the email and password are not empty
        if (TextUtils.isEmpty(email)) {
            binding.emailField.error = getString(R.string.error_field_required)
            binding.emailField.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordField.error = getString(R.string.error_field_required)
            binding.passwordField.requestFocus()
            return
        }

        // Show a progress spinner, and kick off a background task to
        updateUI(STATE_AWAITING_RESPONSE)
        lifecycleScope.launch {
            when (val signedInUser = repo.signIn(email, password)) {
                null -> updateUI(STATE_SIGN_IN_FAILED)
                else -> updateUI(STATE_SIGN_IN_SUCCESS, signedInUser)
            }
        }
    }

    private fun updateUI(uiState: Int, user: GameCharacterInfo? = null) {
        when (uiState) {
            STATE_INITIALIZED -> {
                binding.signInProgress.visibility = View.GONE
                binding.signInButton.text = getString(R.string.label_sign_in)
            }
            STATE_AWAITING_RESPONSE -> {
                binding.signInProgress.visibility = View.VISIBLE
                binding.signInButton.text = ""
            }
            STATE_SIGN_IN_FAILED -> {
                binding.signInProgress.visibility = View.GONE
                binding.signInButton.text = getString(R.string.label_sign_in)
                Toast.makeText(this, R.string.error_sign_in_failed, Toast.LENGTH_SHORT).show()
            }
            STATE_SIGN_IN_SUCCESS -> {
                if (user != null) onSignedIn(user)
            }
        }
    }

    private fun onSignedIn(user: GameCharacterInfo) {
        startActivity(Intent(applicationContext, ActivityHunt::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    companion object {
        private const val STATE_INITIALIZED = 1
        private const val STATE_AWAITING_RESPONSE = 2
        private const val STATE_SIGN_IN_FAILED = 3
        private const val STATE_SIGN_IN_SUCCESS = 4
    }

}