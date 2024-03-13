package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DealDayDetail {

    private LocalDate dealDayDate;

    private String dayTypeCodeValue;
    private String daySubTypeCodeValue;

    private BigDecimal day1Value;
    private BigDecimal day2Value;
    private BigDecimal day3Value;
    private BigDecimal day4Value;
    private BigDecimal day5Value;
    private BigDecimal day6Value;
    private BigDecimal day7Value;
    private BigDecimal day8Value;
    private BigDecimal day9Value;
    private BigDecimal day10Value;
    private BigDecimal day11Value;
    private BigDecimal day12Value;
    private BigDecimal day13Value;
    private BigDecimal day14Value;
    private BigDecimal day15Value;
    private BigDecimal day16Value;
    private BigDecimal day17Value;
    private BigDecimal day18Value;
    private BigDecimal day19Value;
    private BigDecimal day20Value;
    private BigDecimal day21Value;
    private BigDecimal day22Value;
    private BigDecimal day23Value;
    private BigDecimal day24Value;
    private BigDecimal day25Value;
    private BigDecimal day26Value;
    private BigDecimal day27Value;
    private BigDecimal day28Value;
    private BigDecimal day29Value;
    private BigDecimal day30Value;
    private BigDecimal day31Value;

    private  HashMap<Integer, Consumer<BigDecimal>> daySetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<BigDecimal>> dayGetterMap = new HashMap<>();

    public DealDayDetail() {
        initializeSetterMap();
        initializeGetterMap();
    }

    public void copyFrom(DealDayDetail copy) {

        if (copy.dealDayDate != null)
            this.dealDayDate = copy.dealDayDate;

        if (copy.dayTypeCodeValue != null)
            this.dayTypeCodeValue = copy.dayTypeCodeValue;

        if (copy.daySubTypeCodeValue != null)
            this.daySubTypeCodeValue = copy.daySubTypeCodeValue;

        for (int i=1; i < 32; i++) {
            this.daySetterMap.get(i).accept(
                    copy.dayGetterMap.get(i).get());
        }
    }

    public void validate() throws OBValidationException {
        if (dealDayDate == null)
            throw new OBValidationException(DealErrorCode.MISSING_COMMODITY_CODE.getCode());

        if (dayTypeCodeValue == null)
            throw new OBValidationException(DealErrorCode.MISSING_COMMODITY_CODE.getCode());
    }

    @Transient
    @JsonIgnore
    public DayTypeCode getDealDayTypeCode() {
        return DayTypeCode.lookUp(dayTypeCodeValue);
    }

    public void setDealDayTypeCode(DayTypeCode code) {
        this.dayTypeCodeValue = code.getCode();
    }

    @Column(name = "DAY_TYPE_CODE")
    public String getDayTypeCodeValue() {
        return dayTypeCodeValue;
    }

    protected void setDayTypeCodeValue(String dayTypeCodeValue) {
        this.dayTypeCodeValue = dayTypeCodeValue;
    }


    @Column(name = "DAY_SUBTYPE_CODE")
    public String getDaySubTypeCodeValue() {
        return daySubTypeCodeValue;
    }

    public void setDaySubTypeCodeValue(String daySubTypeCodeValue) {
        this.daySubTypeCodeValue = daySubTypeCodeValue;
    }

    @Column(name = "DEAL_DAY_DATE")
    public LocalDate getDealDayDate() {
        return dealDayDate;
    }

    public void setDealDayDate(LocalDate date)  {
        this.dealDayDate = date;
    }

    private void initializeSetterMap() {
        daySetterMap.put(1, (BigDecimal c) -> setDay1Value(c));
        daySetterMap.put(2, (BigDecimal c) -> setDay2Value(c));
        daySetterMap.put(3, (BigDecimal c) -> setDay3Value(c));
        daySetterMap.put(4, (BigDecimal c) -> setDay4Value(c));
        daySetterMap.put(5, (BigDecimal c) -> setDay5Value(c));
        daySetterMap.put(6, (BigDecimal c) -> setDay6Value(c));
        daySetterMap.put(7, (BigDecimal c) -> setDay7Value(c));
        daySetterMap.put(8, (BigDecimal c) -> setDay8Value(c));
        daySetterMap.put(9, (BigDecimal c) -> setDay9Value(c));
        daySetterMap.put(10, (BigDecimal c) -> setDay10Value(c));
        daySetterMap.put(11, (BigDecimal c) -> setDay11Value(c));
        daySetterMap.put(12, (BigDecimal c) -> setDay12Value(c));
        daySetterMap.put(13, (BigDecimal c) -> setDay13Value(c));
        daySetterMap.put(14, (BigDecimal c) -> setDay14Value(c));
        daySetterMap.put(15, (BigDecimal c) -> setDay15Value(c));
        daySetterMap.put(16, (BigDecimal c) -> setDay16Value(c));
        daySetterMap.put(17, (BigDecimal c) -> setDay17Value(c));
        daySetterMap.put(18, (BigDecimal c) -> setDay18Value(c));
        daySetterMap.put(19, (BigDecimal c) -> setDay19Value(c));
        daySetterMap.put(20, (BigDecimal c) -> setDay20Value(c));
        daySetterMap.put(21, (BigDecimal c) -> setDay21Value(c));
        daySetterMap.put(22, (BigDecimal c) -> setDay22Value(c));
        daySetterMap.put(23, (BigDecimal c) -> setDay23Value(c));
        daySetterMap.put(24, (BigDecimal c) -> setDay24Value(c));
        daySetterMap.put(25, (BigDecimal c) -> setDay25Value(c));
        daySetterMap.put(26, (BigDecimal c) -> setDay26Value(c));
        daySetterMap.put(27, (BigDecimal c) -> setDay27Value(c));
        daySetterMap.put(28, (BigDecimal c) -> setDay28Value(c));
        daySetterMap.put(29, (BigDecimal c) -> setDay29Value(c));
        daySetterMap.put(30, (BigDecimal c) -> setDay30Value(c));
        daySetterMap.put(31, (BigDecimal c) -> setDay31Value(c));
    }

    private void initializeGetterMap() {
        dayGetterMap.put(1, () -> getDay1Value());
        dayGetterMap.put(2, () -> getDay2Value());
        dayGetterMap.put(3, () -> getDay3Value());
        dayGetterMap.put(4, () -> getDay4Value());
        dayGetterMap.put(5, () -> getDay5Value());
        dayGetterMap.put(6, () -> getDay6Value());
        dayGetterMap.put(7, () -> getDay7Value());
        dayGetterMap.put(8, () -> getDay8Value());
        dayGetterMap.put(9, () -> getDay9Value());
        dayGetterMap.put(10, () -> getDay10Value());
        dayGetterMap.put(11, () -> getDay11Value());
        dayGetterMap.put(12, () -> getDay12Value());
        dayGetterMap.put(13, () -> getDay13Value());
        dayGetterMap.put(14, () -> getDay14Value());
        dayGetterMap.put(15, () -> getDay15Value());
        dayGetterMap.put(16, () -> getDay16Value());
        dayGetterMap.put(17, () -> getDay17Value());
        dayGetterMap.put(18, () -> getDay18Value());
        dayGetterMap.put(19, () -> getDay19Value());
        dayGetterMap.put(20, () -> getDay20Value());
        dayGetterMap.put(21, () -> getDay21Value());
        dayGetterMap.put(22, () -> getDay22Value());
        dayGetterMap.put(23, () -> getDay23Value());
        dayGetterMap.put(24, () -> getDay24Value());
        dayGetterMap.put(25, () -> getDay25Value());
        dayGetterMap.put(26, () -> getDay26Value());
        dayGetterMap.put(27, () -> getDay27Value());
        dayGetterMap.put(28, () -> getDay28Value());
        dayGetterMap.put(29, () -> getDay29Value());
        dayGetterMap.put(30, () -> getDay30Value());
        dayGetterMap.put(31, () -> getDay31Value());
    }

    @Transient
    @JsonIgnore
    public BigDecimal getDayValue(Integer day) {
        return dayGetterMap.get(day).get();
    }

    public void setDayValue(Integer day, BigDecimal value) {
        daySetterMap.get(day).accept(value);
    }


    @Column(name = "DAY_1_VALUE")
    public BigDecimal getDay1Value() {
        return day1Value;
    }

    public void setDay1Value(BigDecimal day1Value) {
        this.day1Value = day1Value;
    }

    @Column(name = "DAY_2_VALUE")
    public BigDecimal getDay2Value() {
        return day2Value;
    }

    public void setDay2Value(BigDecimal day2Value) {
        this.day2Value = day2Value;
    }

    @Column(name = "DAY_3_VALUE")
    public BigDecimal getDay3Value() {
        return day3Value;
    }

    public void setDay3Value(BigDecimal day3Value) {
        this.day3Value = day3Value;
    }

    @Column(name = "DAY_4_VALUE")
    public BigDecimal getDay4Value() {
        return day4Value;
    }

    public void setDay4Value(BigDecimal day4Value) {
        this.day4Value = day4Value;
    }

    @Column(name = "DAY_5_VALUE")
    public BigDecimal getDay5Value() {
        return day5Value;
    }

    public void setDay5Value(BigDecimal day5Value) {
        this.day5Value = day5Value;
    }

    @Column(name = "DAY_6_VALUE")
    public BigDecimal getDay6Value() {
        return day6Value;
    }

    public void setDay6Value(BigDecimal day6Value) {
        this.day6Value = day6Value;
    }

    @Column(name = "DAY_7_VALUE")
    public BigDecimal getDay7Value() {
        return day7Value;
    }

    public void setDay7Value(BigDecimal day7Value) {
        this.day7Value = day7Value;
    }

    @Column(name = "DAY_8_VALUE")
    public BigDecimal getDay8Value() {
        return day8Value;
    }

    public void setDay8Value(BigDecimal day8Value) {
        this.day8Value = day8Value;
    }

    @Column(name = "DAY_9_VALUE")
    public BigDecimal getDay9Value() {
        return day9Value;
    }

    public void setDay9Value(BigDecimal day9Value) {
        this.day9Value = day9Value;
    }

    @Column(name = "DAY_10_VALUE")
    public BigDecimal getDay10Value() {
        return day10Value;
    }

    public void setDay10Value(BigDecimal day10Value) {
        this.day10Value = day10Value;
    }

    @Column(name = "DAY_11_VALUE")
    public BigDecimal getDay11Value() {
        return day11Value;
    }

    public void setDay11Value(BigDecimal day11Value) {
        this.day11Value = day11Value;
    }

    @Column(name = "DAY_12_VALUE")
    public BigDecimal getDay12Value() {
        return day12Value;
    }

    public void setDay12Value(BigDecimal day12Value) {
        this.day12Value = day12Value;
    }

    @Column(name = "DAY_13_VALUE")
    public BigDecimal getDay13Value() {
        return day13Value;
    }

    public void setDay13Value(BigDecimal day13Value) {
        this.day13Value = day13Value;
    }

    @Column(name = "DAY_14_VALUE")
    public BigDecimal getDay14Value() {
        return day14Value;
    }

    public void setDay14Value(BigDecimal day14Value) {
        this.day14Value = day14Value;
    }

    @Column(name = "DAY_15_VALUE")
    public BigDecimal getDay15Value() {
        return day15Value;
    }

    public void setDay15Value(BigDecimal day15Value) {
        this.day15Value = day15Value;
    }

    @Column(name = "DAY_16_VALUE")
    public BigDecimal getDay16Value() {
        return day16Value;
    }

    public void setDay16Value(BigDecimal day16Value) {
        this.day16Value = day16Value;
    }

    @Column(name = "DAY_17_VALUE")
    public BigDecimal getDay17Value() {
        return day17Value;
    }

    public void setDay17Value(BigDecimal day17Value) {
        this.day17Value = day17Value;
    }

    @Column(name = "DAY_18_VALUE")
    public BigDecimal getDay18Value() {
        return day18Value;
    }

    public void setDay18Value(BigDecimal day18Value) {
        this.day18Value = day18Value;
    }

    @Column(name = "DAY_19_VALUE")
    public BigDecimal getDay19Value() {
        return day19Value;
    }

    public void setDay19Value(BigDecimal day19Value) {
        this.day19Value = day19Value;
    }

    @Column(name = "DAY_20_VALUE")
    public BigDecimal getDay20Value() {
        return day20Value;
    }

    public void setDay20Value(BigDecimal day20Value) {
        this.day20Value = day20Value;
    }

    @Column(name = "DAY_21_VALUE")
    public BigDecimal getDay21Value() {
        return day21Value;
    }

    public void setDay21Value(BigDecimal day21Value) {
        this.day21Value = day21Value;
    }

    @Column(name = "DAY_22_VALUE")
    public BigDecimal getDay22Value() {
        return day22Value;
    }

    public void setDay22Value(BigDecimal day22Value) {
        this.day22Value = day22Value;
    }

    @Column(name = "DAY_23_VALUE")
    public BigDecimal getDay23Value() {
        return day23Value;
    }

    public void setDay23Value(BigDecimal day23Value) {
        this.day23Value = day23Value;
    }

    @Column(name = "DAY_24_VALUE")
    public BigDecimal getDay24Value() {
        return day24Value;
    }

    public void setDay24Value(BigDecimal day24Value) {
        this.day24Value = day24Value;
    }

    @Column(name = "DAY_25_VALUE")
    public BigDecimal getDay25Value() {
        return day25Value;
    }

    public void setDay25Value(BigDecimal day25Value) {
        this.day25Value = day25Value;
    }

    @Column(name = "DAY_26_VALUE")
    public BigDecimal getDay26Value() {
        return day26Value;
    }

    public void setDay26Value(BigDecimal day26Value) {
        this.day26Value = day26Value;
    }

    @Column(name = "DAY_27_VALUE")
    public BigDecimal getDay27Value() {
        return day27Value;
    }

    public void setDay27Value(BigDecimal day27Value) {
        this.day27Value = day27Value;
    }

    @Column(name = "DAY_28_VALUE")
    public BigDecimal getDay28Value() {
        return day28Value;
    }

    public void setDay28Value(BigDecimal day28Value) {
        this.day28Value = day28Value;
    }

    @Column(name = "DAY_29_VALUE")
    public BigDecimal getDay29Value() {
        return day29Value;
    }

    public void setDay29Value(BigDecimal day29Value) {
        this.day29Value = day29Value;
    }

    @Column(name = "DAY_30_VALUE")
    public BigDecimal getDay30Value() {
        return day30Value;
    }

    public void setDay30Value(BigDecimal day30Value) {
        this.day30Value = day30Value;
    }

    @Column(name = "DAY_31_VALUE")
    public BigDecimal getDay31Value() {
        return day31Value;
    }

    public void setDay31Value(BigDecimal day31Value) {
        this.day31Value = day31Value;
    }
}
