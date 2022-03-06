package dev.aspirasoft.huntit.ui.activity

import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.unity3d.player.UnityPlayer
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.data.repo.AuthRepository
import kotlinx.android.synthetic.main.activity_collect.*

class ActivityCollect : FullScreenActivity(), View.OnClickListener {

    private lateinit var mUnityPlayer: UnityPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)

        // Check that user is logged in
        if (!AuthRepository.isSignedIn) {
            finish()
            return
        }

        // Assign click listeners
        settingsButton.setOnClickListener(this)

        // Configure unity player
        mUnityPlayer = UnityPlayer(this)
        val unityPlayerView = findViewById<FrameLayout>(R.id.unityPlayerView)
        unityPlayerView.addView(mUnityPlayer, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        mUnityPlayer.requestFocus()

        // Display 'loading' screen
        pauseScreen.visibility = View.VISIBLE

        // Schedule 'loading' screen to auto-hide after 3.5 seconds
        Handler().postDelayed({ runOnUiThread { pauseScreen.visibility = View.GONE } }, 3500L)
    }

    /**
     * To support deep linking, we need to make sure that the client can get access to
     * the last sent intent. The clients access this through a JNI api that allows them
     * to get the intent set on launch. To update that after launch we have to manually
     * replace the intent with the one caught here.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        mUnityPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mUnityPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        mUnityPlayer.resume()
    }

    override fun onStop() {
        super.onStop()
        mUnityPlayer.stop()
    }

    override fun onDestroy() {
        mUnityPlayer.quit()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mUnityPlayer.lowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory()
        }
    }

    /**
     *  This ensures the layout will be correct.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mUnityPlayer.configurationChanged(newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mUnityPlayer.windowFocusChanged(hasFocus)
    }

    /**
     * For some reason the multiple keyevent type is not supported by the ndk.
     * Force event injection by overriding dispatchKeyEvent().
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_MULTIPLE) mUnityPlayer.injectEvent(event) else super.dispatchKeyEvent(
            event)
    }

    /**
     * Pass any events not handled by (unfocused) views straight to UnityPlayer
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return mUnityPlayer.injectEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return mUnityPlayer.injectEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mUnityPlayer.injectEvent(event)
    }

    /**
     * For API12
     */
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return mUnityPlayer.injectEvent(event)
    }

    override fun onClick(v: View) {
        if (v == settingsButton) {
            v.visibility = View.GONE
            UnityPlayer.UnitySendMessage("Treasure Chest", "Open", "open")
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setUri("res:///" + R.drawable.fireworks)
                .setAutoPlayAnimations(true)
                .build()
            val mFallingCoinsView: SimpleDraweeView = findViewById(R.id.falling_coins)
            mFallingCoinsView.controller = controller
            val mHandler = Handler()
            mHandler.postDelayed({ runOnUiThread { mFallingCoinsView.visibility = View.VISIBLE } }, 1500L)
            mHandler.postDelayed({
                runOnUiThread {
                    val i = intent
                    val coins = i.getIntExtra("ChestValue", 0)
                    val coinsView = findViewById<TextView>(R.id.coins_collected)
                    coinsView.text = "Coins: $coins"
                    coinsView.visibility = View.VISIBLE
                }
            }, 2000L)
            mHandler.postDelayed({
                runOnUiThread {
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }, 4000L)
        }
    }

    fun escape(view: View?) {
        finish()
        overridePendingTransition(0, 0)
    }

}