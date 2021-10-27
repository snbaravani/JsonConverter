package com.au.avarni;

import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Common values & methods shared between all implementations
 */
public class DataTransformerCommon {

    /**
     * Writes a string representing valid JSON data to a json file
     *
     * @param resultsPath Path to the directory to write results to
     * @param fileName    The name to give to the json file
     * @param jsonStr     String representing valid JSON
     * @return The full path to the created file
     */
    public static String writeJSONFile(String resultsPath, String fileName, String jsonStr) throws IOException {
        Files.createDirectories(Paths.get(resultsPath));

        String filePath = resultsPath + "/" + fileName;
        System.out.println("Creating the json file ==>" + filePath);
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(Jsoner.prettyPrint(jsonStr));
        fileWriter.flush();

        return filePath;
    }
}
