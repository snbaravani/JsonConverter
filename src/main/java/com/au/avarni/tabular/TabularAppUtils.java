package com.au.avarni.tabular;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabularAppUtils {

    /**
     * Regex checking if a table row string contains parsable data
     */
    private static final String parsableRowRegex = "[a-z0-9]+";
    private static final Pattern parsableRowPattern = Pattern.compile(parsableRowRegex, Pattern.CASE_INSENSITIVE);

    /**
     * Regex to extract the scope number from a string (constrained to known scope numbers: 1-3)
     */
    private static final String scopeRegex = "scope\\s+([1-3]{1})";
    private static final Pattern scopePattern = Pattern.compile(scopeRegex, Pattern.CASE_INSENSITIVE);

    /**
     * Given a 4 digit year or FY** format, returns a 4 digit year integer.
     *
     * @param year Any one of: 2021, FY21
     * @return A 4 digit year
     */
    public static Integer getFinancialYear(String year) {
        if (year == null) {
            return null;
        }

        // FY** year
        if (year.toUpperCase(Locale.ROOT).startsWith("FY")) {
            return Integer.valueOf("20" + year.replaceFirst("FY", ""));
        }

        // 4 digit year
        if (year.length() == 4) {
            return Integer.valueOf(year);
        }

        // Unrecognised format
        return null;
    }

    /**
     * Returns a string of a number with special chars & spaces removed.
     *
     * @param numberStr A string of a number that might contain non-numeric characters
     * @return A string of the number with all special chars (except the decimal place) removed
     */
    private static String cleanseStringNumber(String numberStr) {
        return numberStr.replaceAll("[^0-9.]", "");
    }

    /**
     * Returns a floating point number special chars & spaces removed, or null if the value can't be parsed.
     *
     * @param numberStr A string of a number that might contain non-numeric characters
     * @return A floating point number
     */
    public static Float cleanseFloat(String numberStr) {
        try {
            return Float.valueOf(cleanseStringNumber(numberStr));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns true if a table row string contains no usable data (strings, numbers)
     *
     * @param row String of a row from a table
     * @return true if the row contains no data to be parsed
     */
    public static Boolean isEmptyRow(String row) {
        return row == null || !parsableRowPattern.matcher(row).find();
    }

    /**
     * Attempts to detect & extract the scope number from a table cell label, or null if one can't be detected.
     *
     * @param label A single table cell label
     * @return The detected scope number or null
     */
    public static Integer getScopeNumber(String label) {
        try {
            Matcher scopeMatcher = scopePattern.matcher(label);

            if (scopeMatcher.find()) {
                return Integer.valueOf(scopeMatcher.group(1));
            }
        } catch (Exception e) {
            System.err.println("Error whilst extracting scope number");
            e.printStackTrace();
        }

        return null;
    }
}
