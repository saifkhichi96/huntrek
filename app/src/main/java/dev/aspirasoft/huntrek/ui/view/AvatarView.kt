package dev.aspirasoft.huntrek.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import dev.aspirasoft.huntrek.R

class AvatarView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val mXPCounter: ProgressBar
    private val mAvatarImage: ImageView
    private val mUserLevelView: TextView
    private val mUserNameView: TextView

    init {
        inflate(context, R.layout.view_avatar, this)
        mXPCounter = findViewById(R.id.xp_counter)
        mAvatarImage = findViewById(R.id.avatar_image)
        mUserLevelView = findViewById(R.id.user_level)
        mUserNameView = findViewById(R.id.user_name)
    }

    fun setAvatar(drawable: Drawable?) {
        mAvatarImage.setImageDrawable(drawable)
    }

    fun setUserName(name: String?) {
        mUserNameView.text = name
    }

    fun setLevel(level: Int) {
        mUserLevelView.text = level.toString()
    }

    fun setXP(currentXP: Int) {
        mXPCounter.max = 100
        mXPCounter.progress = currentXP
    }
}