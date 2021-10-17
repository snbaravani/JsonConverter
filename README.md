# JsonConverter

This is an experimental project build to test out different forms of JSON conversion.

Currently, it contains the following:

- `converter` - Converts CSVs into JSON
- `tabular` - Converts arrays of tables (defined as strings) into JSON

## Goals

The main aim is to test out different methods of taking sample datasets and turning those into a standardised
JSON structure.

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
Within each year object, the `total` field is required. Other fields inside the year objects are optional and
dynamic (any field name is possible based on the source data).

## Tabular approach

The "tabular" approach deals with the tables output provided by a crawler application, which is nested inside
a JSON structure looking like this:

```json
{
  "status": {
    "tables": [
      "Table: Table_1\n\n,FY17 , FY18 , FY19 , FY20 ,\nScope 11,2 107,452 , 99,008 , 117,956 , 118,100"
    ]
  }
}
```

### Source data analysis

The following are assumptions made based on the sample data sets available.

Tables are represented by strings. Each string starts with: `Table: Table_1\n\n,`, where `Table_1` can end with
any number (not just 1). It's assumed that the number here refers either to the table number on a page, or as a way
of linking multiple parts of a table together, if the table spans multiple pages.

New line characters `\n` are used to break up rows. Commas surrounded by spaces ` , ` are used to break up cells.
Therefore, this structure closely resembles CSV.

### Transformation

To transform the data into the target JSON structure, the following needs to be done:

1. Locate tables of importance as there will likely be tables in the data that don't relate to emissions figures
2. Extract values data from each important table, ensuring scope and year are captured for each
3. Perform some data cleansing, such as removing commas from large numbers and dropping irrelevant values 
   or values that can't be parsed
4. Aggregate and categorise all cleansed data by scope and year
5. Convert the aggregated data into the target JSON structure
