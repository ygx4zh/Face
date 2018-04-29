package com.example.facesample.utils;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

public class AnimUtil {
    private static final String TAG = "AnimUtil";

    public static void playTranslationYOut(View bottom, View top) {
        float translationY_top = top.getTranslationY();
        float translationY_bottom = bottom.getTranslationY();
        int height = bottom.getHeight();
        int height_top = top.getHeight();

        ObjectAnimator bottomOut = ObjectAnimator.ofFloat(
                bottom,
                "translationY",
                translationY_bottom, translationY_bottom + height);
        ObjectAnimator topOutScaleY = ObjectAnimator.ofFloat(top,
                "scaleY",
                1.0f, 1.0f + (height * 1.0f / height_top));

        ObjectAnimator topOutScaleX = ObjectAnimator.ofFloat(top,
                "scaleX",
                1.0f, 1.0f + (height * 1.0f / height_top));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(bottomOut, topOutScaleX, topOutScaleY);
        set.setDuration(1000);
        set.start();
    }

    public static void playTranslationYIn(View bottom, View top) {
        float translationY_top = top.getTranslationY();
        float translationY_bottom = bottom.getTranslationY();
        int height = bottom.getHeight();

        ObjectAnimator bottomOut = ObjectAnimator.ofFloat(
                bottom,
                "translationY",
                translationY_bottom + height, translationY_bottom);
        ObjectAnimator topOut = ObjectAnimator.ofFloat(top,
                "translationY",
                1.0f * (translationY_top + height) / translationY_top, 1.0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(bottomOut, topOut);
        set.setDuration(1000);
//        topOut.addUpdateListener();
        /*set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
        set.start();
    }
}
