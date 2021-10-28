package com.au.avarni.converter;

import com.au.avarni.DataTransformerCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.au.avarni.converter.AppUtils.*;

/**
 * AWS Textract is a tool that is similar to Avarni’s crawler. It takes sustainability reports and generates
 * relevant short reports in the csv format.
 * <p>
 * We have also explored this tool and developed an application that transforms these csv’s into Json format. This application will be handy
 * if team at Avarni wants to use this tool in the future.
 * <p>
 * We have tested this app with reports from 3 companies (Amazon,Attlassian,Microsoft) that are not entirely similar and tried to produce Json files.
 * These are available in the resources folder
 */
public class JsonTransformer {

    private static Map<String, TreeMap<String, TreeMap<String, Double>>> scopesData = new TreeMap<>(); // Scope->Fy->Labels

    private static Map<String, Integer> finYears = new TreeMap<>();

    private static List<ReportData> reportDataList = new ArrayList<>();

    private static int processedRows;

    public static void readCsvs(File file, String resultsPath) {
        System.out.println("<==readCsvs==>");
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> data = reader.readAll();
            getFinYears(data.get(0));
            getDataByScope(data);
            createHashMap();

            String jsonStr = new ObjectMapper().writeValueAsString(scopesData);
            String fileName = "csv-" + file.getName().replace(".csv", ".json");

            DataTransformerCommon.writeJSONFile(resultsPath, fileName, jsonStr);

            clearData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getDataByScope(List<String[]> data) {
        getScopeDataFromCsv(data, "Scope 1");
        getScopeDataFromCsv(data, "Scope 2");
        getScopeDataFromCsv(data, "Scope 3");
    }

    private static void getScopeDataFromCsv(List<String[]> data, String scopeNumber) {
        boolean scopeNum = false, scopeBracket = false;
        for (int i = processedRows + 1; i < data.size(); i++) {
            String[] row = data.get(i);
            String val = row[0];
            String scopeStar = scopeNumber.toLowerCase() + "*";
            if (scopeNumber == "Scope 1" && (containsScope2(val) || containsScope3(val))) { // Another Scope so dont process
                return;
            } else if (scopeNumber == "Scope 2" && (containsScope1(val) || containsScope3(val))) {// Another Scope so dont process
                return;
            } else if (scopeNumber == "Scope 3" && (containsScope1(val) || containsScope2(val))) {// Another Scope so dont process
                return;
            } else if (row[0].contains("(" + scopeNumber + ")") || scopeBracket) { // Amazon pattern, Ex: "Emissions from Purchased Electricity (Scope 2)
                scopeBracket = true;
                createDataset(scopeStar, row[0], row);

            } else if (val != null && (scopeNum || val.contains(scopeNumber) || val.contains(scopeNumber.toUpperCase()))) {
                scopeNum = true;
                String label =row[0];
                if (label.startsWith(scopeNumber) && row[0].length() > 15) {
                    label = label.replace(scopeNumber, ""); //Remove 'Scope x from the label
                    if(label.contains("-")){
                        label= label.replaceFirst("-","");
                    }
                    String value = row[2];
                    if (label != null && value != null && !label.isEmpty() && !value.isEmpty()) {
                        createDataset(scopeStar, label.strip(), row);
                    }

                } else if (!row[0].contains(scopeNumber.toUpperCase()) && !row[0].contains("(" + scopeNumber + ")")) {
                    scopeNum = true;
                    createDataset(scopeStar, row[0], row);
                }
            }
            processedRows = i;
        }
    }


    private static void createDataset(String scopeStar, String label, String[] row) {

        for (int i = 0; i < finYears.keySet().size(); i++) {
            ReportData data = new ReportData();
            int index = finYears.get(finYears.keySet().toArray()[i]); // get the column value for that fin year ex: FY19 may be 1
            String val = row[index];
            if (val == null || val.isEmpty()) {
                continue;
            }
            double numericVal = (val != null && val.contains(",")) ? Double.parseDouble(val.replaceAll(",", "")) : Double.parseDouble(val);
            data.setScope(scopeStar);
            String finYear = finYears.keySet().toArray()[i].toString();
            finYear = (finYear != null && finYear.contains("FY"))?finYear.replace("FY","20") : finYear;
            data.setFinYear(finYear+"*");
            data.setLabel(label);
            data.setLabel(label);
            data.setValue(numericVal);
            reportDataList.add(data);
        }


    }

    /**
     * Creating hashmaps in the foll format
     *
     * Scope1 > 2019 -> goal 1
     *               ->2 goal 2
     *
     * Scope1 > 2020 -> goal 1
     *                -> goal 2
     *
     */
    private static void createHashMap() {
        TreeMap<String, TreeMap<String, Double>> dataByFinYear;

        for (int i = 0; i < reportDataList.size(); i++) {
            ReportData data = reportDataList.get(i);
            String scope = data.getScope();
            String fy = data.getFinYear();
            dataByFinYear = scopesData.get(scope);
            if (dataByFinYear == null) {
                dataByFinYear = new TreeMap<>();
            }
            TreeMap<String, Double> dataByLabelMap = dataByFinYear.get(fy);
            if (dataByLabelMap == null) {
                dataByLabelMap = new TreeMap<>();
            }
            Double total = (dataByLabelMap.get("total") != null) ? dataByLabelMap.get("total") : 0.0;

            dataByLabelMap.put(data.getLabel(), data.getValue());
            total = total + data.getValue();
            dataByLabelMap.put("total", Math.round(total * 100.0) / 100.0);

            dataByFinYear.put(fy, dataByLabelMap);
            scopesData.put(scope, dataByFinYear);
        }

    }


    private static void clearData() {
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
