package com.omarlet.setscounter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Workout implements Serializable {
    private String name;
    private List<Exercise> exercises = new ArrayList<>();
    private int id = -1;

    public static final int EDIT_WORKOUT = 2;
    public static final int DELETE_WORKOUT = 1;
    public static final int CHOOSE_WORKOUT = 0;

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

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
