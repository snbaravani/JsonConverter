package com.au.avarni.tabular.objects;

import java.util.HashMap;

public class ScopeObject {

    // The name of the scope to be used in the JSON field name
    private final String label;

    // Map of years to their values
    private final HashMap<Integer, YearObject> yearValues = new HashMap<>();

    /**
     * Initialise the scope object with an index number, used to create the label.
     *
     * @param index A single-digit scope number
     */
    public ScopeObject(Integer index) {
        this.label = "scope" + index;
    }

    /**
     * Return a map containing all child year maps of values
     *
     * @return Map of year value maps
     */
    public HashMap<Integer, HashMap<String, Float>> getYearsMap() {
        HashMap<Integer, HashMap<String, Float>> yearsMap = new HashMap<>();

        yearValues.forEach((year, values) -> {
            yearsMap.put(year, values.getValues());
        });

        return yearsMap;
    }

    /**
     * Sets a value inside a year object based on the year number and name & value
     *
     * @param year  4 digit year number
     * @param name  Name/key of the value being set
     * @param value Numeric value to set
     */
    public void setYearValue(Integer year, String name, Float value) {
        if (!yearValues.containsKey(year)) {
            // Create the year object if it doesn't exist already
            yearValues.put(year, new YearObject(year));
        }

        yearValues.get(year).setValue(name, value);
    }
}
