package com.omarlet.setscounter.calculation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.omarlet.setscounter.R;

public class Timer extends CountDownTimer {

    private TextView counter;
    private final long secondsFuture;
    private ProgressBar progressBar;
    private final Drawable progressFirst;
    private final Drawable progressSecond;
    private final Drawable progressThird;

    public Timer(long millisInFuture, long countDownInterval, Activity main) {
        super(millisInFuture, countDownInterval);
        secondsFuture = millisInFuture;
        this.counter = main.findViewById(R.id.counter);
        this.progressBar = main.findViewById(R.id.countProgress);
        this.progressFirst = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar, null);
        this.progressSecond = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar_secondphase, null);
        this.progressThird = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar_thirdphase, null);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onTick(long l) {
        // having progress bore taking larger values so it rotates smoothly
        long seconds = (secondsFuture-l)/1000;
        int progressUpdate = (int) (secondsFuture-l);
        long minutes = 0;
        if(seconds >= 60){
            minutes = seconds/60;
            seconds -= minutes*60;
            // resetting the progress to 0
            progressUpdate -= minutes*60*1000;
        }
        progressBar.setProgress(progressUpdate);

        // maybe for later
        /*
        if(minutes == 1){
            progressBar.setProgressDrawable(progressSecond);
        } else if (minutes >= 2){
            progressBar.setProgressDrawable(progressThird);
        }
         */

        if(seconds < 10){
            counter.setText(minutes + ":0" + seconds);
        }else{
            counter.setText(minutes + ":" + seconds);
        }
    }

    @Override
    public void onFinish() {
    }
}
