package com.omarlet.setscounter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.omarlet.setscounter.R;
import com.omarlet.setscounter.model.Exercise;

public class ExerciseDialog extends Dialog {

    public ExerciseDialog(@NonNull Context context) {
        super(context);
    }

    private EditText insertName, insertSets, insertWeight;
    private Button save;
    private Exercise exercise = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);

        insertName = findViewById(R.id.insertName);
        insertSets = findViewById(R.id.insertSets);
        insertWeight = findViewById(R.id.insertWeight);
        save = findViewById(R.id.saveExercise);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = insertName.getText().toString();
                String sets = insertSets.getText().toString();
                String weight = insertWeight.getText().toString();

                if(!name.isEmpty() && !sets.isEmpty() && !weight.isEmpty()){
                    exercise = new Exercise(name, Integer.parseInt(sets), Integer.parseInt(weight));
                    dismiss();
                }
            }
        });

    }

    public Exercise getExercise(){
        return exercise;
    }
}
