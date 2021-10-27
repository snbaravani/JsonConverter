package com.au.avarni.tabular;

import com.au.avarni.tabular.objects.ScopeObject;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class TabularTransformer {

    /**
     * Match the start of a table string regardless of table content
     */
    private static final String tablePrefixRegex = "^Table: Table_[0-9]+\\n\\n,";
    private static final Pattern tablePrefixPattern = Pattern.compile(tablePrefixRegex);

    /**
     * Match a year by FY21 or 2021 format
     */
    private static final String yearRegex = "(FY[0-9]{2}|[0-9]{4})";
    private static final Pattern yearPattern = Pattern.compile(yearRegex);

    /**
     * Match a table string containing emissions data based on table headers
     */
    private static final String emissionsTableRegex = tablePrefixRegex + yearRegex + " , ";
    private static final Pattern emissionsTablePattern = Pattern.compile(emissionsTableRegex);

    /**
     * Given the JSON object returned by the crawler, returns any tables related to emissions.
     *
     * @return JSON array of strings representing tabular data
     */
    public static JsonArray findEmissionsTables(JsonObject crawlerJSON) throws Exception {
        JsonKey statusKey = Jsoner.mintJsonKey("status", new JsonObject());
        JsonObject statusObject = crawlerJSON.getMap(statusKey);

        JsonKey tablesKey = Jsoner.mintJsonKey("tables", new JsonArray());
        JsonArray tables = statusObject.getCollection(tablesKey);

        // Remove irrelevant table strings, leaving only important tables
        tables.removeIf((table) -> {
            String tableStr = (String) table;

            return !emissionsTablePattern.matcher(tableStr).find();
        });

        var config = TabularAppConfig.getAppConfig();
        Boolean onlyFirstTable = TabularAppUtils.isTrue(config.get("onlyFirstTable"));

        // If only the first table is needed, remove all others
        if (onlyFirstTable) {
            return new JsonArray(List.of(tables.get(0)));
        }

        return tables;
    }

    /**
     * Given an array of emissions table strings, returns JSON in the target schema.
     *
     * @return The target JSON object (see README.md for example object)
     */
    public static String transformEmissionsTables(JsonArray emissionsTables) {
        TreeMap<String, ScopeObject> scopesMap = new TreeMap<>() {{
            put("scope1", new ScopeObject(1));
            put("scope2", new ScopeObject(2));
            put("scope3", new ScopeObject(3));
        }};

        emissionsTables.forEach((table) -> {
            String cleanTable = ((String) table)
                    .strip()
                    .replaceFirst(tablePrefixRegex, "");

            String[] rows = cleanTable.split("\\n");

            String headerRow = rows[0];
            String[] columnNames = headerRow.split(",");

            // Define a map storing the column index of each year (key: index, value: year)
            HashMap<Integer, String> yearColumnIndexes = new HashMap<>();

            // Extract years from the header row cells, recording each column index they occupy
            for (Integer headerCellIndex = 0; headerCellIndex < columnNames.length; headerCellIndex++) {
                String columnName = columnNames[headerCellIndex].strip();

                // Check if the column name is a year value
                if (yearPattern.matcher(columnName).find()) {
                    String year = TabularAppUtils.getFinancialYear(columnName);

                    // Set the column index of this year
                    yearColumnIndexes.put(headerCellIndex, year);
                }
            }

            // Keep track of the current scope to apply to rows without an explicit scope value in their label
            Integer currentScope = null;

            // Loop over all rows after the header row to collect values
            for (Integer rowIndex = 1; rowIndex < rows.length; rowIndex++) {
                String row = rows[rowIndex].strip();

                // Strip trailing end whitespace space & comma: " ,"
                row = row.substring(0, row.length() - 2);

                // Extract cell values by a ", " delimiter (not just comma, due to commas in numbers)
                String[] cells = row.split(", ");

                String rowLabelCell = cells[0];
                String rowLabel = TabularAppUtils.getRowLabel(rowLabelCell);
                Integer rowScope = TabularAppUtils.getScopeNumber(rowLabelCell);

                if (rowScope != null) {
                    currentScope = rowScope;
                }

                // Skip parsing row if any of these are true:
                //  - it's empty
                //  - no scope encountered in the table yet (to ignore irrelevant rows that come before any scope rows)
                //  - row label contains "total" (don't need totals from the raw data, this gets calculated dynamically)
                if (
                        TabularAppUtils.isEmptyRow(row)
                                || currentScope == null
                                || rowLabel.toLowerCase().contains("total")
                ) {
                    continue;
                }

                // Loop over each column based on the year heading, storing the value in each cell
                for (Map.Entry<Integer, String> entry : yearColumnIndexes.entrySet()) {
                    Integer colIndex = entry.getKey();
                    String year = entry.getValue();

                    String rawValue = cells[colIndex];
                    Double numericValue = null;

                    // Special parsing needed for the first value as the row label & value get combined
                    if (colIndex == 0) {
                        numericValue = TabularAppUtils.getCellValue(rawValue);
                    } else {
                        numericValue = TabularAppUtils.cleanseDouble(rawValue);
                    }

                    if (year != null && numericValue != null) {
                        scopesMap.get("scope" + currentScope).setYearValue(year, rowLabel, numericValue);
                    }
                }
            }
        });

        // Convert the map of scope objects into a map of maps for JSON serialization
        TreeMap<String, TreeMap<String, TreeMap<String, Double>>> jsonMap = new TreeMap<>();
        scopesMap.forEach((scopeName, scopeObject) -> {
            scopeObject.removeRawTotalValues();

            jsonMap.put(scopeName, scopeObject.getYearsMap());
        });

        return Jsoner.serialize(jsonMap);
    }
}
