package dev.aspirasoft.huntrek.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class FullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }

}