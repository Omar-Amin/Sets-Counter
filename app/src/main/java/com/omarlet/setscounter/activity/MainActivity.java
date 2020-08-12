package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.calculation.Timer;

public class MainActivity extends AppCompatActivity {

    private View countBackground;
    private TextView counter;
    private ProgressBar countProgress;
    private Timer timer;
    private int sets = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countBackground = findViewById(R.id.counterBackground);
        counter = findViewById(R.id.counter);
        countProgress = findViewById(R.id.countProgress);
        // in order for it to rotate smoothly
        countProgress.setMax(60000);
        timer = new Timer(300000,10,this);
        startTimer();
    }

    private void nextWorkout(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                counter.setText("Start");
                startTimer();
            }
        });
    }

    private void startTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.start();
                stopTimer();
            }
        });
    }

    private void stopTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                timer.cancel();
                sets++;
                counter.setText("Sets: " + sets);
                if(sets == 4) {
                    nextWorkout();
                    sets = 0;
                } else {
                    resetTimer();
                }
            }
        });
    }

    private void resetTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                timer.start();
                stopTimer();
            }
        });
    }

}
