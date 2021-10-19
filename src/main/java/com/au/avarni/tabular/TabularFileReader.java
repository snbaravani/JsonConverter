package com.au.avarni.tabular;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class TabularFileReader {

    /**
     * Read a single JSON file to a JsonObject without applying any formatting/filtering.
     *
     * @param filename JSON file name
     * @return A simple JsonObject
     */
    public static JsonObject readJSONFile(String filename) throws IOException, URISyntaxException {
        String json = getResourceFileAsString(filename);

        return Jsoner.deserialize(json, new JsonObject());
    }

    /**
     * Reads a resource file's content into a string.
     *
     * @param filename Resource file name
     * @return String content of the resource file
     */
    private static String getResourceFileAsString(String filename) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
