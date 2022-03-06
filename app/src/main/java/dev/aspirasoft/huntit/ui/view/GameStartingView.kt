package dev.aspirasoft.huntit.ui.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import dev.aspirasoft.huntit.R

/**
 * Created by saifkhichi96 on 24/12/2017.
 */
class GameStartingView : RelativeLayout {

    private var coverImage: ImageView? = null
    private var messageView: TextView? = null
    private var annotatedProgressBar: AnnotatedProgressBar? = null

    var isFinished = false
        private set

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.screen_starting, this)
        coverImage = findViewById(R.id.cover)
        messageView = findViewById(R.id.message)
        annotatedProgressBar = findViewById(R.id.progress)
    }

    fun start() {
        annotatedProgressBar!!.start(context)
        Handler().postDelayed(Carousel(), 100)
    }

    fun finish() {
        if (!isFinished) {
            isFinished = true
            annotatedProgressBar!!.finish()
        }
    }

    private inner class Carousel : Thread() {
        var coverIndex = 0
        var messageIndex = 0
        private val coverImages = intArrayOf(
            R.drawable.bg_starting_2,
            R.drawable.bg_starting_1,
            R.drawable.bg_starting_3,
            R.drawable.bg_starting_4
        )
        private val messages = arrayOf(
            "Do not trespass while playing.",
            "Stay alert at all times.",
            "Do not drive and play.",
            "Beware of your surroundings."
        )

        override fun run() {
            if (!annotatedProgressBar!!.isFinished) {
                // Update cover image
                coverIndex++
                if (coverIndex >= coverImages.size) coverIndex = 0
                coverImage!!.setImageResource(coverImages[coverIndex])

                // Update message text
                messageIndex++
                if (messageIndex >= messages.size) messageIndex = 0
                messageView!!.text = messages[messageIndex]

                // Wait for 1 second
                Handler().postDelayed(this, 3500)
            } else {
                visibility = GONE
            }
        }
    }
}