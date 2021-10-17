package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TabularFileReader {

  public static JsonObject readJSONFile(String filename) throws Exception {
    URL fileUrl = TabularFileReader.class.getClassLoader().getResource(filename);
    Path filePath = Path.of(String.valueOf(fileUrl));

    String json = Files.readString(filePath, StandardCharsets.US_ASCII);

    return Jsoner.deserialize(json, new JsonObject());
  }
}
