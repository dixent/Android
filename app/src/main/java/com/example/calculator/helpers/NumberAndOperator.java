package com.example.calculator.helpers;

import java.util.Arrays;

public abstract class NumberAndOperator {
    public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    protected String lastNumber(String content) {
        String[] numbers = content.split("\\D+");
        return numbers[numbers.length - 1];
    }

    protected String[] lastNumberWithOperator(String content) {
        String[] numbers = content.split(String.format(WITH_DELIMITER, "[^0-9]"));

        String lastNumber =  numbers[numbers.length - 1];

        if (numbers.length == 1) {
            return new String[] { "", lastNumber };
        }

        String lastOperator =  numbers[numbers.length - 2];

        if (numbers[numbers.length - 2].equals(".")) {
            lastNumber = String.join("", Arrays.copyOfRange(numbers, numbers.length - 3,  numbers.length - 1));
            lastOperator = numbers[numbers.length - 4];
        }
        return new String[] { lastOperator, lastNumber };
    }

    protected String[] lastNumberWithOperatorFromExpression(String content) {
        return lastNumberWithOperator(content.replaceAll("\\(|\\)", ""));
    }

    protected String lastSymbol(String string) {
        return String.valueOf(string.charAt(string.length() - 1));
    }

    protected boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
