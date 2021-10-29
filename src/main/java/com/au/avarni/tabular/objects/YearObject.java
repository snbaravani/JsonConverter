package com.au.avarni.tabular.objects;

import com.au.avarni.tabular.TabularAppConfig;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class YearObject {

    // Year number this object relates to
    private final String year;

    // Map of values in this year, initialised with a zero total value
    private final TreeMap<String, BigDecimal> values = new TreeMap<>() {{
        put("total", BigDecimal.ZERO);
    }};

    /**
     * Initialise the year object with a 4 digit year number
     *
     * @param year String of a 4 digit year
     */
    public YearObject(String year) {
        this.year = year;
    }

    public TreeMap<String, BigDecimal> getValues() {
        return values;
    }

    /**
     * Set a single value in the year object and recalculate the total.
     *
     * @param name  Name/key of the value
     * @param value Numeric value to set
     */
    public void setValue(String name, BigDecimal value) {
        values.put(name, value);

        updateTotal();
    }

    /**
     * Attempts to remove values that are actually totals of other fields. Excludes the `total` field.
     */
    public void removeRawTotalValue() {
        // Do nothing if there's only a `total` and one other field
        if (values.size() < 3) {
            return;
        }

        try {
            var config = TabularAppConfig.getAppConfig();

            if (config.getExcludeTotalValues()) {
                String keyToRemove = null;

                // Loop within a loop here is to compare each value against the total of
                // all other values in the map to determine if the value is actually a total
                // of the rest. This can then be removed, as `total` is calculated dynamically.
                for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
                    String key = entry.getKey();
                    BigDecimal value = entry.getValue();

                    // Ignore the dynamic `total` field
                    if (key.equals("total")) {
                        continue;
                    }

                    BigDecimal otherFieldsTotal = BigDecimal.ZERO;

                    for (Map.Entry<String, BigDecimal> otherEntry : values.entrySet()) {
                        String otherKey = otherEntry.getKey();
                        BigDecimal otherValue = otherEntry.getValue();

                        // Ignore the dynamic `total` field,
                        // or if this is the same key we're checking in the parent loop
                        if (otherKey.equals("total") || otherKey.equals(key)) {
                            continue;
                        }

                        otherFieldsTotal = otherFieldsTotal.add(otherValue);
                    }

                    if (otherFieldsTotal.equals(value)) {
                        keyToRemove = key;
                    }
                }

                if (keyToRemove != null) {
                    values.remove(keyToRemove);

                    updateTotal();
                }
            }
        } catch (Exception e) {
            System.out.println("Error during removeRawTotalValue");
            e.printStackTrace();
        }
    }

    /**
     * Recalculate the year's total value
     */
    private void updateTotal() {
        BigDecimal newTotal = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
            String key = entry.getKey();
            BigDecimal value = entry.getValue();

            if (key.equals("total")) {
                continue;
            }

            newTotal = newTotal.add(value);
        }

        values.put("total", newTotal);
    }
}
