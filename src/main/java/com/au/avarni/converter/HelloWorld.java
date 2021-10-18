package com.au.avarni.converter;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;

public class HelloWorld {
    public static void main(String[] args) throws IOException, CsvException {
        CsvReader.readCsvs();
    }
}
