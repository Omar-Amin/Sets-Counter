package com.omarlet.setscounter.calculation;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Timer extends CountDownTimer {

    private TextView counter;
    private long secondsFuture;

    public Timer(long millisInFuture, long countDownInterval, TextView counter) {
        super(millisInFuture, countDownInterval);
        secondsFuture = millisInFuture/1000;
        this.counter = counter;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTick(long l) {
        long seconds = secondsFuture-l/1000;
        long minutes = 0;
        if(seconds > 60){
            minutes = seconds/60;
            seconds -= minutes*60;
        }
        if(seconds < 10){
            counter.setText(minutes + ":0" + seconds);
        }else{
            counter.setText(minutes + ":" + seconds);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onFinish() {
        counter.setText("00:00");
    }
}
