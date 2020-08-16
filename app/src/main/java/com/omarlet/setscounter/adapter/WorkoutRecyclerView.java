package com.omarlet.setscounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.model.Workout;

import java.util.List;

public class WorkoutRecyclerView extends RecyclerView.Adapter<WorkoutRecyclerView.WorkoutViewHolder> {

    private Context context;
    private List<Workout> workouts;

    public WorkoutRecyclerView(Context context, List<Workout> workouts){
        this.context = context;
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutRecyclerView.WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workout_layout,parent,false);
        return new WorkoutRecyclerView.WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutRecyclerView.WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.workoutName.setText(workout.getName());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {

        Button workoutName;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutListName);
        }
    }

}
