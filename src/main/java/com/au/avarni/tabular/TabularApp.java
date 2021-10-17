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

        JsonObject crawlerJSON = TabularFileReader.readJSONFile(filename);

        JsonArray emissionsTables = TabularTransformer.findEmissionsTables(crawlerJSON);

        JsonObject targetJSON = TabularTransformer.transformTables(emissionsTables);

        System.out.println(targetJSON);
    }
}
