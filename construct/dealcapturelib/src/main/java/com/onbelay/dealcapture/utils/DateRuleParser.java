package com.onbelay.dealcapture.utils;

import com.onbelay.dealcapture.common.enums.DateOperator;
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;

/**
 * The DateRuleParser parses a string in the format of:
 * <ol>
 *     <li>pstart</li>
 *     <li>pstart + nM</li>
 *     <li>pstart + nM + nD</li>
 * </ol>
 * Where pstart and pend represent position start date or position end date.
 * position start and position end are base on the frequency of the position which for price indices
 * is either daily or monthly.
 *
 */
public class DateRuleParser {

    private String dateRuleAsString;

    public DateRuleParser(String dateRuleAsString) {
        this.dateRuleAsString = dateRuleAsString.toLowerCase().strip();
    }

    public DateRuleCalculator calculateDateRule() {
        if (dateRuleAsString == null || dateRuleAsString.isEmpty()) {
            return new DateRuleCalculator();
        }

        String[] parts = dateRuleAsString.split(" ");

        if (parts.length > 5) {
            return new DateRuleCalculator();
        }

        OptionExpiryDateRuleToken optionExpiryDateRuleToken;
        switch (parts[0]) {
            case "pend":
                optionExpiryDateRuleToken = OptionExpiryDateRuleToken.POSITION_END_DATE;
                break;
            case "pstart":
                optionExpiryDateRuleToken = OptionExpiryDateRuleToken.POSITION_START_DATE;
                break;

            default: return new DateRuleCalculator();
        }

        if (parts.length == 1) {
            return new DateRuleCalculator(optionExpiryDateRuleToken);
        }

        // Return as error
        if (parts.length == 2) {
            return new DateRuleCalculator();
        }


        DateOperator monthDateOperator;
        switch (parts[1]) {
            case "-":
                monthDateOperator = DateOperator.MINUS;
            break;
            case "+":
                monthDateOperator = DateOperator.PLUS;
            break;

            default: return new DateRuleCalculator();
        }

        int month = Integer.parseInt(parts[2]);

        if (parts.length == 3) {
            DateRuleCalculatorBuilder builder = new DateRuleCalculatorBuilder();
            return builder
                    .withDateRule(optionExpiryDateRuleToken)
                    .withMonthOperator(monthDateOperator)
                    .withMonthOffset(month)
                    .build();
        }


        // return as error
        if (parts.length == 4) {
            return new DateRuleCalculator();
        }

        DateOperator dayDateOperator;
        switch (parts[3]) {
            case "-":
                dayDateOperator = DateOperator.MINUS;
                break;
            case "+":
                dayDateOperator = DateOperator.PLUS;
                break;

            default: return new DateRuleCalculator();
        }

        int day = Integer.parseInt(parts[4]);

        DateRuleCalculatorBuilder builder = new DateRuleCalculatorBuilder();
        return builder
                .withDateRule(optionExpiryDateRuleToken)
                .withMonthOperator(monthDateOperator)
                .withMonthOffset(month)
                .withDayOperator(dayDateOperator)
                .withDayOffset(day)
                .build();

    }

}
