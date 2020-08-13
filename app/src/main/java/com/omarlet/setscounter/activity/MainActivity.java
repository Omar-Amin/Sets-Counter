package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.calculation.Timer;
import com.omarlet.setscounter.ui.BounceEffect;

public class MainActivity extends AppCompatActivity {

    private View countBackground;
    private TextView counter, setsText;
    private ProgressBar countProgress;
    private Timer timer;
    private int sets = 0;
    private int maxSets = 0;
    private Button decrement, increment, stopTimer, chooseWorkout;
    private Animation btnAnim,slideUp, slideDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // buttons
        stopTimer = findViewById(R.id.stopTimer);
        decrement = findViewById(R.id.decrementSet);
        increment = findViewById(R.id.incrementSet);
        chooseWorkout = findViewById(R.id.chooseWorkout);
        // button animation
        btnAnim = AnimationUtils.loadAnimation(this,R.anim.button_animation);
        btnAnim.setInterpolator(new BounceEffect());
        slideUp = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_down);
        // "the ring"
        countBackground = findViewById(R.id.counterBackground);
        counter = findViewById(R.id.counter);
        countProgress = findViewById(R.id.countProgress);
        // in order for it to rotate smoothly
        countProgress.setMax(60000);
        // sets
        setsText = findViewById(R.id.sets); // TODO: change so it doesn't always start at 4
        maxSets = Integer.parseInt(setsText.getText().toString());

        timer = new Timer(300000,10,this);
        startTimer();
        setupButtons();
        setupSlide();
    }

    private void setupButtons() {
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrement.startAnimation(btnAnim);
                if(maxSets > 1){
                    maxSets--;
                    setsText.setText(String.valueOf(maxSets));
                }
            }
        });
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment.startAnimation(btnAnim);
                if(maxSets < 9){
                    maxSets++;
                    setsText.setText(String.valueOf(maxSets));
                }
            }
        });
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout();
            }
        });
    }

    private boolean opened = false;

    private void setupSlide(){
        chooseWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!opened){
                    opened = true;
                    LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.choose_workout,null);

                    RelativeLayout background = viewGroup.findViewById(R.id.background);
                    // TODO: Change to listview and add workout
                    final Button listViewWorkout = viewGroup.findViewById(R.id.testing);

                    int layoutSize = findViewById(R.id.mainBackground).getMeasuredHeight();
                    RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (layoutSize*0.75));
                    btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    listViewWorkout.setLayoutParams(btnParams);

                    final PopupWindow popupMenu = new PopupWindow(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listViewWorkout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_down));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    popupMenu.dismiss();
                                }
                            }, 175);
                            opened = false;
                        }
                    });
                    popupMenu.setAnimationStyle(Animation.ABSOLUTE);
                    popupMenu.showAsDropDown(chooseWorkout,0,100);
                    listViewWorkout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_up));
                }

            }
        });
    }

    private void nextWorkoutBtn(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void nextWorkout(){
        timer.cancel();
        sets = 0;
        decrement.setVisibility(View.VISIBLE);
        increment.setVisibility(View.VISIBLE);
        counter.setText("Start");
        countProgress.setProgress(0);
        setsText.setText(String.valueOf(maxSets));
        startTimer();
        stopTimer.setVisibility(View.INVISIBLE);
    }


    private void startTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                decrement.setVisibility(View.INVISIBLE);
                increment.setVisibility(View.INVISIBLE);
                stopTimer.setVisibility(View.VISIBLE);
                counter.setText("Rest");
                sets++;
                setsText.setText(String.valueOf(sets));
                resetTimer();
            }
        });
    }

    private void stopTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                timer.cancel();
                counter.setText("Rest");
                sets++;
                if(sets == maxSets+1) {
                    counter.setText("Finished");
                    nextWorkoutBtn();
                } else {
                    setsText.setText(String.valueOf(sets));
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
                setsText.setText(String.valueOf(sets));
                stopTimer();
            }
        });
    }

}
