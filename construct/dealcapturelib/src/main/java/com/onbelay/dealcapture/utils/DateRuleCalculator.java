package com.onbelay.dealcapture.utils;

import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;

import java.time.LocalDate;

public class DateRuleCalculator {

    private OptionExpiryDateRuleToken expiryDateRuleToken;
    private int dayOffset = 0;
    private int monthOffset = 0;

    public static DateRuleCalculatorBuilder build() {
         return new DateRuleCalculatorBuilder();
    }



    public DateRuleCalculator() {
    }

    public DateRuleCalculator(OptionExpiryDateRuleToken expiryDateRuleToken) {
        this.expiryDateRuleToken = expiryDateRuleToken;
    }

    public DateRuleCalculator(
            OptionExpiryDateRuleToken expiryDateRuleToken,
            int monthOffset) {
        this.expiryDateRuleToken = expiryDateRuleToken;
        this.monthOffset = monthOffset;
    }

    public DateRuleCalculator(
            OptionExpiryDateRuleToken expiryDateRuleToken,
            int monthOffset,
            int dayOffset) {
        this.expiryDateRuleToken = expiryDateRuleToken;
        this.dayOffset = dayOffset;
        this.monthOffset = monthOffset;
    }

    public LocalDate calculateDate(LocalDate dateIn) {
        return dateIn.plusDays(dayOffset).plusMonths(monthOffset);
    }

    public OptionExpiryDateRuleToken getExpiryDateRuleToken() {
        return expiryDateRuleToken;
    }

    public int getDayOffset() {
        return dayOffset;
    }

    public int getMonthOffset() {
        return monthOffset;
    }

    void setExpiryDateRuleToken(OptionExpiryDateRuleToken expiryDateRuleToken) {
        this.expiryDateRuleToken = expiryDateRuleToken;
    }

    void setDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    void setMonthOffset(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
