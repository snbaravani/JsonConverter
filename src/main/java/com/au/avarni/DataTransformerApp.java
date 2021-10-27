package com.au.avarni;

import com.au.avarni.converter.JsonTransformer;
import com.au.avarni.tabular.TabularApp;

import java.io.File;

public class DataTransformerApp {

    private static final String csvName = "csv";
    private static final String tabularName = "tabular";

    public static void main(String[] args) {
        System.out.println("DataTransformerApp:" + args.length + "<>" + args[0] + "<===>" + args[1]);
        if (!validateInputArgs(args)) {
            System.exit(0);
        }

        String resultsPath = args.length > 2 ? args[2] : "/avarni/reports/results";

        if (args[0].equals(tabularName)) {
            System.out.println("\n Calling Tabular transformer ! \n");
            TabularApp.convertJSON(new File(args[1]), resultsPath);
        } else if (args[0].equals(csvName)) {
            System.out.println("\n Calling CSV transformer ! \n");
            JsonTransformer.readCsvs(new File(args[1]), resultsPath);
        }
    }


    private static boolean validateInputArgs(String[] args) {
        String options = "( '" + tabularName + "' or '" + csvName + "' )";

        if (args.length < 2) {
            System.out.println("Wrong arg numbers. Please enter app to run " + options + " and  file name with location ");
            return false;
        } else if (args[0] == null || (args[1] == null)) {
            System.out.println("Invalid params");
            return false;
        } else if (!args[0].equals(tabularName) && !args[0].equals(csvName)) {
            System.out.println("Please enter app to run " + options + " and file name with location ");
            return false;
        } else if (args[1] != "") {
            File f = new File(args[1]);
            if (!f.isFile()) {
                System.out.println("File " + args[1] + " doesn't exist, please check !");
                return false;
            }
        }
        return true;
    }
}
