package com.omarlet.setscounter.calculation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private RelativeLayout layout;

    public Timer(long millisInFuture, long countDownInterval, Activity main) {
        super(millisInFuture, countDownInterval);
        secondsFuture = millisInFuture;
        this.counter = main.findViewById(R.id.counter);
        this.progressBar = main.findViewById(R.id.countProgress);
        this.progressFirst = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar, null);
        this.progressSecond = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar_secondphase, null);
        this.progressThird = ResourcesCompat.getDrawable(main.getResources(), R.drawable.progress_bar_thirdphase, null);
        this.layout = main.findViewById(R.id.mainBackground);
    }

    long prev = 0;

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

        // it was cool and all, but also kinda distracting
        /*
        if(prev != seconds){
            changeColor();
            prev = seconds;
        }
        */

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

    private boolean oneDown = false;
    private boolean oneUp = false;
    private boolean twoDown = false;
    private boolean twoUp = true;
    private boolean threeDown = false;
    private boolean threeUp = false;
    private String color = "#ff6666";

    private void changeColor() {
        int current;
        if(oneDown){
            current = Integer.parseInt(color.substring(1,2),16)-1;
            String hex = Integer.toHexString(current);
            color = "#" + hex + hex + "ff66";
            if(current == 6){
                oneDown = false;
                threeUp = true;
            }
        }else if(oneUp){
            current = Integer.parseInt(color.substring(1,2),16)+1;
            String hex = Integer.toHexString(current);
            color = "#" + hex + hex + "66ff";
            if(current == 15){
                oneUp = false;
                threeDown = true;
            }
        }else if(twoDown){
            current = Integer.parseInt(color.substring(4,5),16)-1;
            String hex = Integer.toHexString(current);
            color = "#66" + hex + hex + "ff";
            if(current == 6){
                twoDown = false;
                oneUp = true;
            }
        }else if(twoUp){
            current = Integer.parseInt(color.substring(4,5),16)+1;
            String hex = Integer.toHexString(current);
            color = "#ff" + hex + hex + "66";
            if(current == 15){
                twoUp = false;
                oneDown = true;
            }
        }else if(threeDown){
            current = Integer.parseInt(color.substring(6,7),16)-1;
            String hex = Integer.toHexString(current);
            color = "#ff66" + hex + hex;
            if(current == 6){
                threeDown = false;
                twoUp = true;
            }
        }else if(threeUp){
            current = Integer.parseInt(color.substring(6,7),16)+1;
            String hex = Integer.toHexString(current);
            color = "#66ff" + hex + hex;
            if(current == 15){
                threeUp = false;
                twoDown = true;
            }
        }
        layout.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onFinish() {
    }
}
