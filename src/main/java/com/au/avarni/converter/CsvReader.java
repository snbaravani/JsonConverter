package com.au.avarni.converter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.*;

public class CsvReader {

    private static Map<String, String> finYearContent = new HashMap<>();

    private static Map<String, List<String>> scopes = new HashMap<>();

    private static List<String> finYears = new ArrayList<>();

    public static void readCsvs() throws IOException, CsvException {
        ClassLoader classLoader = CsvReader.class.getClassLoader();
        File file = new File(classLoader.getResource("amazon.csv").getFile());
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> data = reader.readAll();
            getFinYears(data.get(0));
            getDataByScope(data);
            data.forEach(x -> System.out.println(Arrays.toString(x)));
        }
    }

    private static void getFinYears(String[] firstLine) {
        for (int i = 0; i < firstLine.length; i++) {
            if (firstLine[i] != null && firstLine[i].length() > 0 &&
                    !firstLine[i].contains("%")) {
                finYears.add(firstLine[i]);
            }
        }
    }

    private static void getDataByScope(List<String[]> data) {
        boolean scope1, scope2, scope3;
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                if (row[j] != null && row[j].contains("Scope 1")) {

                    getScope1Data(row, i);

                } else if (row[i] != null && row[i].contains("Scope 1")) {

                } else {

                }
            }
        }
    }

    private static void getScope1Data(String[] row, int i) {
        boolean scope1 = false;

        for (int j = 0; j < row.length; j++) {
            if (row[i] != null && row[i].contains("Scope 2")  || row[i].contains("Scope 3")) {
                return;
            }
            else if (row[j] != null && (row[j].contains("Scope 1") && row[j].startsWith("Scope 1") || row[i].contains("(Scope 1)")) || scope1) {
                scope1 = true;
            }
        }

    }
}
