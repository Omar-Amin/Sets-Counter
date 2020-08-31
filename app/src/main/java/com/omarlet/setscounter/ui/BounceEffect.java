package com.omarlet.setscounter.ui;

public class BounceEffect implements android.view.animation.Interpolator {

    private float a = 1;
    private float w = (float) 2.5;

    public BounceEffect(float a, float w){
        this.a = a;
        this.w = w;
    }

    public BounceEffect(){
    }

    @Override
    public float getInterpolation(float t) {
        return (float) (-1 * Math.pow(Math.E, -t/ a) * Math.cos(t * w) + 1);
    }
}
