package com.omarlet.setscounter.model;

public interface TimerNotifcation {
    void onPrevWorkout(boolean clicked);
    void onNextExercise(boolean clicked);
    void onStartWorkout();
    void onRestWorkout();
}
