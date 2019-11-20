package com.aspirasoft.huntrek.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.model.characters.Barkeep;
import com.aspirasoft.huntrek.model.characters.Barmaid;
import com.aspirasoft.huntrek.model.characters.GameCharacter;
import com.aspirasoft.huntrek.model.characters.IndonesianKid;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;


public class GamePlayerView extends SimpleDraweeView {

    private GameCharacter mGameCharacter;
    private int characterType;

    private boolean isWalking;

    public GamePlayerView(Context context) {
        super(context);
        init(0);
    }

    public GamePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GamePlayerView,
                0, 0);
        try {
            characterType = a.getInteger(R.styleable.GamePlayerView_character_type, 0);
        } finally {
            a.recycle();
        }

        init(characterType);
    }

    public GamePlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GamePlayerView,
                0, 0);
        try {
            characterType = a.getInteger(R.styleable.GamePlayerView_character_type, 0);
        } finally {
            a.recycle();
        }

        init(characterType);
    }

    public void setCharacterType(int characterType) {
        this.characterType = characterType;
        if (this.characterType >= 3) {
            this.characterType = 0;
        }

        switch (this.characterType) {
            case 0:
                mGameCharacter = new Barkeep();
                break;
            case 1:
                mGameCharacter = new Barmaid();
                break;
            case 2:
                mGameCharacter = new IndonesianKid();
                break;
            default:
                mGameCharacter = new Barkeep();
                break;
        }

        this.setController(Fresco.newDraweeControllerBuilder()
                .setControllerListener(new AnimationController())
                .build());

        this.setImageURI(mGameCharacter.getWalkingAnimUri());
    }

    public void init(int characterType) {
        this.isWalking = false;
        this.setCharacterType(characterType);
    }

    public void startWalking() {
        if (!this.isWalking) {
            if (this.getController() != null && this.getController().getAnimatable() != null) {
                this.getController().getAnimatable().start();
                isWalking = true;
            }
        }
    }

    public void stopWalking() {
        if (this.isWalking) {
            if (this.getController() != null && this.getController().getAnimatable() != null) {
                this.getController().getAnimatable().stop();
                isWalking = false;
            }
        }
    }

    @Nullable
    public Drawable getCharacterDrawable() {
        return mGameCharacter.getFullDrawable(getContext());
    }

    @Nullable
    public Drawable getFaceDrawable() {
        return mGameCharacter.getFaceDrawable(getContext());
    }

    private class AnimationController extends BaseControllerListener<ImageInfo> {
        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo,
                                    @Nullable Animatable anim) {
            if (anim != null) {
                anim.stop();
            }
        }
    }

}