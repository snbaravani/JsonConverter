package com.au.avarni;


import com.au.avarni.converter.JsonTransformer;

import java.io.File;

public class DataTransformerApp {
    public static void main(String[] args) {
        System.out.println("DataTransformerApp:"+args.length+"<>"+args[0]+"<===>"+args[1]);
        if(!validateInputArgs(args)) {
            System.exit(0);
        }
        if(args[0].equals("tab")){
                System.out.println("\n Calling Tabular transformer ! \n");
          } else if(args[0].equals("csv")){
                System.out.println("\n Calling CSV transformer ! \n");
                JsonTransformer.readCsvs( new File(args[1]));
          }
        }


    private static boolean validateInputArgs(String[]args){
        if(args.length != 2){
            System.out.println("Wrong arg numbers. Please enter app to run ( 'tab' or 'csv' ) and  file name with location ");
            return false;
        } else if(args[0] == null  || (args[1] == null)) {
            System.out.println("Invalid params");
            return false;
        } else if( !args[0].equals("tab") && !args[0].equals("csv")){
            System.out.println("Please enter app to run ( 'tab' or 'csv' ) and  file name with location ");
            return false;
         } else if(args[1] != "" ){
             File f = new File(args[1]);
             if(!f.isFile()){
                 System.out.println("File "+args[1] +" doesn't exist, please check !");
                 return false;
             }
        }
        return true;
    }
}
