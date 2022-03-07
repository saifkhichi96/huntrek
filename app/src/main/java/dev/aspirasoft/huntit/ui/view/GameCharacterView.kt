package dev.aspirasoft.huntit.ui.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import dev.aspirasoft.huntit.model.characters.*

class GameCharacterView : SimpleDraweeView {

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    /**
     * The character's sprite.
     *
     * By default, we use the BARMAN sprite.
     * @see [CharacterSprite]
     */
    private var sprite: CharacterSprite = createCharacterSprite(CharacterType.BARMAN)

    /**
     * Is the character walking?
     */
    private var isWalking = false

    val characterDrawable: Drawable?
        get() = sprite.getFullDrawable(context)

    val faceDrawable: Drawable?
        get() = sprite.getFaceDrawable(context)

    /**
     * Creates a new [CharacterSprite] based on the [type] of the character.
     *
     * @param type The type of the character.
     */
    fun createCharacterSprite(type: CharacterType): CharacterSprite {
        this.sprite = when (type) {
            CharacterType.BARMAN -> Barkeep()
            CharacterType.BARMAID -> Barmaid()
            CharacterType.INDONESIAN_KID -> IndonesianKid()
        }

        this.controller = Fresco.newDraweeControllerBuilder()
            .setControllerListener(AnimationController())
            .build()

        setImageURI(sprite.walkingAnimUri)  // fixme: replace deprecated method
        return sprite
    }

    /**
     * Starts the walking animation.
     */
    fun startWalking() {
        if (!isWalking) {
            this.controller?.animatable?.start()
            isWalking = true
        }
    }

    /**
     * Stops the walking animation.
     */
    fun stopWalking() {
        if (isWalking) {
            this.controller?.animatable?.stop()
            isWalking = false
        }
    }

    private inner class AnimationController : BaseControllerListener<ImageInfo?>() {
        override fun onFinalImageSet(id: String, imageInfo: ImageInfo?, anim: Animatable?) {
            anim?.stop()
        }
    }

}