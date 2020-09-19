package com.example.calculator;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ButtonActivity {
    private TextView inputField;
    private MainActivity layout;
    private final int[] inputElements = new int[] {
            R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8,
            R.id.button_9, R.id.button_0, R.id.button_plus, R.id.button_minus 
    };
    public ButtonActivity(MainActivity layout) {
        this.layout = layout;
        inputField = layout.findViewById(R.id.input_field);
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
    }

    private void addValueToView(String newValue) {
          inputField.setText(inputField.getText() + newValue);
    }
}
