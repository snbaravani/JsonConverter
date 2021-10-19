package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

public class TabularApp {
    public static void main(String[] args) throws Exception {
        String filename = args[0];

        if (filename == null) {
            System.out.println("Please pass the JSON file name to be parsed");
        }

        System.out.println("Transforming " + filename);

        // 1. Get a JsonObject from the specified JSON file
        JsonObject crawlerJSON = TabularFileReader.readJSONFile(filename);

        // 2. Get a collection of tables that relate to emissions
        JsonArray emissionsTables = TabularTransformer.findEmissionsTables(crawlerJSON);

        // 3. Extract, cleanse & aggregate data from emissions tables, formatting it into the target JSON
        String targetJSON = TabularTransformer.transformEmissionsTables(emissionsTables);

        System.out.println(targetJSON);
    }
}
