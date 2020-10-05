package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            new ButtonActivity(this).addEventsToButtons();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("input", (String) ((TextView) this.findViewById(R.id.input_field)).getText());
        outState.putString("result", (String) ((TextView) this.findViewById(R.id.result_field)).getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ((TextView) this.findViewById(R.id.input_field)).setText(savedInstanceState.getString("input"));
        ((TextView) this.findViewById(R.id.result_field)).setText(savedInstanceState.getString("result"));
    }
}