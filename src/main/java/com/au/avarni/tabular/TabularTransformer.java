package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

public class TabularTransformer {

  /**
   * Given the JSON object returned by the crawler, returns any tables related to emissions.
   * @return JSON array of strings representing tabular data
   */
  public static JsonArray findEmissionsTables(JsonObject crawlerJSON) {
    // TODO: Implement this
    return null;
  }

  /**
   * Given an array of emissions table strings, returns JSON in the target schema.
   * @return The target JSON object (see README.md for example object)
   */
  public static JsonObject transformTables(JsonArray emissionsTables) {
    // TODO: Implement this
    return null;
  }
}
