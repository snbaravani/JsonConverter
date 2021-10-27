package com.au.avarni.tabular;

import com.au.avarni.DataTransformerCommon;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.File;

public class TabularApp {

    public static void convertJSON(File file, String outputPath) {
        System.out.println("Transforming JSON file: " + file.getPath());

        try {
            // 1. Get a JsonObject from the specified JSON file
            JsonObject crawlerJSON = TabularFileSystem.readJSONFile(file);

            // 2. Get a collection of tables that relate to emissions
            JsonArray emissionsTables = TabularTransformer.findEmissionsTables(crawlerJSON);

            // 3. Extract, cleanse & aggregate data from emissions tables, formatting it into the target JSON
            String targetJSON = TabularTransformer.transformEmissionsTables(emissionsTables);

            // 4. Write the JSON output to a file
            DataTransformerCommon.writeJSONFile(outputPath, "tabular-" + file.getName(), targetJSON);

            System.out.println("Successfully parsed " + file.getName());
        } catch (Exception e) {
            System.out.println("Error transforming JSON file");
            e.printStackTrace();
        }
    }
}
