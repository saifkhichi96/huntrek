package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by saifkhichi96 on 25/12/2017.
 */

public class LoadingAnimation extends RelativeLayout {
    public LoadingAnimation(Context context) {
        super(context);
        init(context);
    }

    public LoadingAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.screen_loading, this);
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (anim != null) {
                    anim.start();
                }
            }
        };

        SimpleDraweeView mSimpleDraweeView = findViewById(R.id.anim_loading);
        mSimpleDraweeView.setController(Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .build());

        mSimpleDraweeView.setImageURI(new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.icon_loading))
                .build());
    }
}
