package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.calculation.Timer;

public class MainActivity extends AppCompatActivity {

    private View countBackground;
    private TextView counter;
    private ProgressBar countProgress;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countBackground = findViewById(R.id.counterBackground);
        counter = findViewById(R.id.counter);
        countProgress = findViewById(R.id.countProgress);
        timer = new Timer(300000,1000,counter);
        timer.start();
    }
}
