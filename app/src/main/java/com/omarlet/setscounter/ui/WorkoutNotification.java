package com.omarlet.setscounter.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.activity.MainActivity;
import com.omarlet.setscounter.model.Exercise;
import com.omarlet.setscounter.services.NotificationService;

public class WorkoutNotification {

    public static final String WORKOUT_ID = "workout";
    public static final String PREVIOUS_WORKOUT = "previous";
    public static final String NEXT_WORKOUT = "next";
    public static final String START_TIMER = "start";
    public static final String REST = "rest";

    public static Notification notification;

    public static boolean rest = false;

    public static void createNotification(Context context, Exercise currentExercise, int pos, int size, String time) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

        String text = currentExercise == null ? "No exercise" : currentExercise.getName();

        PendingIntent pendingPrev;
        int prevDrawing;
        if (pos == 0) {
            pendingPrev = null;
            prevDrawing = 0;
        } else {
            Intent intentPrev = new Intent(context, NotificationService.class)
                    .setAction(PREVIOUS_WORKOUT);
            pendingPrev = PendingIntent.getBroadcast(context, 0, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);
            prevDrawing = R.drawable.left_exercise;
        }

        PendingIntent pendingStart;
        int restDrawing;
        if (rest) {
            Intent intentRest = new Intent(context, NotificationService.class).setAction(REST);
            restDrawing = R.drawable.rest_notification;
            pendingStart = PendingIntent.getBroadcast(context, 0, intentRest, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intentRest = new Intent(context, NotificationService.class).setAction(START_TIMER);
            restDrawing = R.drawable.start_notification;
            pendingStart = PendingIntent.getBroadcast(context, 0, intentRest, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        PendingIntent pendingNext;
        int nextDrawing;
        if (pos == size - 1) {
            pendingNext = null;
            nextDrawing = 0;
        } else {
            Intent intentNext = new Intent(context, NotificationService.class)
                    .setAction(NEXT_WORKOUT);
            pendingNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            nextDrawing = R.drawable.right_exercise;
        }

        notification = new NotificationCompat.Builder(context, WORKOUT_ID)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle(text)
                .setContentText(time + " Sets: " + MainActivity.sets)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(prevDrawing, "Previous", pendingPrev)
                .addAction(restDrawing, "Start", pendingStart)
                .addAction(nextDrawing, "Next", pendingNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    public static void createNotification(Context context) {
        createNotification(context, null, 0, 1);
    }

    public static void createNotification(Context context , Exercise currentExercise, int pos, int size) {
        createNotification(context,currentExercise,pos,size, "0:00");
    }


}