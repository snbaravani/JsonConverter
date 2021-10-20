package com.au.avarni.converter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.au.avarni.converter.AppUtils.*;

public class JsonTransformer {

    private static Map<String, TreeMap<String,TreeMap<String, Double>>> scopesData = new TreeMap<>(); // Scope->Fy->Labels

    private static Map<String,Integer> finYears = new TreeMap<>();

    private static List<ReportData> reportDataList = new ArrayList<>();

    private static int processedRows ;

    public static void readCsvs() throws IOException, CsvException {
       // String[] fileList = {"atlassian.csv","amazon.csv","microsoft.csv"};
        String[] fileList = {"atlassian.csv"};
        for(int i = 0; i<fileList.length;i++){
            System.out.println(fileList[i]);
            URL fileUrl = JsonTransformer.class.getClassLoader().getResource(fileList[i]); //atlassian.csv,amazon.csv,microsoft.csv
            try (CSVReader reader = new CSVReader(new FileReader(fileUrl.getFile()))) {
                List<String[]> data = reader.readAll();
                getFinYears(data.get(0));
                getDataByScope(data);
                createHashMap();
                createJson(fileList[i].replace("csv", "json"),scopesData);
                clearData();
            }
        }
    }

    private static void getDataByScope(List<String[]> data) {
        getScopeDataFromCsv(data,  "Scope 1");
        getScopeDataFromCsv(data,  "Scope 2");
        getScopeDataFromCsv(data,  "Scope 3");
    }

    private static void getScopeDataFromCsv(List<String[]> data, String scopeNumber) {
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
                createDataset(scopeStar, row[0] , row);

            } else if ( val != null && (scopeNum || val.contains(scopeNumber) || val.contains(scopeNumber.toUpperCase()) )) {
                //Microsoft pattern,  "Scope 1 Scope1-based² ","2,697,554 ","10000 ","3,557,518 ","4,102,445 ",
                    scopeNum = true;
                     if(row[0].startsWith(scopeNumber) && row[0].length() > 15){
                       String label =   row[0].replace(scopeNumber, ""); //Remove 'Scope x from the label
                       String value = row[2];
                       if(label != null && value != null && label.length() > 2 && value.length() > 2){
                           createDataset(scopeStar, row[0] , row);
                       }

                     } else  if(!row[0].contains(scopeNumber.toUpperCase()) && !row[0].contains("("+scopeNumber+")")){
                         // Attlassian,"SCOPE 1 ","119.4 ","0.14% ","274.9 ","0.4% ",
                           scopeNum = true;
                           createDataset(scopeStar, row[0] , row);
                     }
                }
                     processedRows = i;
            }
    }


    private static void createDataset(String scopeStar, String label, String[] row){

        for(int i = 0; i< finYears.keySet().size(); i++){
            ReportData  data = new ReportData();
            int index = finYears.get(finYears.keySet().toArray()[i]); // get the column value for that fin year ex: FY19 may be 1
            String val =  row[index];
            if(val == null || val.isEmpty()){
                continue;
            }
            double numericVal = (val != null && val.contains(",")) ? Double.parseDouble(val.replaceAll(",","")) : Double.parseDouble(val);
            data.setScope(scopeStar);
            data.setFinYear(finYears.keySet().toArray()[i].toString());
            data.setLabel(label);
            data.setLabel(label);
            data.setValue(numericVal);
            reportDataList.add(data);
        }


    }
    private static void createHashMap(){
        TreeMap<String,   TreeMap<String, Double>> dataByFinYear;

        for(int i = 0; i<reportDataList.size();i++){
            ReportData  data    = reportDataList.get(i);
            String scope = data.getScope();
            String fy = data.getFinYear();
            dataByFinYear =  scopesData.get(scope);
            if(dataByFinYear == null) {
                dataByFinYear = new TreeMap<>();
            }
            TreeMap<String, Double>  dataByLabelMap =    dataByFinYear.get(fy);
            if(dataByLabelMap == null){
                dataByLabelMap = new TreeMap<>();
            }
            Double total = (dataByLabelMap.get("total") != null)? dataByLabelMap.get("total") :0.0;

            dataByLabelMap.put(data.getLabel(),data.getValue()) ;
            total = total + data.getValue();
            dataByLabelMap.put("total",Math.round(total * 100.0) / 100.0);

            dataByFinYear.put(fy,dataByLabelMap);
            scopesData.put(scope, dataByFinYear);
        }

    }


    private static  void clearData(){
        scopesData.clear();
        processedRows = 1;
    }


    private static void getFinYears(String[] firstLine) {
        for (int i = 0; i < firstLine.length; i++) {
            if (firstLine[i] != null && firstLine[i].length() > 0 &&
                    !firstLine[i].contains("%")) {
                finYears.put(firstLine[i], i);
            }
        }
    }

}
