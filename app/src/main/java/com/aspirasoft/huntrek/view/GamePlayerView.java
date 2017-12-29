package com.aspirasoft.huntrek.view;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;

import com.aspirasoft.huntrek.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by saifkhichi96 on 27/12/2017.
 */

public class GamePlayerView {

    private final SimpleDraweeView mSimpleDraweeView;
    private boolean isWalking = false;

    public GamePlayerView(SimpleDraweeView mSimpleDraweeView) {
        this.mSimpleDraweeView = mSimpleDraweeView;
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (anim != null) {
                    anim.stop();
                }
            }
        };
        this.mSimpleDraweeView.setController(Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .build());

        this.mSimpleDraweeView.setImageURI(new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.character_kid))
                .build());
    }

    public void start() {
        if (isWalking) return;
        isWalking = true;

        try {
            Animatable animatable = mSimpleDraweeView.getController().getAnimatable();
            animatable.start();
        } catch (Exception ignored) {

        }
    }

    public void stop() {
        if (!isWalking) return;
        isWalking = false;

        try {
            Animatable animatable = mSimpleDraweeView.getController().getAnimatable();
            animatable.stop();
        } catch (Exception ignored) {

        }
    }

    public void hide() {
        mSimpleDraweeView.setVisibility(View.GONE);
    }

    public void show() {
        mSimpleDraweeView.setVisibility(View.VISIBLE);
    }

}
