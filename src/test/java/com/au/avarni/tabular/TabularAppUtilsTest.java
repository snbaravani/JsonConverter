package com.au.avarni.tabular;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TabularAppUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"FY21", "2021"})
    public void getFinancialYear_ValidYear_ReturnsCompleteYear(String inputYear) {
        Integer expected = 2021;
        Integer actual = TabularAppUtils.getFinancialYear(inputYear);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void cleanseFloat_InvalidValue_ReturnsNull(String inputStr) {
        Float actual = TabularAppUtils.cleanseFloat(inputStr);

        assertNull(actual);
    }

    @Test
    public void cleanseFloat_WithSpecialChars_ReturnsFloat() {
        Float expected = 1234560.789f;
        Float actual = TabularAppUtils.cleanseFloat(" 1,234,560. 789³ ");

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " , , ,"})
    public void isEmptyRow_NoData_ReturnsTrue(String inputStr) {
        Boolean actual = TabularAppUtils.isEmptyRow(inputStr);

        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "300", "test , 300 , test"})
    public void isEmptyRow_WithData_ReturnsFalse(String inputStr) {
        Boolean actual = TabularAppUtils.isEmptyRow(inputStr);

        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "Test test 1 2 3",
            "Test scope test 1 2 3",
            "Test scope1 test",
            "Test scope 4 test"
    })
    public void getScopeNumber_NoValidScope_ReturnsNull(String inputStr) {
        Integer actual = TabularAppUtils.getScopeNumber(inputStr);

        assertNull(actual);
    }

    @Test
    public void getScopeNumber_ValidScopeNumber_ReturnsNumber() {
        Integer expected = 3;
        Integer actual = TabularAppUtils.getScopeNumber("Test scope 3 test");

        assertEquals(expected, actual);
    }
}
