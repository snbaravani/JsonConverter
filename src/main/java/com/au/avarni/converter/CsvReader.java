package com.au.avarni.converter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URL;
import java.util.*;

public class CsvReader {

    private static Map<String, String> finYearContent = new HashMap<>();

    private static Map<String, Map<String,String>> scopesData = new HashMap<>();

    private static List<String> finYears = new ArrayList<>();

    private static List<String> scopeList = Collections.singletonList("{Scope 1, Scope 2, Scope 3}");

    private static int processedRows ;

    public static void readCsvs() throws IOException, CsvException {
        URL fileUrl = CsvReader.class.getClassLoader().getResource("atlassian.csv"); //atlassian.csv,amazon.csv,microsoft.csv
        try (CSVReader reader = new CSVReader(new FileReader(fileUrl.getFile()))) {
            List<String[]> data = reader.readAll();
            getFinYears(data.get(0));
            getDataByScope(data);
            scopesData = scopesData;
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
        for (int i = 1; i < data.size(); i ++ ) {
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                if (containsScope1(row[j])) {
                    getScope1Data(data, i, "Scope 1");
                    i = processedRows;
                    continue;
                } else if (containsScope2(row[j])) {

                } else {

                }
            }
        }
    }

    private static void getScope1Data(List<String[]> data, int currentRow, String scopeNumber) {
        boolean scopeNum = false;
        for (int i = currentRow; i < data.size(); i ++ ) {
            String[] row = data.get(i);
                if (row[0] != null && scopeList.contains(row[0]) && row[0] != scopeNumber) {
                    return;
                } else if (containsScope1(row[0]) || scopeNum) {
                    scopeNum = true;
                    Map<String, String> goals =  scopesData.get(scopeNumber);
                    if(goals == null){
                        goals = new HashMap<>();
                    }
                     if(row[0].startsWith("Scope 1") && row[0].length() > 15){  // "Scope 1 Scope1-based² ","2,697,554 ","10000 ","3,557,518 ","4,102,445 ",
                       String label =   row[0].replace(scopeNumber, ""); //Remove 'Scope x from the label
                       String value = row[2];
                       goals.put(label,value);

                     } else  if(!row[0].contains(scopeNumber.toUpperCase()) && !row[0].contains("("+scopeNumber+")")){ // Avoid the Scope heading and consider only lables or goals ex: Scope 1
                             goals.put(row[0],row[2]);

                     } else if(!row[0].contains("("+scopeNumber+")")){
                         continue;
                     }
                    scopesData.put(scopeNumber,goals);
                    processedRows = i;

                }

            }


    }

    private static boolean containsScope1(String value){
        if(value != null && (value.contains("Scope1") ||value.contains("SCOPE 1")   || value.contains("(Scope 1)") ||
                value.contains("(SCOPE 1)") ) ){
            return true;
        }
        return false;
    }

    private static boolean containsScope2(String value){
        if(value != null && (value.contains("Scope 2") ||value.contains("SCOPE 2")   || value.contains("(Scope 2)") ||
                value.contains("(SCOPE 2)") ) ){
            return true;
        }
        return false;
    }

    private static boolean containsScope3(String value){
        if(value != null && (value.contains("Scope 3") ||value.contains("SCOPE 3")   || value.contains("(Scope 3)") ||
                value.contains("(SCOPE 3)") ) ){
            return true;
        }
        return false;
    }
}
