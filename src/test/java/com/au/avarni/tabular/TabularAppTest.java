package com.au.avarni.tabular;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class TabularAppTest {

    static String expectedMicrosoftJSON;
    static File inputCrawlerJSONFile;

    @BeforeAll
    public static void setup() throws Exception {
        expectedMicrosoftJSON = Files.readString(
                Path.of(getResourceFilePath("expected-microsoft.json")),
                StandardCharsets.US_ASCII
        );

        inputCrawlerJSONFile = new File(getResourceFilePath("input-crawler-microsoft.json"));
    }

    @Test
    void convertJSON_ValidFile_HasExpectedJSON() throws Exception {
        String actual = TabularApp.getJSONFromInput(inputCrawlerJSONFile);

        JSONAssert.assertEquals(expectedMicrosoftJSON, actual, false);
    }

    private static String getResourceFilePath(String resourceFile) {
        return Objects.requireNonNull(TabularAppTest.class
                        .getClassLoader()
                        .getResource(resourceFile))
                .getFile();
    }
}
