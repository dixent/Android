package com.example.calculator;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calculator.helpers.MainHelper;

public class ButtonActivity extends MainHelper {
    private TextView inputField;
    private TextView resultField;
    private MainActivity layout;
    private ScriptEngine engine;

    private final int[] inputElements = new int[] {
        R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8,
        R.id.button_9, R.id.button_0, R.id.button_plus, R.id.button_minus,  R.id.button_dot,  R.id.button_div,  R.id.button_multiply
    };

    private final int[] trigonometricElements = new int[] {
        R.id.button_sin,  R.id.button_cos,  R.id.button_tg,  R.id.button_ctg
    };


    public ButtonActivity(MainActivity layout) throws ScriptException {
        this.layout = layout;
        inputField = layout.findViewById(R.id.input_field);
        resultField = layout.findViewById(R.id.result_field);
        initEngine();
    }

    private void initEngine() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager ();
        engine = manager.getEngineByName ("js");

        engine.eval("var sin = Math.sin");
        engine.eval("var cos = Math.cos");
        engine.eval("var tg = Math.tan");
        engine.eval("var ctg = function cot(value) { return 1 / Math.tan(value); };");
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
                try {
                    resultField.setText(String.valueOf(engine.eval((String) inputField.getText())));
                } catch (ScriptException e) {
                    e.printStackTrace();
                    resultField.setText(R.string.invalid_exp);
                }
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

        layout.findViewById(R.id.button_minus_plus).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addPlusMinus();
            }
        });

        if( layout.findViewById(trigonometricElements[0]) != null) {
            for (int id: trigonometricElements) {
                layout.findViewById(id).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button button = (Button)v;
                        addOperatorWithBrackets(replaceBrackets((String) button.getText()));
                    }
                });
            }
        }
    }

    private void addValueToView(String newValue) {
        if (!duplicatedOperator(newValue)) inputField.setText(inputField.getText() + newValue);
    }

    private boolean duplicatedOperator(String symbol) {
        String currentContent = (String) inputField.getText();
        if (blank(currentContent)) return !isNumeric(symbol);

        String last = lastSymbol(currentContent);
        if (isNumeric(last)) {
            return false;
        } else if (last.equals(symbol)) {
            return true;
        } else if (isNumeric(symbol) && !last.equals(")")) {
            return false;
        } else return isNumeric(symbol);
    }

    private void removeSymbol() {
        String currentContent = (String) inputField.getText();
        if (!blank(currentContent)) inputField.setText(currentContent.substring(0, currentContent.length() - 1));
    }

    private void removeLastNumber() {
        String currentContent = (String) inputField.getText();

        if (!blank(currentContent) && isNumeric(lastSymbol(currentContent))) {
            String newContent = "";
            int lastNumberSize = lastNumber(currentContent).length();
            newContent = currentContent.substring(0, currentContent.length() - lastNumberSize);
            if(lastSymbol(newContent).equals(".")) {
                lastNumberSize = lastNumber(newContent).length();
                newContent = newContent.substring(0, newContent.length() - (lastNumberSize + 1));
            }
            inputField.setText(newContent);
        }
    }

    private void addOperatorWithBrackets(String operator) {
        String currentContent = (String) inputField.getText();

        if (!blank(currentContent)) {
            String lastSymbol = lastSymbol(currentContent);
            if (isNumeric(lastSymbol)) {
                String[] result = lastNumberWithOperator(currentContent);
                String lastOperator = result[0], lastNumber = result[1];
                String regexp;
                String replacement;

                if (blank(lastOperator)) {
                    regexp = String.format("%s$", lastNumber);
                    replacement = String.format("%s(%s)", operator, lastNumber);
                } else {
                    regexp = String.format("\\%s%s$", lastOperator, lastNumber);
                    replacement = String.format("%s%s(%s)", lastOperator, operator, lastNumber);
                }
                currentContent = currentContent.replaceFirst(regexp, replacement);
            } else if (lastSymbol.equals(")")) {
                String lastOperation = lastNumberWithOperatorFromExpressionWithBrackets(currentContent);
                String regexp = String.format("%s$", lastOperation.replace("(", "\\(").replace(")", "\\)"));
                String replacement =  String.format("%s(%s)", operator, lastOperation);

                currentContent = currentContent.replaceFirst(regexp, replacement);
            }
            inputField.setText(currentContent);
        }
    }

    private void addPlusMinus() {
        String currentContent = (String) inputField.getText();

        if (!blank(currentContent)) {
            String lastSymbol = lastSymbol(currentContent);

            if (isNumeric(lastSymbol)) {
                String[] result = lastNumberWithOperator(currentContent);
                String lastOperator = result[0], lastNumber = result[1];
                String regexp = String.format("\\%s%s$", lastOperator, lastNumber);
                String replacement =  String.format("%s(-%s)", lastOperator, lastNumber);
                currentContent = currentContent.replaceFirst(regexp, replacement);
            } else if (lastSymbol.equals(")")) {
                String lastOperation = lastNumberWithOperatorFromExpressionWithBrackets(currentContent);
                if (isNumeric(replaceBrackets(lastOperation))) {
                    String[] result = lastNumberWithOperatorFromExpression(currentContent);
                    String lastOperator = result[0], lastNumber = result[1];
                    String regexp = String.format("\\(\\%s%s\\)$", lastOperator, lastNumber);
                    currentContent = currentContent.replaceFirst(regexp, lastNumber);
                } else {
                    currentContent = String.format("(-%s)", lastOperation);
                }
            }
            inputField.setText(currentContent);
        }
    }
}
