package com.au.avarni.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Contails utility methods
 */
public class AppUtils {
    public static boolean containsScope1(String value){
        if(value != null && (value.contains("Scope1") ||value.contains("SCOPE 1")   || value.contains("(Scope 1)") ||
                value.contains("(SCOPE 1)") ) ){
            return true;
        }
        return false;
    }

    public static boolean containsScope2(String value){
        if(value != null && (value.contains("Scope 2") ||value.contains("SCOPE 2")   || value.contains("(Scope 2)") ||
                value.contains("(SCOPE 2)") ) ){
            return true;
        }
        return false;
    }

    public static boolean containsScope3(String value){
        if(value != null && (value.contains("Scope 3") ||value.contains("SCOPE 3")   || value.contains("(Scope 3)") ||
                value.contains("(SCOPE 3)") ) ){
            return true;
        }
        return false;
    }

    /**
     * Converts Map into corresponding Json structure and writes into a file
     * @param fileName
     * @param scopesData
     * @throws IOException
     */

    public static void createJson(String fileName, Map<String, TreeMap<String, TreeMap<String, Double>>> scopesData) throws IOException {

        String json = new ObjectMapper().writeValueAsString(scopesData);
        fileName = "/avarni/reports/"+fileName;
        System.out.println("Creating the json file ==>"+fileName);
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(json);
        fileWriter.flush();


    }
}
