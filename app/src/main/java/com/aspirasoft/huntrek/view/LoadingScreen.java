package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.aspirasoft.huntrek.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by saifkhichi96 on 25/12/2017.
 */

public class LoadingScreen extends RelativeLayout {
    public LoadingScreen(Context context) {
        super(context);
        init(context);
    }

    public LoadingScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.screen_loading, this);
        SimpleDraweeView loadingAnimation = findViewById(R.id.anim_loading);
        loadingAnimation.setController(Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .build());

        loadingAnimation.setImageURI(new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.icon_loading))
                .build());
    }
}
