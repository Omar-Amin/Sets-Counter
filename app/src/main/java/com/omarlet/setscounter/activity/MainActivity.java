package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omarlet.setscounter.R;
import com.omarlet.setscounter.adapter.WorkoutRecyclerView;
import com.omarlet.setscounter.calculation.Timer;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.model.Workout;
import com.omarlet.setscounter.ui.BounceEffect;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WorkoutRecyclerView.OnWorkoutClick {

    private View countBackground;
    private TextView counter, setsText, showName, showExercise;
    private ProgressBar countProgress;
    private Timer timer;
    private Button decrement, increment, stopTimer, chooseWorkout;
    private Animation btnAnim;
    private List<Workout> workouts = new ArrayList<>();
    private List<Exercise> exercises = new ArrayList<>();
    private RecyclerView workoutList;
    private Workout chosenWorkout;
    private ImageButton leftExercise, rightExercise;

    private int sets = 0;
    private int maxSets = 0;
    private int exerciseLeft = 0;
    private int currentExercise = 0;

    private int DELETE_WORKOUT = 1;
    private int CHOOSE_WORKOUT = 0;

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
        // "the ring"
        countBackground = findViewById(R.id.counterBackground);
        counter = findViewById(R.id.counter);
        countProgress = findViewById(R.id.countProgress);
        // in order for it to rotate smoothly
        countProgress.setMax(60000);
        // sets
        setsText = findViewById(R.id.sets); // TODO: change so it doesn't always start at 4
        maxSets = Integer.parseInt(setsText.getText().toString());
        // workout and exercise
        showName = findViewById(R.id.showName);
        showExercise = findViewById(R.id.showExercise);
        // choose exercise to the left/right
        rightExercise = findViewById(R.id.rightExercise);
        leftExercise = findViewById(R.id.leftExercise);

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

        rightExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout();
            }
        });

        leftExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWorkout();
            }

            private void previousWorkout() {
                if(chosenWorkout != null){
                    if(currentExercise >= 1){
                        exerciseLeft++;
                        currentExercise--;
                        Exercise current = exercises.get(currentExercise);
                        maxSets = current.getSets();
                        showExercise.setText(current.getName());
                    }
                }

                reset();
            }
        });
    }

    private boolean opened = false;

    private RelativeLayout background;

    private void setupSlide(){
        chooseWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!opened){
                    opened = true;
                    LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.choose_workout,null);

                    // updates recycler view in the slider
                    setupWorkout(viewGroup);
                    updateList();
                    // background in order to make it "dark" and clickable
                    background = viewGroup.findViewById(R.id.background);
                    final TextView addWorkout = viewGroup.findViewById(R.id.addWorkout);
                    final RelativeLayout listViewWorkout = viewGroup.findViewById(R.id.listLayout);

                    // calculating the size of the menu according to the screen size
                    int layoutSize = findViewById(R.id.mainBackground).getMeasuredHeight();
                    RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (layoutSize*0.75));
                    btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    listViewWorkout.setLayoutParams(btnParams);

                    // setup so that when touching the background it closes the window
                    // limiting the size of it so it doesn't activate the listener when clicking on the list
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

                    // switches to an activity for creating a workout
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

            private void setupWorkout(ViewGroup viewGroup) {
                workoutList = viewGroup.findViewById(R.id.workoutList);
                LinearLayoutManager lm = new LinearLayoutManager(MainActivity.this);
                workoutList.setLayoutManager(lm);
            }

        });
    }

    private void updateList() {
        workoutList.setAdapter(new WorkoutRecyclerView(MainActivity.this,workouts, this));
    }

    // this is the start after starting/finishing a workout/exercise
    private void nextWorkoutBtn(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWorkout();
            }
        });
    }

    private void nextWorkout(){
        if(chosenWorkout != null){
            if(exerciseLeft > 0){
                exerciseLeft--;
                currentExercise++;
                Exercise current = exercises.get(currentExercise);
                maxSets = current.getSets();
                showExercise.setText(current.getName());
            } else if(!exercises.isEmpty()) {
                setupExercises();
            }
        }

        reset();
    }

    @SuppressLint("SetTextI18n")
    private void reset(){
        if(exercises.isEmpty()){
            decrement.setVisibility(View.VISIBLE);
            increment.setVisibility(View.VISIBLE);
        }

        timer.cancel();
        sets = 0;
        counter.setText("Start");
        countProgress.setProgress(0);
        setsText.setText(String.valueOf(maxSets));
        stopTimer.setVisibility(View.INVISIBLE);

        if(currentExercise >= 1){
            leftExercise.setVisibility(View.VISIBLE);
        } else {
            leftExercise.setVisibility(View.INVISIBLE);
        }

        if(exerciseLeft == 0){
            rightExercise.setVisibility(View.INVISIBLE);
        } else {
            rightExercise.setVisibility(View.VISIBLE);
        }

        startTimer();
    }

    // starting the timer (rest phase)
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
                if(chosenWorkout != null){
                    stopTimer.setText("Skip workout");
                }
                resetTimer();
            }
        });
    }

    // stops the timer, countdown the amount of sets
    private void stopTimer(){
        countBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                timer.cancel();
                counter.setText("Rest");
                sets++;
                if(sets == maxSets+1 && (exerciseLeft == 0 || exercises.isEmpty())) {
                    counter.setText("Finished");
                    nextWorkoutBtn();
                } else if(sets == maxSets+1 && !exercises.isEmpty()){
                    counter.setText("Next");
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
    public void onWorkoutClick(int position, int operation) {
        if(operation == DELETE_WORKOUT){
            deleteWorkout(position);
        }
        if(operation == CHOOSE_WORKOUT){
            setupWorkout(position);
        }
    }

    private void deleteWorkout(int position) {
        System.out.println(position);
    }

    @SuppressLint("SetTextI18n")
    private void setupWorkout(int position) {
        chosenWorkout = workouts.get(position);
        background.callOnClick();

        if(chosenWorkout != null){
            // setup for the workout
            showName.setText(chosenWorkout.getName());
            showName.setVisibility(View.VISIBLE);
            timer.cancel();
            sets = 0;
            startTimer();
            countProgress.setProgress(0);
            counter.setText("Start");
            exercises = chosenWorkout.getExercises();
            if(exercises.size() > 0){
                setupExercises();
            }else {
                hideExercise();
            }
        }
    }

    // hides the exercises if the workout doesn't have any
    private void hideExercise() {
        showExercise.setVisibility(View.INVISIBLE);
        rightExercise.setVisibility(View.INVISIBLE);
        leftExercise.setVisibility(View.INVISIBLE);
    }

    // acts like a resetter when finishing a workout
    private void setupExercises(){
        currentExercise = 0;
        Exercise exercise = exercises.get(currentExercise);
        showExercise.setText(exercise.getName());
        setsText.setText(String.valueOf(exercise.getSets()));
        maxSets = exercise.getSets();
        increment.setVisibility(View.INVISIBLE);
        decrement.setVisibility(View.INVISIBLE);
        showExercise.setVisibility(View.VISIBLE);
        // if there is only one exercise there is no need to show the buttons
        if(exercises.size() > 1){
            rightExercise.setVisibility(View.VISIBLE);
            leftExercise.setVisibility(View.INVISIBLE);
        } else {
            rightExercise.setVisibility(View.INVISIBLE);
            leftExercise.setVisibility(View.INVISIBLE);
        }
        exerciseLeft = exercises.size()-1;
    }

    // so we don't have to go through all workouts every time we resume or add a new workout
    private int added = 0;

    @Override
    protected void onStart() {
        super.onStart();
        getWorkouts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWorkouts();
        if(workoutList != null){
            updateList();
        }
    }

    // retrieves each workout created
    private void getWorkouts() {
        SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
        int amount = pref.getInt("amount",0);
        Gson gson = new Gson();

        for (int i = added; i < amount; i++) {
            String json = pref.getString("workout"+i,"");
            assert json != null;
            if(!json.isEmpty()){
                Workout workout = gson.fromJson(json, Workout.class);
                workouts.add(workout);
            }
        }
        added = amount;
    }

}
