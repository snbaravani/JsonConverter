# AWS Crawler's CSV Transformation

Goal: Transforming AWS Textract’s csvs into standardized Json files.

AWS Textract is a tool that is similar to Avarni’s crawler. It takes sustainability reports and generates relevant short
reports in the csv format.

We have also explored this tool (along with Avarni’s crawler) and developed an application that transforms these csv’s
into standardised Json files. This application will be handy if team at Avarni wants to use AWS Textract tool in the
future.

We have tested this app with reports from 3 companies (Amazon,Attlassian,Microsoft) that are not entirely similar and
tried to produce  
standardized Json files.

Challenges and Assumptions:

1. This will work for all reports that are in one of these three formats. Any changes will impact the resulting json
   data
2. Only scope 1, scope 2 and scope 3 are considered.
3. This is not integrated with AWS Textract ; sustainability reports need to be fed into AWS Textract manually and csv’s
   outputted by the Textract need to be fed into this application manually.

# Code flow:

1. Build the financial year header

2. Scan the csv for "scope1" data and build the hashmp with its content and stop once it finds another scope.

3. Repeat step 2 for scope 2 and scope 3 and build a list of scope 1, 2 and 3 datasets

4. Create a hashmpap for each fin year that contains all scopes data as values and each scope will contain its goals as
   values (FY->Scopes->data)

5. Use the Map of maps to build a json file
