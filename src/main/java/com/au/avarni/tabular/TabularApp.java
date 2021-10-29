package com.au.avarni.tabular;

import com.au.avarni.DataTransformerCommon;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.File;

public class TabularApp {

    /**
     * Converts the raw crawler output tables into a standard JSON structure, writing it to a file.
     *
     * @param file       Raw crawler output JSON file
     * @param outputPath The path to write the output JSON file to
     */
    public static void crawlerOutputToJSONFile(File file, String outputPath) {
        System.out.println("Transforming JSON file: " + file.getPath());

        try {
            // 1-3. Parse & transform the input into ideal JSON output
            String targetJSON = getJSONFromInput(file);

            // 4. Write the JSON output to a file
            DataTransformerCommon.writeJSONFile(outputPath, "tabular-" + file.getName(), targetJSON);

            System.out.println("Successfully parsed " + file.getName());
        } catch (Exception e) {
            System.out.println("Error transforming JSON file");
            e.printStackTrace();
        }
    }

    /**
     * Converts raw crawler input JSON into a standard JSON structure as a string.
     *
     * @param file Raw crawler output JSON file
     * @return Standard JSON structure in a string
     * @throws Exception Any error thrown whilst reading the file or parsing the input
     */
    public static String getJSONFromInput(File file) throws Exception {
        // 1. Get a JsonObject from the specified JSON file
        JsonObject crawlerJSON = TabularFileSystem.readJSONFile(file);

        // 2. Get a collection of tables that relate to emissions
        JsonArray emissionsTables = TabularTransformer.findEmissionsTables(crawlerJSON);

        // 3. Extract, cleanse & aggregate data from emissions tables, formatting it into the target JSON
        return TabularTransformer.transformEmissionsTables(emissionsTables);
    }
}
