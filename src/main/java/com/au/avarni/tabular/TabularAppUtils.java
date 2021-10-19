package com.au.avarni.tabular;

import java.util.Locale;

public class TabularAppUtils {

    /**
     * Given a 2/4 digit year or FY** format, returns a 4 digit year.
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
     * Returns a string of a number with special chars & spaces removed
     *
     * @param numberStr A string of a number that might contain non-numeric characters
     * @return A string of the number with all special chars (except the decimal place) removed
     */
    private static String cleanseStringNumber(String numberStr) {
        return numberStr.replaceAll("[^0-9.]", "");
    }

    /**
     * Returns a floating point number special chars & spaces removed
     *
     * @param numberStr A string of a number that might contain non-numeric characters
     * @return A floating point number
     */
    public static Float cleanseFloat(String numberStr) {
        return Float.valueOf(cleanseStringNumber(numberStr));
    }
}
