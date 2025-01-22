package com.onbelay.utils;

import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.utils.DateRuleCalculator;
import com.onbelay.dealcapture.utils.DateRuleParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateRuleParserTest {


    @Test
    public void testTokenOnly() {

        String token = OptionExpiryDateRuleToken.POSITION_START_DATE.getCode();
        DateRuleParser parser = new DateRuleParser(token);

        DateRuleCalculator calculator = parser.calculateDateRule();
        assertEquals(OptionExpiryDateRuleToken.POSITION_START_DATE, calculator.getExpiryDateRuleToken());
    }


    @Test
    public void testStartWithPositiveMthOffset() {

        String token = OptionExpiryDateRuleToken.POSITION_START_DATE.getCode() + " + 5";
        DateRuleParser parser = new DateRuleParser(token);

        DateRuleCalculator calculator = parser.calculateDateRule();
        assertEquals(OptionExpiryDateRuleToken.POSITION_START_DATE, calculator.getExpiryDateRuleToken());
        assertEquals(5, calculator.getMonthOffset());
    }


    @Test
    public void testStartWithNegativeMthOffset() {

        String token = OptionExpiryDateRuleToken.POSITION_START_DATE.getCode() + " - 5";
        DateRuleParser parser = new DateRuleParser(token);

        DateRuleCalculator calculator = parser.calculateDateRule();
        assertEquals(OptionExpiryDateRuleToken.POSITION_START_DATE, calculator.getExpiryDateRuleToken());
        assertEquals(-5, calculator.getMonthOffset());
    }



    @Test
    public void testWithMonthAndDay() {

        String token = OptionExpiryDateRuleToken.POSITION_START_DATE.getCode() + " - 5 + 2";
        DateRuleParser parser = new DateRuleParser(token);

        DateRuleCalculator calculator = parser.calculateDateRule();
        assertEquals(OptionExpiryDateRuleToken.POSITION_START_DATE, calculator.getExpiryDateRuleToken());
        assertEquals(-5, calculator.getMonthOffset());
        assertEquals(2, calculator.getDayOffset());
    }

}
