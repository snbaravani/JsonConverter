package com.au.avarni.converter;

/**
 * Contails utility methods
 */
public class AppUtils {
    public static boolean containsScope1(String value) {
        if (value != null && (value.contains("Scope1") || value.contains("SCOPE 1") || value.contains("(Scope 1)") ||
                value.contains("(SCOPE 1)"))) {
            return true;
        }
        return false;
    }

    public static boolean containsScope2(String value) {
        if (value != null && (value.contains("Scope 2") || value.contains("SCOPE 2") || value.contains("(Scope 2)") ||
                value.contains("(SCOPE 2)"))) {
            return true;
        }
        return false;
    }

    public static boolean containsScope3(String value) {
        if (value != null && (value.contains("Scope 3") || value.contains("SCOPE 3") || value.contains("(Scope 3)") ||
                value.contains("(SCOPE 3)"))) {
            return true;
        }
        return false;
    }

}
