package com.au.avarni.converter;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;

/**
 * Converts csvs produced by the AWS Textract to Jsons
 */
public class AwsTextractJsonConverter {
    public static void main(String[] args) throws IOException, CsvException {
        JsonTransformer.readCsvs();
    }
}
