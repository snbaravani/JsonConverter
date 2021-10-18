package com.au.avarni.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.simple.JSONObject;

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
        URL fileUrl = CsvReader.class.getClassLoader().getResource("amazon.csv"); //atlassian.csv,amazon.csv,microsoft.csv
        try (CSVReader reader = new CSVReader(new FileReader(fileUrl.getFile()))) {
            List<String[]> data = reader.readAll();
            getFinYears(data.get(0));
            getDataByScope(data);
            scopesData = scopesData;
            createJson();
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
            getScopeData(data,  "Scope 1");
            getScopeData(data,  "Scope 2");
            getScopeData(data,  "Scope 3");

    }

    private static void getScopeData(List<String[]> data, String scopeNumber) {
        boolean scopeNum = false, scopeBracket = false;
        for (int i = processedRows+1; i < data.size(); i ++ ) {
            String[] row = data.get(i);
            String val = row[0];
            if(scopeNumber == "Scope 1" && (containsScope2(val) || containsScope3(val))){ // Another Scope so dont process
                    return;
             } else if (scopeNumber == "Scope 2" && (containsScope1(val) || containsScope3(val))) {// Another Scope so dont process
                     return;
             } else if (scopeNumber == "Scope 3" && (containsScope1(val) || containsScope2(val))){// Another Scope so dont process
                   return;
             } else if(row[0].contains("("+scopeNumber+")") || scopeBracket){ // Amazon pattern, Ex: "Emissions from Purchased Electricity (Scope 2)
                scopeBracket  = true;
                Map<String, String> goals =  scopesData.get(scopeNumber);
                if(goals == null){
                    goals = new HashMap<>();
                }
                 String label = row[0];
                 String value = row[2];
                 if (label != null && value != null && label.length() > 2 && value.length() > 2) {
                      goals.put(label, value);
                      scopesData.put(scopeNumber, goals);
                  }

            } else if ( val != null && (scopeNum || val.contains(scopeNumber) || val.contains(scopeNumber.toUpperCase()) )) {//Microsoft pattern,  "Scope 1 Scope1-based² ","2,697,554 ","10000 ","3,557,518 ","4,102,445 ",
                    scopeNum = true;
                    Map<String, String> goals =  scopesData.get(scopeNumber);
                    if(goals == null){
                        goals = new HashMap<>();
                    }
                     if(row[0].startsWith(scopeNumber) && row[0].length() > 15){
                       String label =   row[0].replace(scopeNumber, ""); //Remove 'Scope x from the label
                       String value = row[2];
                       if(label != null && value != null && label.length() > 2 && value.length() > 2){
                           goals.put(label,value);
                           scopesData.put(scopeNumber,goals);
                       }

                     } else  if(!row[0].contains(scopeNumber.toUpperCase()) && !row[0].contains("("+scopeNumber+")")){ // Attlassian,"SCOPE 1 ","119.4 ","0.14% ","274.9 ","0.4% ",
                           scopeNum = true;                                                                                                          // "Natural Gas ","117.8 ","0.13% ","186.8 ","0.2% ",
                            goals.put(row[0],row[2]);
                           scopesData.put(scopeNumber,goals);

                     } else if(row[0].contains("("+scopeNumber+")")){ // Amazon pattern,
                         continue;
                     }
                }
                     processedRows = i;
            }


    }

    private static void createJson() throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(scopesData);
        System.out.println(json);


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
