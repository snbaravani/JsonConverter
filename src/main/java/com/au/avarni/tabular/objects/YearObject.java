package com.au.avarni.tabular.objects;

import java.util.HashMap;

public class YearObject {

    // Year number this object relates to
    private final Integer year;

    // Map of values in this year, initialised with a zero total value
    private final HashMap<String, Float> values = new HashMap<>() {{
        put("total", 0f);
    }};

    /**
     * Initialise the year object with a 4 digit year number
     *
     * @param year
     */
    public YearObject(Integer year) {
        this.year = year;
    }

    public HashMap<String, Float> getValues() {
        return values;
    }

    /**
     * Set a single value in the year object and recalculate the total
     *
     * @param name  Name/key of the value
     * @param value Numeric value to set
     */
    public void setValue(String name, Float value) {
        values.put(name, value);

        // Recalculate the year's total value
        values.put("total", values.get("total") + value);
    }
}
