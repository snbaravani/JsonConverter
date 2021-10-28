# Tabular Approach

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

Other JSON fields in this data are irrelevant for the purposes of this application and are ignored.

## Source data assumptions

Some sample JSON datasets are available under `data/tabular`. The following are assumptions made based on the available
samples.

- Tables data can be found in a fixed location within each JSON structure at: `"status: { "tables": [] }`.
- Tables are represented by strings. Each string starts with: `Table: Table_1\n\n,`, where `Table_1` can end with any
  number (not just 1). It's assumed that the number here refers either to the table number on a page, or as a way of
  linking multiple parts of a table together, if the table spans multiple pages.
- New line characters `\n` are used to break up rows. Commas surrounded by spaces ` , ` are used to break up cells.
  Therefore, this structure closely resembles CSV.
- Values can be either strings or numbers. Strings are assumed for column and row headings. Numbers will be present in
  cells, and these could be whole numbers, fractional numbers or large numbers containing commas. They might represent a
  single value or a total value of other cells in the table.
- All samples seen so far contain at least one "total" row. This is redundant as all other values in the scope should
  add up to the same figure, so any rows containing *total* are ignored, and the `total` field in the target JSON gets
  calculated based on the values encountered in a scope's year.
- Some reports don't specify a scope number in every row (e.g., Amazon & Atlassian).

## Transformation process

> "Table of importance" mentioned below refers to any table data that should be factored in to the final JSON output.

To transform the source data into the target JSON structure, the following needs to be done:

1. Locate tables of importance as there will likely be tables in the data that don't relate to emissions figures.
    - There may be one or more tables of importance identified
2. Extract values from each important table, ensuring scope and year are captured against each
3. Perform some data cleansing, such as removing commas from large numbers (`3,465,234` to `3465234`) and skipping
   irrelevant values or values that can't be parsed
4. Aggregate and categorise all cleansed data by scope and year
5. Convert the aggregated data into the target JSON structure & write it to a file

## Implementation Notes

- Regular expressions have been heavily used to perform string pattern-matching and data cleansing. This was done
  because they are portable and can be dropped into any other program. The tradeoff is they are hard to read.
- The config inside `config/tabularConfig.yml` provides some basic controls over the transformation process. Do check
  out the comments inside that file for an explanation of each control.

## Caveats & Areas for improvement

### Field names

Fields containing the text "Scope N" can be cleaned up, however some fields contain nothing but "Scope N", so these end
up becoming empty. Better logic around this can detect cases like these and skip the cleansing.

Most rows have no clear separation between the label and the first value. This was partially solved with regex, but
comes at the cost of some label format issues, like labels being cut off

### Accuracy of results

It's hard (next to impossible) to achieve 100% accuracy across all PDF table formats with a completely dynamic solution
requiring no vendor-specific configurations. To improve results, it's recommended to employ some form of vendor-specific
regular expressions or even business logic where there are wide variances in table structure.

`config/tabularConfig.yml` may help when running the program against a specific set of data and this could be expanded
to add control over regular expressions used in the app.
