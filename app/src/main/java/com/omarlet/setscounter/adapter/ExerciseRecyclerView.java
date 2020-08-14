package com.omarlet.setscounter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
