package com.omarlet.setscounter.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.omarlet.setscounter.R;

public class DeleteWorkoutDialog extends Dialog {

    public DeleteWorkoutDialog(@NonNull Context context) {
        super(context);
    }

    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_workout);

        Button deleteWorkout = findViewById(R.id.deleteWorkoutPopup);
        Button cancel = findViewById(R.id.cancelDeletion);

        deleteWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 1;
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 0;
                dismiss();
            }
        });
    }

    public int getStatus() {
        return status;
    }
}
