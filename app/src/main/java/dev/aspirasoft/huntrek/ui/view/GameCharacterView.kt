package dev.aspirasoft.huntrek.ui.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import dev.aspirasoft.huntrek.R
import dev.aspirasoft.huntrek.model.characters.Barkeep
import dev.aspirasoft.huntrek.model.characters.Barmaid
import dev.aspirasoft.huntrek.model.characters.CharacterSprite
import dev.aspirasoft.huntrek.model.characters.IndonesianKid

class GameCharacterView : SimpleDraweeView {

    private var sprite: CharacterSprite? = null
    private var characterType = 0
    private var isWalking = false

    constructor(context: Context?) : super(context) {
        init(0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GamePlayerView,
            0, 0)
        characterType = try {
            a.getInteger(R.styleable.GamePlayerView_character_type, 0)
        } finally {
            a.recycle()
        }
        init(characterType)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GamePlayerView,
            0, 0)
        characterType = try {
            a.getInteger(R.styleable.GamePlayerView_character_type, 0)
        } finally {
            a.recycle()
        }
        init(characterType)
    }

    fun setCharacterType(characterType: Int) {
        this.characterType = characterType
        if (this.characterType >= 3) {
            this.characterType = 0
        }
        sprite = when (this.characterType) {
            0 -> Barkeep()
            1 -> Barmaid()
            2 -> IndonesianKid()
            else -> Barkeep()
        }
        this.controller = Fresco.newDraweeControllerBuilder()
            .setControllerListener(AnimationController())
            .build()
        this.setImageURI(sprite!!.walkingAnimUri)
    }

    fun init(characterType: Int) {
        isWalking = false
        setCharacterType(characterType)
    }

    fun startWalking() {
        if (!isWalking) {
            if (this.controller != null && this.controller!!.animatable != null) {
                this.controller!!.animatable.start()
                isWalking = true
            }
        }
    }

    fun stopWalking() {
        if (isWalking) {
            if (this.controller != null && this.controller!!.animatable != null) {
                this.controller!!.animatable.stop()
                isWalking = false
            }
        }
    }

    val characterDrawable: Drawable?
        get() = sprite!!.getFullDrawable(context)

    val faceDrawable: Drawable?
        get() = sprite!!.getFaceDrawable(context)

    private inner class AnimationController : BaseControllerListener<ImageInfo?>() {
        override fun onFinalImageSet(
            id: String, imageInfo: ImageInfo?,
            anim: Animatable?,
        ) {
            anim?.stop()
        }
    }

}