package com.example.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestionActivity extends ActivityWithMenu {
    private DatabaseReference database;
    private ArrayList<Question> questions;
    private Integer score, currentQuestionIndex;
    private TextView questionView;
    private ArrayList<Button> buttons;
    private FirebaseAuth mAuth;
    private String testName;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        init();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4204397579039349/5253600606");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void setQuestion(final Integer questionIndex) {
        if (questionIndex < questions.size()) {
            Question question = questions.get(questionIndex);
            questionView.setText(question.question);
            for (Integer index : new Integer[]{0, 1, 2, 3}) {
                Button button = buttons.get(index);
                button.setText(question.answers.get(index));
            }
        } else {
            saveScore();
            Intent intent = new Intent(QuestionActivity.this, TestActivity.class);
            String scoreAlert = String.format("%s quiz finished. Your score is %d.", testName, score);
            intent.putExtra("scoreAlert", scoreAlert);
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {

            }
            startActivity(intent);
        }
    }

    private void saveScore() {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("scores").child(user.getUid());
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(testName, score);
        userUpdates.put("email", user.getEmail());
        userRef.updateChildren(userUpdates);
    }

    private void init() {
        testName = getIntent().getStringExtra("testName");
        questions = new ArrayList();
        database = FirebaseDatabase.getInstance().getReference("tests");
        mAuth = FirebaseAuth.getInstance();

        database.child(testName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (questions.size() > 0) questions.clear();

                for(DataSnapshot questionData: snapshot.getChildren()) {
                    HashMap data = (HashMap) questionData.getValue();
                    String answer = data.get(data.remove("answer").toString()).toString();
                    ArrayList<String> answers = new ArrayList<>();
                    for (Object answerObject : data.values().toArray()) {
                        answers.add(answerObject.toString());
                    }
                    Question question = new Question(questionData.getKey(), answers, answer);
                    questions.add(question);
                }
                setQuestion(currentQuestionIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        currentQuestionIndex = 0;
        score = 0;
        questionView = findViewById(R.id.question);
        buttons = new ArrayList<>();
        buttons.add((Button) findViewById(R.id.answer1));
        buttons.add((Button) findViewById(R.id.answer2));
        buttons.add((Button) findViewById(R.id.answer3));
        buttons.add((Button) findViewById(R.id.answer4));
        for(Button button: buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    if (button.getText().equals(questions.get(currentQuestionIndex).answer)) score += 1;

                    currentQuestionIndex += 1;
                    setQuestion(currentQuestionIndex);
                }
            });
        }
    }
}
