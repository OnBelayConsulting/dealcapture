package com.onbelay.dealcapture.utils;

import com.onbelay.dealcapture.common.enums.DateOperator;
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;

public class DateRuleCalculatorBuilder {

    private DateRuleCalculator calculator = new DateRuleCalculator();

    private DateOperator monthDateOperator = DateOperator.PLUS;
    private DateOperator dayDateOperator = DateOperator.PLUS;

    public DateRuleCalculatorBuilder withDateRule(OptionExpiryDateRuleToken token) {
        calculator.setExpiryDateRuleToken(token);
        return this;
    }

    public DateRuleCalculatorBuilder withMonthOperator(DateOperator operator) {
        this.monthDateOperator = operator;
        return this;
    }

    public DateRuleCalculatorBuilder withMonthOffset(int monthOffset) {
        calculator.setMonthOffset(monthOffset);
        return this;
    }


    public DateRuleCalculatorBuilder withDayOperator(DateOperator operator) {
        this.dayDateOperator = operator;
        return this;
    }


    public DateRuleCalculatorBuilder withDayOffset(int dayOffset) {
        calculator.setDayOffset(dayOffset);
        return this;
    }

    public DateRuleCalculator build() {
        if (monthDateOperator == DateOperator.MINUS) {
            calculator.setMonthOffset(calculator.getMonthOffset() * -1);
        }
        if (dayDateOperator == DateOperator.MINUS) {
            calculator.setDayOffset(calculator.getDayOffset() * -1);
        }
        return calculator;
    }

}
