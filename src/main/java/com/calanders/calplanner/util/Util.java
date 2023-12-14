package com.calanders.calplanner.util;

/**
 * A general Utility class.
 */
public class Util {
    /**
     * Retrieves the index of a String in a String array. Returns -1 if not found in array.
     *
     * @param s the String to find the index of
     * @param a the String array to parse
     * @return the index of the String or -1 if not found
     */
    public static int indexOf(String s, String[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the specified value if it is between the specified minimum and maximum. If the
     * value is below the minimum, the minimum will be returned. If the value is above the
     * maximum, the maximum will be returned.
     *
     * @param value the value to be clamped
     * @param min the lower bound for clamping
     * @param max the upper bound for clamping
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }
}
