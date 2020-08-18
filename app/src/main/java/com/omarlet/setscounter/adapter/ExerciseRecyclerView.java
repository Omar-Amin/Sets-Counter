package com.omarlet.setscounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.Exercise;

import java.util.List;

public class ExerciseRecyclerView extends RecyclerView.Adapter<ExerciseRecyclerView.ExerciseViewHolder> {

    private Context context;
    private List<Exercise> exercises;

    public ExerciseRecyclerView(Context context, List<Exercise> exercises){
        this.context = context;
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseRecyclerView.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_layout,parent,false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseSets.setText(String.valueOf(exercise.getSets()));
        holder.exerciseweight.setText(String.valueOf(exercise.getWeight()));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, exerciseSets, exerciseweight;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseSets = itemView.findViewById(R.id.exerciseSets);
            exerciseweight = itemView.findViewById(R.id.exerciseWeight);
        }
    }
}
