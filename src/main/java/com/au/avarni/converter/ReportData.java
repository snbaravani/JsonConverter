package com.au.avarni.converter;

/**
 * Data holder
 */
public class ReportData {

    private String scope;

    private String finYear;

    private String label;

    private Double value;

    public String getFinYear() {
        return finYear;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setFinYear(String finYear) {
        this.finYear = finYear;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
