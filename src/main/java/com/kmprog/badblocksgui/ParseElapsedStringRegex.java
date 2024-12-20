package com.kmprog.badblocksgui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseElapsedStringRegex {

    public static void main(String[] args) {
        String input = "elapsed. (0/123123/30 errors)"; //Example with extra whitespace
        int[] results = parseElapsedString(input);

        if (results != null) {
            System.out.println("Errors: " + results[0] + "/" + results[1] + "/" + results[2]);
        } else {
            System.out.println("Invalid input string format.");
        }
    }

    public static int[] parseElapsedString(String input) {
        Pattern pattern = Pattern.compile("\\(\\s*(\\d+)\\s*/\\s*(\\d+)\\s*/\\s*(\\d+)\\s*errors\\s*\\)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            int[] errors = new int[3];
            errors[0] = Integer.parseInt(matcher.group(1));
            errors[1] = Integer.parseInt(matcher.group(2));
            errors[2] = Integer.parseInt(matcher.group(3));
            return errors;
        } else {
            return null; // Indicate invalid format
        }
    }
}
