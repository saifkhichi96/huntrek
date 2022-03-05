package dev.aspirasoft.huntrek.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import dev.aspirasoft.huntrek.R
import dev.aspirasoft.huntrek.model.GameCharacterState

/**
 * Created by saifkhichi96 on 04/01/2018.
 */
class DialogUserDetails(context: Context, themeResId: Int) : Dialog(context, themeResId), View.OnClickListener {

    private var gameCharacterState: GameCharacterState? = null
    private var mCharacterImage: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_user_details)
        findViewById<View>(R.id.dismissButton).setOnClickListener(this)
        updateUI()
    }

    fun updateUI() {
        if (gameCharacterState != null) {
            val mUserNameView = findViewById<TextView>(R.id.user_name)
            mUserNameView.text = gameCharacterState!!.name
            val mUserEmailView = findViewById<TextView>(R.id.userEmail)
            mUserEmailView.text = gameCharacterState!!.email
            val mChestCountView = findViewById<TextView>(R.id.chestsOpened)
            mChestCountView.text = gameCharacterState!!.getChestsOpened().toString()
            val mCoinCountView = findViewById<TextView>(R.id.coinsCollected)
            mCoinCountView.text = gameCharacterState!!.score.toString()
            val totalXP = gameCharacterState!!.checkTotalXP()
            val targetXP = 100
            val userXP = totalXP % targetXP
            val level = totalXP / targetXP + 1
            val mUserXPView = findViewById<TextView>(R.id.userXP)
            mUserXPView.text = userXP.toString()
            val mTargetXPView = findViewById<TextView>(R.id.targetXP)
            mTargetXPView.text = " / $targetXP XP"
            val mUserLevelView = findViewById<TextView>(R.id.user_level)
            mUserLevelView.text = level.toString()
            val mTotalXPView = findViewById<TextView>(R.id.totalXP)
            mTotalXPView.text = totalXP.toString()
            val mUserProgress = findViewById<ProgressBar>(R.id.userProgress)
            mUserProgress.max = targetXP
            mUserProgress.progress = userXP
        }
        if (mCharacterImage != null) {
            val mCharacterImage = findViewById<ImageView>(R.id.characterImage)
            mCharacterImage.setImageDrawable(this.mCharacterImage)
        }
    }

    fun setUser(gameCharacterState: GameCharacterState?) {
        this.gameCharacterState = gameCharacterState
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dismissButton -> dismiss()
        }
    }

    fun setCharacterImage(characterRes: Drawable?) {
        mCharacterImage = characterRes
    }

}