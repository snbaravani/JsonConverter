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
    public void cleanseDouble_InvalidValue_ReturnsNull(String inputStr) {
        Double actual = TabularAppUtils.cleanseDouble(inputStr);

        assertNull(actual);
    }

    @Test
    public void cleanseDouble_WithSpecialChars_ReturnsDouble() {
        Double expected = 1234560.789;
        Double actual = TabularAppUtils.cleanseDouble(" 1,234,560. 789³ ");

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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "123 abc"})
    public void getCellValue_InvalidContent_ReturnsNull(String inputStr) {
        Double actual = TabularAppUtils.getCellValue(inputStr);

        assertNull(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " 1234567.89 ",
            " 1,234,567.89 ",
            "abc 1,234,567.89",
            "abc 1,234,567.89",
            "999 1,234,567.89",
    })
    public void getCellValue_ValidContent_ReturnsNull(String inputStr) {
        Double expected = 1234567.89;
        Double actual = TabularAppUtils.getCellValue(inputStr);

        assertEquals(expected, actual);
    }

    @Test
    public void getCellLabel_NoNumber_ReturnsWholeString() {
        String expected = "123 abc";
        String actual = TabularAppUtils.getRowLabel("123 abc");

        assertEquals(expected, actual);
    }

    @Test
    public void getCellLabel_WithNumber_ReturnsLabel() {
        String expected = "123 abc";
        String actual = TabularAppUtils.getRowLabel(" 123 abc 1,234.56");

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"false", "truez"})
    public void isTrue_NotTrueString_ReturnsFalse(String inputStr) {
        Boolean actual = TabularAppUtils.isTrue(inputStr);

        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "TRUE", "TrUe"})
    public void isTrue_WithTrueString_ReturnsTrue(String inputStr) {
        Boolean actual = TabularAppUtils.isTrue(inputStr);

        assertTrue(actual);
    }
}
