package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omarlet.setscounter.R;
import com.omarlet.setscounter.calculation.Timer;
import com.omarlet.setscounter.model.Workout;
import com.omarlet.setscounter.ui.BounceEffect;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View countBackground;
    private TextView counter, setsText;
    private ProgressBar countProgress;
    private Timer timer;
    private int sets = 0;
    private int maxSets = 0;
    private Button decrement, increment, stopTimer, chooseWorkout;
    private Animation btnAnim,slideUp, slideDown;
    private List<Workout> workouts = new ArrayList<>();

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
        getWorkouts();
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
                    final TextView addWorkout = viewGroup.findViewById(R.id.addWorkout);
                    // TODO: Change to listview and add workout
                    final LinearLayout listViewWorkout = viewGroup.findViewById(R.id.testing);

                    // calculating the size of the menu according to the screen size
                    int layoutSize = findViewById(R.id.mainBackground).getMeasuredHeight();
                    RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (layoutSize*0.75));
                    btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    listViewWorkout.setLayoutParams(btnParams);

                    // setup so that when touching the background it closes the window
                    RelativeLayout.LayoutParams backgroundParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (layoutSize*0.22));
                    backgroundParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    background.setLayoutParams(backgroundParams);

                    final PopupWindow popupMenu = new PopupWindow(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                    background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listViewWorkout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_down));
                            Handler handler = new Handler();
                            // let the animation play before actually closing it
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    popupMenu.dismiss();
                                }
                            }, 175);
                            opened = false;
                        }
                    });

                    addWorkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addWorkout.startAnimation(btnAnim);
                            Intent addWorkout = new Intent(MainActivity.this, AddWorkout.class);
                            MainActivity.this.startActivity(addWorkout);
                        }
                    });

                    listViewWorkout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_up));
                    popupMenu.setAnimationStyle(Animation.ABSOLUTE);
                    popupMenu.showAsDropDown(chooseWorkout);
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

    @Override
    protected void onResume() {
        super.onResume();
        getWorkouts();
    }

    private void getWorkouts() {
        SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
        int amount = pref.getInt("amount",0);
        Gson gson = new Gson();
        for (int i = 0; i < amount; i++) {
            String json = pref.getString("workout"+i,"");
            assert json != null;
            if(!json.isEmpty()){
                Workout workout = gson.fromJson(json,Workout.class);
                workouts.add(workout);
            }
        }
    }


}
