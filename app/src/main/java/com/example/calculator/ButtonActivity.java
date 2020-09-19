package com.example.calculator;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ButtonActivity {
    private TextView inputField;
    private TextView resultField;
    private MainActivity layout;
    private final int[] inputElements = new int[] {
            R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8,
            R.id.button_9, R.id.button_0, R.id.button_plus, R.id.button_minus,  R.id.button_dot,  R.id.button_div,  R.id.button_multiply
    };

    public ButtonActivity(MainActivity layout) {
        this.layout = layout;
        inputField = layout.findViewById(R.id.input_field);
        resultField = layout.findViewById(R.id.result_field);
    }

    public void addEventsToButtons() {
        for (int id: inputElements) {
            layout.findViewById(id).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Button button = (Button)v;
                    addValueToView((String) button.getText());
                }
            });
        }

        layout.findViewById(R.id.equal).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calculateResult((String) inputField.getText());
            }
        });

        layout.findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeSymbol();
            }
        });

        layout.findViewById(R.id.button_C).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputField.setText("");
            }
        });

        layout.findViewById(R.id.button_CE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeLastNumber();
            }
        });
    }

    private void addValueToView(String newValue) {
        inputField.setText(inputField.getText() + newValue);
    }

    private void removeSymbol() {
        String currentContent = (String) inputField.getText();
        inputField.setText(currentContent.substring(0, currentContent.length() - 1));
    }

    private void removeLastNumber() {
        String currentContent = (String) inputField.getText();

        if (isNumeric(String.valueOf(currentContent.charAt(currentContent.length() - 1)))) {
            String newContent = "";
            String[] numbers = currentContent.split("\\D+");
            int lastNumberSize = numbers[numbers.length - 1].length();
            newContent = currentContent.substring(0, currentContent.length() - lastNumberSize);
            if(String.valueOf(newContent.charAt(newContent.length() - 1)).equals(".")) {
                numbers = newContent.split("\\D+");
                lastNumberSize = numbers[numbers.length - 1].length();
                newContent = newContent.substring(0, newContent.length() - (lastNumberSize + 1));
            }
            inputField.setText(newContent);
        }
    }
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void calculateResult(String expressionString) {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
            String result = String.valueOf(engine.eval(expressionString));

            resultField.setText(result);
        } catch (ScriptException e) {
            System.err.println("Error evaluating the script: " + e.getMessage());
        }
    }
}
