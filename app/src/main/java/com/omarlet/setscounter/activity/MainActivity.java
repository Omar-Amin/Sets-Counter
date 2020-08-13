package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.calculation.Timer;

public class MainActivity extends AppCompatActivity {

    private View countBackground;
    private TextView counter, setsText;
    private ProgressBar countProgress;
    private Timer timer;
    private int sets = 0;
    private int maxSets = 0;
    private Button decrement, increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countBackground = findViewById(R.id.counterBackground);
        counter = findViewById(R.id.counter);
        countProgress = findViewById(R.id.countProgress);
        decrement = findViewById(R.id.decrementSet);
        increment = findViewById(R.id.incrementSet);
        setsText = findViewById(R.id.sets); // TODO: change so it doesn't always start at 4
        maxSets = Integer.parseInt(setsText.getText().toString());
        // in order for it to rotate smoothly
        countProgress.setMax(60000);
        timer = new Timer(300000,10,this);
        startTimer();
        setupButtons();
    }

    private void setupButtons() {
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(maxSets > 1){
                    maxSets--;
                    setsText.setText(String.valueOf(maxSets));
                }
            }
        });
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(maxSets < 9){
                    maxSets++;
                    setsText.setText(String.valueOf(maxSets));
                }
            }
        });
    }

    private void nextWorkout(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                decrement.setVisibility(View.VISIBLE);
                increment.setVisibility(View.VISIBLE);
                counter.setText("Start");
                countProgress.setProgress(0);
                setsText.setText(String.valueOf(maxSets));
                startTimer();
            }
        });
    }

    private void startTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrement.setVisibility(View.INVISIBLE);
                increment.setVisibility(View.INVISIBLE);
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
                counter.setText("Rest");
                setsText.setText(String.valueOf(maxSets-sets));
                if(sets == maxSets) {
                    counter.setText("Finished");
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
