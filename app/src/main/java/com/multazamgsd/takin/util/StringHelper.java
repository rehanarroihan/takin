package com.multazamgsd.takin.util;

public class StringHelper {

    public String cutString(String sentence, Integer maxLength) {
        String output = sentence;
        if (sentence.length() > 0) {
            if (sentence.length() >= maxLength) {
                output = sentence.substring(0, maxLength) + "...";
            }
        }
        return output;
    }
}
