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
     * E.g.: "Scope 2 - Location-based² 2,697,554 , " -> "2"
     * E.g.: "Emissions from Purchased Electricity (Scope 2) 4.71 , " -> "2"
     */
    private static final String scopeRegex = "\\(*scope\\s+([1-3]{1})\\)*";
    private static final Pattern scopePattern = Pattern.compile(scopeRegex, Pattern.CASE_INSENSITIVE);

    /**
     * Regex to extract the numeric value from a cell, reading from the end of the string
     * E.g.: "Scope 11,2 107,452 " -> "107452"
     */
    private static final String cellValueRegex = "\\s+((\\d|\\.|,)+)\\s*$";
    private static final Pattern cellValuePattern = Pattern.compile(cellValueRegex);

    /**
     * Regex to highlight the end of a string if it contains invalid special characters
     * E.g.: " - Location-based²" -> " - Location-based"
     */
    private static final String labelSpecialTrailingRegex = "[^a-z()]+$";

    /**
     * Regex to highlight the start of a string if it contains invalid special characters
     * E.g.: " - Location-based" -> "Location-based"
     */
    private static final String labelSpecialLeadingRegex = "^(\\W*)";

    /**
     * Given a 4 digit year or FY** format, returns a 4 digit year.
     *
     * @param year Any one of: 2021, FY21
     * @return A 4 digit year
     */
    public static String getFinancialYear(String year) {
        if (year == null) {
            return null;
        }

        // FY** year
        if (year.toUpperCase(Locale.ROOT).startsWith("FY")) {
            return "20" + year.replaceFirst("FY", "");
        }

        // 4 digit year
        if (year.length() == 4) {
            return year;
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
     * Returns a decimal number with special chars & spaces removed, or null if the value can't be parsed.
     *
     * @param numberStr A string of a number that might contain non-numeric characters
     * @return A decimal number
     */
    public static Double cleanseDouble(String numberStr) {
        try {
            return Double.valueOf(cleanseStringNumber(numberStr));
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
            System.err.println("Error whilst extracting scope number from: \"" + label + "\"");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the value of the cell content based on the final number appearing in the string.
     *
     * @param cell String cell content
     * @return Numeric cell value or null if there's no valid value found
     */
    public static Double getCellValue(String cell) {
        try {
            Matcher scopeMatcher = cellValuePattern.matcher(cell);

            if (scopeMatcher.find()) {
                return cleanseDouble(scopeMatcher.group(1));
            }
        } catch (Exception e) {
            System.err.println("Error whilst extracting cell value from: \"" + cell + "\"");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a cleansed label of the cell content based on the leading string, minus trailing number.
     *
     * @param cell String cell content
     * @return Label from the start of the string or the whole string if no trailing number found
     */
    public static String getRowLabel(String cell) {
        try {
            Matcher scopeMatcher = cellValuePattern.matcher(cell);

            if (scopeMatcher.find()) {
                String label = cell.substring(0, scopeMatcher.start()).strip();

                var config = TabularAppConfig.getAppConfig();

                // Whether to remove "Scope X" from the label
                if (config.getRemoveLabelScope()) {
                    Matcher matcher = scopePattern.matcher(label);
                    String cleanedLabel = matcher.replaceFirst("");

                    // Is the label is too short after removing the "Scope X" text?
                    if (cleanedLabel.length() <= 3) {
                        // If too short, replace the label with just "Scope X"
                        String scopeNumber = matcher.group(1);
                        label = "Scope " + scopeNumber;
                    } else {
                        // Otherwise, continue with the cleaned label
                        label = cleanedLabel;
                    }
                }

                // If the label is now exactly "Scope X", no further processing can be done
                if (scopePattern.matcher(label).matches()) {
                    return label;
                }

                // Do further cleansing of the label if required
                if (config.getCleanLabels()) {
                    // Remove any trailing special chars
                    label = label.replaceFirst(labelSpecialTrailingRegex, "");

                    // Remove any leading special chars
                    label = label.replaceFirst(labelSpecialLeadingRegex, "");
                }

                return label;
            }
        } catch (Exception e) {
            System.err.println("Error whilst extracting row label from: \"" + cell + "\"");
            e.printStackTrace();
        }

        return cell;
    }

    /**
     * Returns true if the passed value is the string "true" (case-insensitive)
     *
     * @param value String value
     * @return Boolean true if value is "true"
     */
    public static Boolean isTrue(String value) {
        return value != null && value.equalsIgnoreCase("true");
    }
}
