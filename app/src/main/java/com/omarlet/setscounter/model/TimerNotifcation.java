package com.omarlet.setscounter.model;

public interface TimerNotifcation {
    void onPrevWorkout(boolean clicked);
    void onNextExercise(boolean clicked);
    void onStartWorkout(boolean clicked);
    void onRestWorkout();
}
