package com.example.quizapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ScoreActivity extends ActivityWithMenu {
    private FirebaseAuth current_user;
    private HashMap<String, Long> myScores, scores;
    private TableLayout myScoresTable, topTenTable, topHundredTable;
    private LinearLayout myScoresLayout, topTenLayout, topHundredLayout;
    private Button myScoresButton, topTenButton, topHundredButton;
    private DatabaseReference database;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);
        init();
        initListeners();
        initDbData();

//        MobileAds.initialize(this, "ca-app-pub-4204397579039349~3050093184");
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void init() {
        current_user = FirebaseAuth.getInstance();
        myScores = new HashMap<>();
        scores = new HashMap<>();
        myScoresTable = findViewById(R.id.my_scores);
        topTenTable = findViewById(R.id.top_ten);
        topHundredTable = findViewById(R.id.top_hundred);
        myScoresLayout = findViewById(R.id.my_scores_layout);
        topTenLayout = findViewById(R.id.top_ten_layout);
        topHundredLayout = findViewById(R.id.top_hundred_layout);
        myScoresButton = findViewById(R.id.my_scores_button);
        topTenButton = findViewById(R.id.top_ten_button);
        topHundredButton = findViewById(R.id.top_hundred_button);
    }

    private void initListeners() {
        myScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myScoresLayout.setVisibility(View.VISIBLE);
                topTenLayout.setVisibility(View.GONE);
                topHundredLayout.setVisibility(View.GONE);
            }
        });

        topTenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myScoresLayout.setVisibility(View.GONE);
                topTenLayout.setVisibility(View.VISIBLE);
                topHundredLayout.setVisibility(View.GONE);
            }
        });

        topHundredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myScoresLayout.setVisibility(View.GONE);
                topTenLayout.setVisibility(View.GONE);
                topHundredLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initDbData() {
        database = FirebaseDatabase.getInstance().getReference("scores");

        database.child(current_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myScores = (HashMap<String, Long>) snapshot.getValue();

                if (myScores == null) return;

                if (myScores.get("email") != null) {
                    myScores.remove("email");
                }
                updateTable(myScores, myScoresTable, 20);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userScores : snapshot.getChildren()) {
                    HashMap<String, Object> userData = (HashMap<String, Object>) userScores.getValue();
                    String email = "";
                    if (userData.get("email") != null) {
                       email = userData.remove("email").toString();
                    }



                    Long[] selectedUserScores = userData.values().toArray(new Long[0]);
                    long score = 0;
                    for(int index = 0; index < selectedUserScores.length; index += 1) {
                        score += selectedUserScores[index];
                    }

                  scores.put(email.equals("") ? userScores.getKey() : email, score);
                }

                scores = sortByValue(scores);
                updateTable(sortByValueReverse(getLast(scores, 10)), topTenTable, 15);
                updateTable(sortByValueReverse(getLast(scores, 100)), topHundredTable, 15);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static HashMap<String, Long> sortByValue(HashMap<String, Long> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Long> > list =
                new LinkedList<Map.Entry<String, Long> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Long> >() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static HashMap<String, Long> sortByValueReverse(HashMap<String, Long> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Long> > list =
                new LinkedList<Map.Entry<String, Long> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Long> >() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Collections.reverse(list);
        // put data from sorted list to hashmap
        HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private HashMap<String, Long> getLast(HashMap<String, Long> currentScores, int lastNumber) {
        HashMap<String, Long> lastScores = new HashMap<>();
        String[] scoresKeys = currentScores.keySet().toArray(new String[0]);

        for(int index = currentScores.size() - 1; index >= 0; index -= 1) {
            String key = scoresKeys[index];
            lastScores.put(key, currentScores.get(key));
            lastNumber -= 1;
            if (lastNumber == 0) return lastScores;
        }

        return lastScores;
    }

    private void updateTable(HashMap<String, Long> scores, TableLayout table, Integer textSize) {
        TableRow row;
        TextView textView;
        int index = 0;
        for(String testName : scores.keySet()) {
            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            textView = new TextView(this);
            textView.setText(testName);
            textView.setPadding(20, 20, 20, 20);
            textView.setTextSize(textSize);
            row.addView(textView, 0);

            textView = new TextView(this);
            textView.setText(scores.get(testName).toString());
            textView.setPadding(20, 20, 20, 20);
            textView.setTextSize(textSize);

            row.addView(textView, 1);
            table.addView(row);
        }
    }
}
