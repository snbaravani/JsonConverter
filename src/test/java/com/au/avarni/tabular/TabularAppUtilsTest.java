package com.au.avarni.tabular;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TabularAppUtilsTest {

    @Test
    public void getFinancialYear_FYValue_ReturnsYear() {
        Integer expected = 2021;
        Integer actual = TabularAppUtils.getFinancialYear("FY21");

        assertEquals(expected, actual);
    }

    @Test
    public void getFinancialYear_4DigitYear_ReturnsYear() {
        Integer expected = 2021;
        Integer actual = TabularAppUtils.getFinancialYear("2021");

        assertEquals(expected, actual);
    }

    @Test
    public void cleanseFloat_SpecialChars_ReturnsFloat() {
        Float expected = 1234560.789f;
        Float actual = TabularAppUtils.cleanseFloat(" 1,234,560. 789³ ");

        assertEquals(expected, actual);
    }
}
