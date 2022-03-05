package dev.aspirasoft.huntrek.ui.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import dev.aspirasoft.huntrek.R

/**
 * Created by saifkhichi96 on 25/12/2017.
 */
class LoadingAnimation(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        inflate(context, R.layout.screen_loading, this)
        val controllerListener: ControllerListener<ImageInfo> = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(
                id: String,
                imageInfo: ImageInfo?,
                anim: Animatable?,
            ) {
                anim?.start()
            }
        }

        val mSimpleDraweeView = findViewById<SimpleDraweeView>(R.id.anim_loading)
        mSimpleDraweeView.controller = Fresco.newDraweeControllerBuilder()
            .setControllerListener(controllerListener)
            .build()

        mSimpleDraweeView.setImageURI(Uri.Builder()
            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
            .path(R.drawable.icon_loading.toString())
            .build())
    }

}