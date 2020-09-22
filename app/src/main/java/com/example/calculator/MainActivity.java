package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
}