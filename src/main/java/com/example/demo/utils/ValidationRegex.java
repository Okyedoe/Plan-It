package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
        }
    public static boolean isRegexDate(String target){
        String regex = "^[0-9]*$";
        boolean result = Pattern.matches(regex,target);
        return result;
    }
    public static boolean isRegexPhoneNum(String target){
        String regex = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$";
        boolean result = Pattern.matches(regex,target);
        return result;
    }
}

