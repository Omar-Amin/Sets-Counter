package com.omarlet.setscounter.ui;

import android.app.Notification;
import android.content.Context;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.model.Workout;

public class WorkoutNotification {

    public static final String WORKOUT_ID = "workout";
    public static final String PREVIOUS_WORKOUT = "previous";
    public static final String START_TIMER = "start";
    public static final String REST = "rest";

    public static Notification notification;

    public static void createNotification(Context context, Workout workout, Exercise currentExercise){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notification = new NotificationCompat.Builder(context,WORKOUT_ID)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle(workout.getName())
                .setContentText(currentExercise.getName())
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManagerCompat.notify(1,notification);
    }

}
