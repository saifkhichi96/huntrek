package dev.aspirasoft.huntrek.ui.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.ProgressBar
import java.util.*

/**
 * Created by saifkhichi96 on 24/12/2017.
 */
class AnnotatedProgressBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ProgressBar(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val mLoadHandler = Handler()
    private val mRandomGenerator = Random()

    private var finishing = false

    fun start(context: Context?) {
        progress = 0
        val initialDelay = 250 + mRandomGenerator.nextInt(250)
        mLoadHandler.postDelayed(ProgressUpdater(), initialDelay.toLong())
    }

    fun finish() {
        finishing = true
    }

    val isFinished: Boolean
        get() = progress == max

    private inner class ProgressUpdater : Thread() {
        override fun run() {
            // Advance one step at every iteration. Cap at 90% progress.
            val currentProgress = progress
            if (!finishing && currentProgress < 0.90 * max) {
                val newProgress = (currentProgress + 0.01 * max).toInt()
                progress = newProgress

                // Schedule next iteration
                val delay = 250 + mRandomGenerator.nextInt(250)
                mLoadHandler.postDelayed(this, delay.toLong())
            } else if (finishing && !isFinished) {
                val newProgress = (currentProgress + 0.01 * max).toInt()
                progress = newProgress

                // Schedule next iteration
                val delay = 25
                mLoadHandler.postDelayed(this, delay.toLong())
            }
        }
    }

}