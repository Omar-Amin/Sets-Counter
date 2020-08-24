package com.omarlet.setscounter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.model.OnExerciseClick;

import java.util.List;

public class ExerciseRecyclerView extends RecyclerView.Adapter<ExerciseRecyclerView.ExerciseViewHolder>{

    private Context context;
    private List<Exercise> exercises;
    private OnExerciseClick onExerciseClick;

    public ExerciseRecyclerView(Context context, List<Exercise> exercises, OnExerciseClick onExerciseClick ){
        this.context = context;
        this.exercises = exercises;
        this.onExerciseClick = onExerciseClick;
    }

    @NonNull
    @Override
    public ExerciseRecyclerView.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_layout,parent,false);
        return new ExerciseViewHolder(view, onExerciseClick, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseSets.setText(String.valueOf(exercise.getSets()));
        holder.exerciseWeight.setText(String.valueOf(exercise.getWeight()));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView exerciseName, exerciseSets, exerciseWeight;
        private Context context;
        private OnExerciseClick onExerciseClick;

        public ExerciseViewHolder(@NonNull View itemView, OnExerciseClick onExerciseClick, Context context) {
            super(itemView);
            this.context = context;
            this.onExerciseClick = onExerciseClick;

            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseSets = itemView.findViewById(R.id.exerciseSets);
            exerciseWeight = itemView.findViewById(R.id.exerciseWeight);

            RelativeLayout exerciseLayout = itemView.findViewById(R.id.exerciseLayout);
            exerciseLayout.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu popup = new PopupMenu(context, view);
            popup.inflate(R.menu.exercise_on_hold);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.deleteExercise) {
                        onExerciseClick.onExerciseClick(getAdapterPosition());
                        return true;
                    }
                    return false;

                }
            });
            popup.show();
            return false;
        }

    }

}
