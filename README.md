# JsonConverter

This is an experimental project build to test out different forms of JSON conversion.

Currently, it contains the following:

- `converter` - Converts CSVs into JSON
- `tabular` - Converts arrays of tables (defined as strings) into JSON

---

## Goals

The main aim is to test out different methods of taking sample datasets and turning those into a standardised JSON
structure.

The target JSON structure looks like this:

```json
{
  "scope1": {
    "2020": {
      "Natural Gas": 186.8,
      "Diesel": 88.1,
      "total": 274.9
    },
    "2019": {
      "Natural Gas": 117.8,
      "Diesel": 1.6,
      "total": 119.4
    }
  },
  "scope2": {
    "2020": {
      "Fugitive Emissions": 200.9,
      "total": 200.9
    }
  },
  "scope3": {
    "2020": {
      "Purchased Goods and Services": 52376,
      "Fuel & Energy": 605,
      "total": 52981
    }
  }
}
```

The 3 `scope*` fields are all required. The nested year objects can have dynamic names using the full year number.
Within each year object, the `total` field is required. Other fields inside the year objects are optional and dynamic (
any field name is possible based on the source data).

---

## Run the application

The app can be run against CSVs (**converter** app) or JSON files (**tabular** app). The below steps apply to both.

1. Clone the source code from https://github.com/snbaravani/JsonConverter:
   `git clone https://github.com/snbaravani/JsonConverter.git`
2. Make sure your have Docker installed
3. Store your csv/json files in a directory on the host machine
4. Build the docker image: `sh ./build.sh`
5. Run the script, passing your arguments (examples below)
6. The resulting JSON file will be created under a `results` directory in the same folder as your source data files

Example `run.sh` usages:

```shell
# Use the CSV converter to transform ./data/csv/atlassian.csv:
sh ./run.sh csv $(pwd)/data/csv atlassian.csv

# Use the tabular converter to transform ./data/tabular/microsoft.json
sh ./run.sh tabular $(pwd)/data/tabular microsoft.json

# Optionally, tell the app where to write the results JSON file to
sh ./run.sh tabular $(pwd)/data/tabular microsoft.json /custom-dir/results
```

> You may use any path for the source data directory, but it must be an absolute path.

---

## Tabular approach

The "tabular" approach deals with the tables output provided by a crawler application, which is nested inside a JSON
structure looking like this:

```json
{
  "status": {
    "tables": [
      "Table: Table_1\n\n,FY17 , FY18 , FY19 , FY20 ,\nScope 11,2 107,452 , 99,008 , 117,956 , 118,100"
    ]
  }
}
```

### Source data assumptions

Some sample JSON datasets are available under `data/tabular`. The following are assumptions made based on the available
samples.

- Tables data can be found in a fixed location within each JSON structure at: `"status: { "tables": [] }`.
- Tables are represented by strings. Each string starts with: `Table: Table_1\n\n,`, where `Table_1` can end with any
  number (not just 1). It's assumed that the number here refers either to the table number on a page, or as a way of
  linking multiple parts of a table together, if the table spans multiple pages.
- New line characters `\n` are used to break up rows. Commas surrounded by spaces ` , ` are used to break up cells.
  Therefore, this structure closely resembles CSV.
- Values can be either strings or numbers. Strings will be used for column and row headings. Numbers will be present in
  cells, and these could either be whole numbers, fractional numbers or percentages. They might represent a single value
  or a total value of other cells in the table.
- All samples seen so far contain at least one "total" row. This is redundant as all other values in the scope should
  add up to the same figure, so any rows containing *total* are ignored, and the `total` field in the target JSON can be
  calculated based on the fields.
- Some reports don't specify a scope number in every row (e.g., Amazon & Atlassian)

### Transformation process

> "Table of importance" mentioned below refers to any table data that should be factored in to the final JSON output.

To transform the source data into the target JSON structure, the following needs to be done:

1. Locate tables of importance as there will likely be tables in the data that don't relate to emissions figures.
    - There may be one or more tables of importance
2. Extract values data from each important table, ensuring scope and year are captured for each
3. Perform some data cleansing, such as removing commas from large numbers (`3,465,234` to `3465234`) and dropping
   irrelevant values or values that can't be parsed
4. Aggregate and categorise all cleansed data by scope and year
5. Convert the aggregated data into the target JSON structure

### Implementation Notes

- Regular expressions have been heavily used to perform string pattern-matching and data cleansing.
- The config inside `src/main/resources/config.properties` provides some basic control over the transformation process.
    - `tabularApp.onlyFirstTable=true` will stop the transformer from processing any tables after the first found one
      containing emissions data. This was done for cases like Microsoft's report where they include many tables
      featuring the same column headings (which are used to determine if a table is relevant), but different kinds of
      values in each table. Enabling this limits the parsing to the first matched table, which might ignore other tables
      in the raw data that should be combined with the first match.
    - `tabularApp.excludeTotalValues=true` tells the transformer to try to calculate if a cell's value is actually a
      total value of everything within a scope. This was added as different companies print their scope totals in
      different formats, sometimes mixing them with individual item values. Enabling this may result in false-positive
      matches as a value that isn't technically a total might still add up to the total of all other fields in the
      scope.

### Caveats & Areas for improvement

- Accuracy of results is hard (next to impossible) to achieve 100% in across all PDFs with a completely dynamic solution
  requiring no vendor-specific configurations. To improve results, it's recommended to employ some form of
  vendor-specific regular expressions or even business logic where there are wide variances in table structure.
