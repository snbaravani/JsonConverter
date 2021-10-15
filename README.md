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
