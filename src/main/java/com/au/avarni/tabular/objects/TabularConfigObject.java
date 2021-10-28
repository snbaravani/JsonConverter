package com.au.avarni.tabular.objects;

public class TabularConfigObject {

    private Boolean removeLabelScope;
    private Boolean onlyFirstTable;
    private Boolean excludeTotalValues;
    private Boolean cleanLabels;

    public Boolean getRemoveLabelScope() {
        return removeLabelScope;
    }

    public void setRemoveLabelScope(Boolean removeLabelScope) {
        this.removeLabelScope = removeLabelScope;
    }

    public Boolean getOnlyFirstTable() {
        return onlyFirstTable;
    }

    public void setOnlyFirstTable(Boolean onlyFirstTable) {
        this.onlyFirstTable = onlyFirstTable;
    }

    public Boolean getExcludeTotalValues() {
        return excludeTotalValues;
    }

    public void setExcludeTotalValues(Boolean excludeTotalValues) {
        this.excludeTotalValues = excludeTotalValues;
    }

    public Boolean getCleanLabels() {
        return cleanLabels;
    }

    public void setCleanLabels(Boolean cleanLabels) {
        this.cleanLabels = cleanLabels;
    }
}
