package com.au.avarni.converter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.au.avarni.converter.AppUtils.*;

public class CsvReader {


    private static Map<String, Map<String,Double>> scopesData = new TreeMap<>();

    private static List<String> finYears = new ArrayList<>();

    private static int processedRows ;

    private static Double totalVal = 0.0;

    public static void readCsvs() throws IOException, CsvException {
        URL fileUrl = CsvReader.class.getClassLoader().getResource("atlassian.csv"); //atlassian.csv,amazon.csv,microsoft.csv
        try (CSVReader reader = new CSVReader(new FileReader(fileUrl.getFile()))) {
            List<String[]> data = reader.readAll();
            getFinYears(data.get(0));
            getDataByScope(data);
            createJson("atlassian.json",scopesData);
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
            totalVal = 0.0; // reset to 0 for every scope
            getScopeData(data,  "Scope 2");
            totalVal =0.0;
            getScopeData(data,  "Scope 3");

    }

    private static void getScopeData(List<String[]> data, String scopeNumber) {
        boolean scopeNum = false, scopeBracket = false;
        for (int i = processedRows+1; i < data.size(); i ++ ) {
            String[] row = data.get(i);
            String val = row[0];
            String scopeStar = scopeNumber.toLowerCase()+"*";
            if(scopeNumber == "Scope 1" && (containsScope2(val) || containsScope3(val))){ // Another Scope so dont process
                    return;
             } else if (scopeNumber == "Scope 2" && (containsScope1(val) || containsScope3(val))) {// Another Scope so dont process
                     return;
             } else if (scopeNumber == "Scope 3" && (containsScope1(val) || containsScope2(val))){// Another Scope so dont process
                   return;
             } else if(row[0].contains("("+scopeNumber+")") || scopeBracket){ // Amazon pattern, Ex: "Emissions from Purchased Electricity (Scope 2)
                scopeBracket  = true;
                createDataForAmazonTemplate(scopeNumber,row[0],row[1] );

            } else if ( val != null && (scopeNum || val.contains(scopeNumber) || val.contains(scopeNumber.toUpperCase()) )) {//Microsoft pattern,  "Scope 1 Scope1-based² ","2,697,554 ","10000 ","3,557,518 ","4,102,445 ",
                    scopeNum = true;
                     if(row[0].startsWith(scopeNumber) && row[0].length() > 15){
                       String label =   row[0].replace(scopeNumber, ""); //Remove 'Scope x from the label
                       String value = row[2];
                       if(label != null && value != null && label.length() > 2 && value.length() > 2){
                           createDataForMicrosoftTemplate(scopeStar, label, value);
                       }

                     } else  if(!row[0].contains(scopeNumber.toUpperCase()) && !row[0].contains("("+scopeNumber+")")){ // Attlassian,"SCOPE 1 ","119.4 ","0.14% ","274.9 ","0.4% ",
                           scopeNum = true;                                                                                                          // "Natural Gas ","117.8 ","0.13% ","186.8 ","0.2% ",
                           createDataForAttlassianTemplate(scopeStar, row[0] , row[3]);

                     }
                }
                     processedRows = i;
            }
    }

    private static void createDataForAmazonTemplate(String scopeStar, String label, String value){
        Map<String, Double> goals =  scopesData.get(scopeStar);
        if(goals == null){
            goals = new TreeMap<>();
        }
        if (label != null && value != null && label.length() > 2 && value.length() > 2) {
            double numericVal = Double.parseDouble(value);
            totalVal+= numericVal;
            goals.put(label, numericVal);
            goals.put("total", totalVal);
            scopesData.put(scopeStar, goals);
        }

    }
    private static void createDataForMicrosoftTemplate(String scopeStar, String label, String value){
        Map<String, Double> goals =  scopesData.get(scopeStar);
        double numericVal = Double.parseDouble(value);
        totalVal+= numericVal;
        goals.put(label,numericVal);
        scopesData.put(scopeStar,goals);
        goals.put("total", totalVal);
    }

    private static void createDataForAttlassianTemplate(String scopeStar, String label, String value){
        Map<String, Double> goals =  scopesData.get(scopeStar);
        double numericVal = Double.parseDouble(value);
        totalVal+= numericVal;
        goals.put(label,numericVal);
        scopesData.put(scopeStar,goals);
        goals.put("total", totalVal);
    }

}
