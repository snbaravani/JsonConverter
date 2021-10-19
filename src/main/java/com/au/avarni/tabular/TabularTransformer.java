package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashMap;
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
    public static JsonArray findEmissionsTables(JsonObject crawlerJSON) {
        JsonKey statusKey = Jsoner.mintJsonKey("status", new JsonObject());
        JsonObject statusObject = crawlerJSON.getMap(statusKey);

        JsonKey tablesKey = Jsoner.mintJsonKey("tables", new JsonArray());
        JsonArray tables = statusObject.getCollection(tablesKey);

        // Remove irrelevant table strings, leaving only important tables
        tables.removeIf((table) -> {
            String tableStr = (String) table;

            return !emissionsTablePattern.matcher(tableStr).find();
        });

        return tables;
    }

    /**
     * Given an array of emissions table strings, returns JSON in the target schema.
     *
     * @return The target JSON object (see README.md for example object)
     */
    public static String transformEmissionsTables(JsonArray emissionsTables) {
        HashMap<Integer, HashMap<String, Float>> jsonMap = new HashMap<>();

        emissionsTables.forEach((table) -> {
            String cleanTable = ((String) table)
                    .trim()
                    .replaceFirst(tablePrefixRegex, "");

            String[] rows = cleanTable.split("\\n");
            String[] columnNames = rows[0].split(",");

            for (String columnName : columnNames) {
                String cleanColumnName = columnName.trim();

                if (!cleanColumnName.isEmpty()) {
                    Integer year = TabularAppUtils.getFinancialYear(cleanColumnName);

                    // TODO: Add entries in map
                    jsonMap.put(year, new HashMap<>());
                }
            }
        });

        return Jsoner.serialize(jsonMap);
    }
}
