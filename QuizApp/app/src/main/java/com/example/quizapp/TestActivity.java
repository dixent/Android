package com.example.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends ActivityWithMenu {
    private ListView listView;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        init();
        checkAlerts();
        indexTests();
    }

    private void checkAlerts() {
        String scoreAlert = (String) getIntent().getStringExtra("scoreAlert");
        if (scoreAlert != null) {
            Toast toast = Toast.makeText(getApplicationContext(), scoreAlert, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void init() {
        listView = findViewById(R.id.tests_list);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
               Intent intent = new Intent(TestActivity.this, QuestionActivity.class);
               intent.putExtra("testName", (String) parent.getItemAtPosition(position));
               startActivity(intent);
            }
        });
        database = FirebaseDatabase.getInstance().getReference("tests");
    }

    private void indexTests() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listData.size() > 0) listData.clear();

                for(DataSnapshot testData: snapshot.getChildren()) {
                    String testName = testData.getKey();
                    assert testName != null;
                    listData.add(testName);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
