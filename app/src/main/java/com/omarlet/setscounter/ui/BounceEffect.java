package com.omarlet.setscounter.ui;

public class BounceEffect implements android.view.animation.Interpolator {

    @Override
    public float getInterpolation(float t) {
        float a = 1;
        float w = (float) 2.5;
        return (float) (-1 * Math.pow(Math.E, -t/ a) * Math.cos(t * w) + 1);
    }
}
