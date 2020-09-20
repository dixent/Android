package com.example.calculator.helpers;

public abstract class MainHelper extends NumberAndOperator {
    protected boolean blank(String content) {
        return content.equals("");
    }
}
