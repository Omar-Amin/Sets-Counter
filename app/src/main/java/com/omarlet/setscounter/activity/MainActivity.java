package com.omarlet.setscounter.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.omarlet.setscounter.model.OnWorkoutClick;
import com.omarlet.setscounter.model.TimerNotifcation;
import com.omarlet.setscounter.model.Workout;
import com.omarlet.setscounter.services.OnClearService;
import com.omarlet.setscounter.ui.BounceEffect;
import com.omarlet.setscounter.ui.WorkoutNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnWorkoutClick, TimerNotifcation {

    private View countBackground;
    private TextView counter, setsText, showName, showExercise;
    private ProgressBar countProgress;
    private Timer timer;
    private Button decrement, increment, stopTimer, chooseWorkout;
    private Animation btnAnim;
    private List<Workout> workouts = new ArrayList<>();
    private List<Exercise> exercises = new ArrayList<>();
    private RecyclerView workoutList;
    public static Workout chosenWorkout;
    private ImageButton leftExercise, rightExercise;

    public static int sets = 0;
    private int maxSets = 0;
    private int exerciseLeft = 0;
    public static int currentExercise = 0;

    NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        // notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            NotificationChannel notificationChannel = new NotificationChannel(WorkoutNotification.WORKOUT_ID,"Sets Counter", NotificationManager.IMPORTANCE_HIGH);

            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(notificationChannel);
            }

            registerReceiver(broadcastReceiver, new IntentFilter("TRACK_WORKOUT"));
            startService(new Intent(getBaseContext(), OnClearService.class));
            createNotiNoWorkout();
        }

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
                if(exerciseLeft >= 2){
                    rightExercise.startAnimation(btnAnim);
                }
                nextWorkout();
            }
        });

        leftExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWorkout();
            }

            private void previousWorkout() {
                if(currentExercise >= 2){
                    leftExercise.startAnimation(btnAnim);
                }

                if(currentExercise >= 1){
                    exerciseLeft++;
                    currentExercise--;
                    Exercise current = exercises.get(currentExercise);
                    maxSets = current.getSets();
                    showExercise.setText(current.getName());
                    onPrevWorkout(true);
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
                    ViewGroup backgroundView = (ViewGroup) inflater.inflate(R.layout.slider_background,null);

                    // updates recycler view in the slider
                    setupWorkout(viewGroup);
                    updateList();
                    // background in order to make it "dark" and clickable
                    background = viewGroup.findViewById(R.id.background);
                    final Button addWorkout = viewGroup.findViewById(R.id.addWorkout);
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
                    final PopupWindow staticBackground = new PopupWindow(backgroundView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

                    background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupMenu.dismiss();
                            staticBackground.dismiss();
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

                    popupMenu.setAnimationStyle(R.style.SlideAnimation);
                    staticBackground.setAnimationStyle(Animation.ABSOLUTE);
                    staticBackground.showAsDropDown(chooseWorkout);
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
        workoutList.setAdapter(new WorkoutRecyclerView(MainActivity.this, workouts, this));
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
        WorkoutNotification.rest = false;
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
        onNextExercise(true);
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
                WorkoutNotification.rest = true;
                onStartWorkout(true);
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
                    sets = 0;
                    nextWorkoutBtn();
                } else if(sets == maxSets+1 && !exercises.isEmpty()){
                    counter.setText("Next");
                    sets = 0;
                    nextWorkoutBtn();
                } else {
                    setsText.setText(String.valueOf(sets));
                    resetTimer();
                }
                onStartWorkout(true);
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
        if(operation == Workout.EDIT_WORKOUT){
            editWorkout(position);
        }
        if(operation == Workout.DELETE_WORKOUT){
            deleteWorkout(position);
        }
        if(operation == Workout.CHOOSE_WORKOUT){
            setupWorkout(position);
        }
    }

    private void editWorkout(int position) {
        edited = position;
        Workout workout = workouts.get(position);
        workout.setId(position);
        Intent intent = new Intent(MainActivity.this,AddWorkout.class);
        intent.putExtra("EditWorkout", workout);
        this.startActivity(intent);
    }

    private void deleteWorkout(int position) {
        Workout workout = workouts.remove(position);
        if(workout == chosenWorkout){
            defaultLayout();
        }
        chosenWorkout = null;
        removeWorkout();
    }

    private void defaultLayout(){
        hideExercise();
        exercises.clear();
        showName.setVisibility(View.INVISIBLE);
        currentExercise = 0;
        exerciseLeft = 0;
        maxSets = 4;
        reset();
    }

    // updates the new workout list after removing
    private void removeWorkout() {
        SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        int i = 0;
        for (Workout workout :workouts) {
            String json = gson.toJson(workout);
            editor.putString("workout"+i++, json);
            editor.putInt("amount",workouts.size());
        }
        // if the last workout is deleted the size would be 0
        if(workouts.size() == 0){
            editor.remove("workout");
            editor.putInt("amount",0);
        }
        editor.apply();
        added = workouts.size();
        updateList();
    }

    @SuppressLint("SetTextI18n")
    private void setupWorkout(int position) {
        chosenWorkout = workouts.get(position);
        background.callOnClick();

        if(chosenWorkout != null){
            // setup for the workout
            WorkoutNotification.rest = false;
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
            } else {
                hideExercise();
            }

            onStartWorkout(true);
        }
    }

    // hides the exercises if the workout doesn't have any
    private void hideExercise() {
        showExercise.setVisibility(View.INVISIBLE);
        rightExercise.setVisibility(View.INVISIBLE);
        leftExercise.setVisibility(View.INVISIBLE);
        increment.setVisibility(View.VISIBLE);
        decrement.setVisibility(View.VISIBLE);
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
        if (exercises.size() > 1){
            rightExercise.setVisibility(View.VISIBLE);
        } else {
            rightExercise.setVisibility(View.INVISIBLE);
        }
        leftExercise.setVisibility(View.INVISIBLE);
        exerciseLeft = exercises.size()-1;
    }

    // so we don't have to go through all workouts every time we resume or add a new workout
    private int added = 0;
    private int edited = -1;

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
            if(edited >= 0){
                getUpdatedWorkout(edited);
                if(chosenWorkout != null && chosenWorkout.getId() == edited){
                    setupWorkout(edited);
                }
                edited = -1;
            }
            updateList();
        }
    }

    private void getUpdatedWorkout(int pos) {
        SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("workout"+pos,"");
        assert json != null;
        if(!json.isEmpty()){
            Workout workout = gson.fromJson(json, Workout.class);
            workouts.set(pos,workout);
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

    // action depending on what the user presses on the notification
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String action = Objects.requireNonNull(intent.getExtras()).getString("action");

                assert action != null;
                switch (action){
                    case WorkoutNotification.PREVIOUS_WORKOUT:
                        onPrevWorkout(false);
                        break;
                    case WorkoutNotification.START_TIMER:
                        onStartWorkout(false);
                        break;
                    case WorkoutNotification.REST:
                        // TODO: make it rest after starting
                        onRestWorkout();
                        break;
                    case WorkoutNotification.NEXT_WORKOUT:
                        onNextExercise(false);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onPrevWorkout(boolean clicked) {
        if(!exercises.isEmpty() && leftExercise.getVisibility() == View.VISIBLE){
            if(!clicked){
                leftExercise.callOnClick();
            }
            createNotification();
            
        }
    }

    @Override
    public void onNextExercise(boolean clicked) {
        if(!exercises.isEmpty() && rightExercise.getVisibility() == View.VISIBLE){
            if(!clicked){
                rightExercise.callOnClick();
            }
            createNotification();
        } else {
            createNotiNoWorkout();
        }
    }

    @Override
    public void onStartWorkout(boolean clicked) {
        if(!clicked){
            startTimer();
            countBackground.callOnClick();
        } else {
            if(chosenWorkout == null || exercises.isEmpty()){
                createNotiNoWorkout();
            } else {
                createNotification();
            }
        }

    }

    @Override
    public void onRestWorkout() {
        countBackground.callOnClick();
        if(chosenWorkout == null && exercises.isEmpty()){
            createNotiNoWorkout();
        } else {
            createNotification();
        }
    }

    private void createNotification(){
        WorkoutNotification.createNotification(MainActivity.this,exercises.get(currentExercise), currentExercise,exercises.size());
    }

    private void createNotiNoWorkout() {
        WorkoutNotification.createNotification(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);
    }
}
