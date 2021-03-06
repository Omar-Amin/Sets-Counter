package com.omarlet.setscounter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.omarlet.setscounter.R;
import com.omarlet.setscounter.adapter.ExerciseRecyclerView;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.model.OnExerciseClick;
import com.omarlet.setscounter.model.Workout;
import com.omarlet.setscounter.ui.ExerciseDialog;

public class AddWorkout extends AppCompatActivity implements OnExerciseClick {

    private Workout workout;
    private RecyclerView exerciseList;
    private EditText workoutName;
    private boolean edit = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        Button saveWorkout = findViewById(R.id.saveWorkout);
        Button addExercise = findViewById(R.id.addExercise);
        workoutName = findViewById(R.id.workoutName);

        exerciseList = findViewById(R.id.exerciseList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        exerciseList.setLayoutManager(lm);

        workout = (Workout) getIntent().getSerializableExtra("EditWorkout");
        if (workout == null){
            workout = new Workout("Choose name");
        }else {
            workoutName.setText(workout.getName());
            edit = true;
            saveWorkout.setText("Update");
        }

        setupExerciseList();

        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ExerciseDialog exerciseDialog = new ExerciseDialog(AddWorkout.this);
                exerciseDialog.show();
                exerciseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Exercise newExercise = exerciseDialog.getExercise();
                        if(newExercise != null){
                            workout.addExercise(newExercise);
                            setupExerciseList();
                        }
                    }
                });

            }
        });

        saveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!workoutName.getText().toString().isEmpty()){
                    workout.setName(workoutName.getText().toString());

                    if(!edit){
                        saveWorkout();
                    } else {
                        updateWorkout();
                    }
                }
            }

            private void updateWorkout() {
                SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(workout);
                editor.putString("workout"+workout.getId(), json);
                editor.apply();
                finish();
            }

            private void saveWorkout(){
                SharedPreferences pref = getSharedPreferences("workouts",MODE_PRIVATE);
                int amount = pref.getInt("amount",0);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(workout);
                editor.putString("workout"+amount, json);
                editor.putInt("amount",amount+1);
                editor.apply();
                finish();
            }

        });
    }

    private void setupExerciseList() {
        exerciseList.setAdapter(new ExerciseRecyclerView(this, workout.getExercises(),this));
    }

    @Override
    public void onExerciseClick(int position) {
        workout.removeExercise(position);
        setupExerciseList();
    }
}