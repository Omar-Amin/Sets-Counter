package com.omarlet.setscounter.model;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private String name;
    private List<Exercise> exercises = new ArrayList<>();

    public Workout(String name){
        this.name = name;
    }

    public void addExercise(Exercise exercise){
        exercises.add(exercise);
    }

    public void removeExercise(int position){
        exercises.remove(position);
    }

    public List<Exercise> getExercises(){
        return exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
