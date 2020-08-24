package com.omarlet.setscounter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.OnWorkoutClick;
import com.omarlet.setscounter.model.Workout;
import com.omarlet.setscounter.ui.DeleteWorkoutDialog;

import java.util.List;

public class WorkoutRecyclerView extends RecyclerView.Adapter<WorkoutRecyclerView.WorkoutViewHolder> {

    private Context context;
    private List<Workout> workouts;
    private OnWorkoutClick onWorkoutClick;

    public WorkoutRecyclerView(Context context, List<Workout> workouts, OnWorkoutClick onWorkoutClick){
        this.context = context;
        this.workouts = workouts;
        this.onWorkoutClick = onWorkoutClick;
    }

    @NonNull
    @Override
    public WorkoutRecyclerView.WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workout_layout,parent,false);
        return new WorkoutViewHolder(view, onWorkoutClick,context);
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

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        Button workoutName;
        ImageButton deleteWorkout, editWorkout;
        OnWorkoutClick onWorkoutClick;


        public WorkoutViewHolder(@NonNull final View itemView, final OnWorkoutClick onWorkoutClick, final Context context){
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutListName);
            deleteWorkout = itemView.findViewById(R.id.deleteWorkout);
            editWorkout = itemView.findViewById(R.id.editWorkout);
            this.onWorkoutClick = onWorkoutClick;
            workoutName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWorkoutClick.onWorkoutClick(getAdapterPosition(),Workout.CHOOSE_WORKOUT);
                }
            });

            deleteWorkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DeleteWorkoutDialog deleteWorkoutDialog = new DeleteWorkoutDialog( context);
                    deleteWorkoutDialog.show();
                    deleteWorkoutDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if(deleteWorkoutDialog.getStatus() == 1){
                                onWorkoutClick.onWorkoutClick(getAdapterPosition(), Workout.DELETE_WORKOUT);
                            }
                        }
                    });
                }
            });

            editWorkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWorkoutClick.onWorkoutClick(getAdapterPosition(),Workout.EDIT_WORKOUT);
                }
            });
        }

    }


}
