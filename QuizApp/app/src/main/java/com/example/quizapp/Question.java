package com.example.quizapp;

import java.util.ArrayList;

public class Question {
    public String question;
    public ArrayList<String> answers;
    public String answer;
    public Question(String question, ArrayList<String> answers, String answer) {
        this.question = question;
        this.answers = answers;
        this.answer = answer;
    }
}
