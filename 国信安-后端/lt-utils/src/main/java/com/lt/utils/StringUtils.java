package com.lt.utils;

/**
 * @author Lhz
 */
public class StringUtils {

    public static synchronized String deleteCharString0(String sourceString) {
        StringBuilder deleteString = new StringBuilder();
        for (int i = 0; i < sourceString.length(); i++) {
            if(i == 0) {
                continue;
            }
            if(i == sourceString.length() - 1) {
                continue;
            }
            deleteString.append(sourceString.charAt(i));
        }
        return deleteString.toString();
    }

}
