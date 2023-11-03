package com.calanders.calplanner.calendar.util;

/**
 * A general Utility class.
 */
public class Util {
    /**
     * Retrieves the index of a String in a String array. Returns -1 if not found in array.
     *
     * @param a the String array to parse
     * @param s the String to find the index of
     * @return the index of the String or -1 if not found
     */
    public static int indexOf(String[] a, String s) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }
}
