package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.*;
import java.util.stream.Collectors;

public class TabularFileSystem {

    /**
     * Read a single JSON file to a JsonObject without applying any formatting/filtering.
     *
     * @param file JSON file
     * @return A simple JsonObject
     */
    public static JsonObject readJSONFile(File file) throws IOException {
        String json = getFileAsString(file);

        return Jsoner.deserialize(json, new JsonObject());
    }

    /**
     * Reads a file's content into a string.
     *
     * @param file File to be parsed
     * @return String content of the resource file
     */
    private static String getFileAsString(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);

        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
