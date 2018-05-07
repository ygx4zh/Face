package com.example.facesample.utils;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimUtil {
    private static final String TAG = "AnimUtil";

    public static void playTranslationYOut(View bottom, View parent,
                                           ValueAnimator.AnimatorUpdateListener listener) {
        float translationY_bottom = bottom.getTranslationY();
        int height = bottom.getHeight();

        ObjectAnimator bottomOut = ObjectAnimator.ofFloat(
                parent,
                "translationY",
                translationY_bottom, translationY_bottom + height);
        bottomOut.addUpdateListener(listener);
        bottomOut.setDuration(500);
        bottomOut.start();
    }

    public static void playTranslationYIn(View bottom,View parent,
                                            ValueAnimator.AnimatorUpdateListener listener) {
        float translationY_bottom = bottom.getTranslationY();
        int height = bottom.getHeight();

        ObjectAnimator bottomOut = ObjectAnimator.ofFloat(
                parent,
                "translationY",
                translationY_bottom + height, translationY_bottom);
        bottomOut.addUpdateListener(listener);
        bottomOut.setDuration(500);
        bottomOut.start();
    }
}
