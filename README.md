# JsonConverter

This is an experimental project build to test out different forms of JSON conversion.

Currently, it contains the following applications:

**converter** - Converts CSVs into JSON. Documentation about this approach is here:
[CSV_APPROACH.md](./CSV_APPROACH.md).

**tabular** - Converts arrays of tables (defined as strings) into JSON. Documentation about this approach is here:
[TABULAR_APPROACH.md](./TABULAR_APPROACH.md).

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
3. Store your csv/json files in a directory on the host machine (examples can be found under the `data` directory)
4. Build the docker image: `sh ./build.sh`
5. Run the script (`run.sh`), passing your arguments (examples below)
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
