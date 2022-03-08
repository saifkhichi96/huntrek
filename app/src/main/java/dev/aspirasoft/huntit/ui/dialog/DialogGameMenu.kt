package dev.aspirasoft.huntit.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.ui.activity.ActivityHunt

/**
 * Created by saifkhichi96 on 04/01/2018.
 */
class DialogGameMenu(context: Context, themeResId: Int) : Dialog(context, themeResId), View.OnClickListener {

    private var gameActivity: ActivityHunt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_game_menu)

        // Assign click listeners
        findViewById<View>(R.id.dismissButton).setOnClickListener(this)
        findViewById<View>(R.id.button_sign_out).setOnClickListener(this)
        findViewById<View>(R.id.pauseButton).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dismissButton -> dismiss()
            R.id.button_sign_out -> {
                if (gameActivity != null) gameActivity!!.signOut()
                dismiss()
            }
            R.id.pauseButton -> {
                if (gameActivity != null) gameActivity!!.toggleOverview(v)
                dismiss()
            }
        }
    }

    fun setGameActivity(gameActivity: ActivityHunt?) {
        this.gameActivity = gameActivity
    }

}