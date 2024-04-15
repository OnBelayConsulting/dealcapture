package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DealHourByDayDetail {

    private LocalDate dealDayDate;

    private String dayTypeCodeValue;
    private String daySubTypeCodeValue;


    private HashMap<Integer, Consumer<BigDecimal>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<BigDecimal>> hourGetterMap = new HashMap<>();
    
    private BigDecimal hour1Value;
    private BigDecimal hour2Value;
    private BigDecimal hour3Value;
    private BigDecimal hour4Value;
    private BigDecimal hour5Value;
    private BigDecimal hour6Value;
    private BigDecimal hour7Value;
    private BigDecimal hour8Value;
    private BigDecimal hour9Value;
    private BigDecimal hour10Value;
    private BigDecimal hour11Value;
    private BigDecimal hour12Value;
    private BigDecimal hour13Value;
    private BigDecimal hour14Value;
    private BigDecimal hour15Value;
    private BigDecimal hour16Value;
    private BigDecimal hour17Value;
    private BigDecimal hour18Value;
    private BigDecimal hour19Value;
    private BigDecimal hour20Value;
    private BigDecimal hour21Value;
    private BigDecimal hour22Value;
    private BigDecimal hour23Value;
    private BigDecimal hour24Value;

    public DealHourByDayDetail() {
        initializeGetterMap();
        initializeSetterMap();
    }


    @Transient
    public boolean isNotEmpty() {
        for (int i=1; i< 25; i++) {
            if (getHourValue(i) != null) {
                return true;
            }
        }
        return false;
    }


    public void copyFrom(DealHourByDayDetail copy) {
        if (copy.dealDayDate != null)
            this.dealDayDate = copy.dealDayDate;

        if (copy.dayTypeCodeValue != null)
            this.dayTypeCodeValue = copy.dayTypeCodeValue;

        if (copy.daySubTypeCodeValue != null)
            this.daySubTypeCodeValue = copy.daySubTypeCodeValue;



        for (int i=1; i < 25; i++) {
            this.hourSetterMap.get(i).accept(
                    copy.hourGetterMap.get(i).get());
        }
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


    @Transient
    @JsonIgnore
    public BigDecimal getHourValue(int hourEnding) {
        return hourGetterMap.get(hourEnding).get();
    }

    public void setHourValue(int hourEnding, BigDecimal value) {
        hourSetterMap.get(hourEnding).accept(value);
    }


    @Column(name = "HOUR_1_VALUE")
    public BigDecimal getHour1Value() {
        return hour1Value;
    }

    public void setHour1Value(BigDecimal hour1Value) {
        this.hour1Value = hour1Value;
    }

    @Column(name = "HOUR_2_VALUE")
    public BigDecimal getHour2Value() {
        return hour2Value;
    }

    public void setHour2Value(BigDecimal hour2Value) {
        this.hour2Value = hour2Value;
    }

    @Column(name = "HOUR_3_VALUE")
    public BigDecimal getHour3Value() {
        return hour3Value;
    }

    public void setHour3Value(BigDecimal hour3Value) {
        this.hour3Value = hour3Value;
    }

    @Column(name = "HOUR_4_VALUE")
    public BigDecimal getHour4Value() {
        return hour4Value;
    }

    public void setHour4Value(BigDecimal hour4Value) {
        this.hour4Value = hour4Value;
    }

    @Column(name = "HOUR_5_VALUE")
    public BigDecimal getHour5Value() {
        return hour5Value;
    }

    public void setHour5Value(BigDecimal hour5Value) {
        this.hour5Value = hour5Value;
    }

    @Column(name = "HOUR_6_VALUE")
    public BigDecimal getHour6Value() {
        return hour6Value;
    }

    public void setHour6Value(BigDecimal hour6Value) {
        this.hour6Value = hour6Value;
    }

    @Column(name = "HOUR_7_VALUE")
    public BigDecimal getHour7Value() {
        return hour7Value;
    }

    public void setHour7Value(BigDecimal hour7Value) {
        this.hour7Value = hour7Value;
    }

    @Column(name = "HOUR_8_VALUE")
    public BigDecimal getHour8Value() {
        return hour8Value;
    }

    public void setHour8Value(BigDecimal hour8Value) {
        this.hour8Value = hour8Value;
    }

    @Column(name = "HOUR_9_VALUE")
    public BigDecimal getHour9Value() {
        return hour9Value;
    }

    public void setHour9Value(BigDecimal hour9Value) {
        this.hour9Value = hour9Value;
    }

    @Column(name = "HOUR_10_VALUE")
    public BigDecimal getHour10Value() {
        return hour10Value;
    }

    public void setHour10Value(BigDecimal hour10Value) {
        this.hour10Value = hour10Value;
    }

    @Column(name = "HOUR_11_VALUE")
    public BigDecimal getHour11Value() {
        return hour11Value;
    }

    public void setHour11Value(BigDecimal hour11Value) {
        this.hour11Value = hour11Value;
    }

    @Column(name = "HOUR_12_VALUE")
    public BigDecimal getHour12Value() {
        return hour12Value;
    }

    public void setHour12Value(BigDecimal hour12Value) {
        this.hour12Value = hour12Value;
    }

    @Column(name = "HOUR_13_VALUE")
    public BigDecimal getHour13Value() {
        return hour13Value;
    }

    public void setHour13Value(BigDecimal hour13Value) {
        this.hour13Value = hour13Value;
    }

    @Column(name = "HOUR_14_VALUE")
    public BigDecimal getHour14Value() {
        return hour14Value;
    }

    public void setHour14Value(BigDecimal hour14Value) {
        this.hour14Value = hour14Value;
    }

    @Column(name = "HOUR_15_VALUE")
    public BigDecimal getHour15Value() {
        return hour15Value;
    }

    public void setHour15Value(BigDecimal hour15Value) {
        this.hour15Value = hour15Value;
    }

    @Column(name = "HOUR_16_VALUE")
    public BigDecimal getHour16Value() {
        return hour16Value;
    }

    public void setHour16Value(BigDecimal hour16Value) {
        this.hour16Value = hour16Value;
    }

    @Column(name = "HOUR_17_VALUE")
    public BigDecimal getHour17Value() {
        return hour17Value;
    }

    public void setHour17Value(BigDecimal hour17Value) {
        this.hour17Value = hour17Value;
    }

    @Column(name = "HOUR_18_VALUE")
    public BigDecimal getHour18Value() {
        return hour18Value;
    }

    public void setHour18Value(BigDecimal hour18Value) {
        this.hour18Value = hour18Value;
    }

    @Column(name = "HOUR_19_VALUE")
    public BigDecimal getHour19Value() {
        return hour19Value;
    }

    public void setHour19Value(BigDecimal hour19Value) {
        this.hour19Value = hour19Value;
    }

    @Column(name = "HOUR_20_VALUE")
    public BigDecimal getHour20Value() {
        return hour20Value;
    }

    public void setHour20Value(BigDecimal hour20Value) {
        this.hour20Value = hour20Value;
    }

    @Column(name = "HOUR_21_VALUE")
    public BigDecimal getHour21Value() {
        return hour21Value;
    }

    public void setHour21Value(BigDecimal hour21Value) {
        this.hour21Value = hour21Value;
    }

    @Column(name = "HOUR_22_VALUE")
    public BigDecimal getHour22Value() {
        return hour22Value;
    }

    public void setHour22Value(BigDecimal hour22Value) {
        this.hour22Value = hour22Value;
    }

    @Column(name = "HOUR_23_VALUE")
    public BigDecimal getHour23Value() {
        return hour23Value;
    }

    public void setHour23Value(BigDecimal hour23Value) {
        this.hour23Value = hour23Value;
    }

    @Column(name = "HOUR_24_VALUE")
    public BigDecimal getHour24Value() {
        return hour24Value;
    }

    public void setHour24Value(BigDecimal hour24Value) {
        this.hour24Value = hour24Value;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, (BigDecimal c) -> setHour1Value(c));
        hourSetterMap.put(2, (BigDecimal c) -> setHour2Value(c));
        hourSetterMap.put(3, (BigDecimal c) -> setHour3Value(c));
        hourSetterMap.put(4, (BigDecimal c) -> setHour4Value(c));
        hourSetterMap.put(5, (BigDecimal c) -> setHour5Value(c));
        hourSetterMap.put(6, (BigDecimal c) -> setHour6Value(c));
        hourSetterMap.put(7, (BigDecimal c) -> setHour7Value(c));
        hourSetterMap.put(8, (BigDecimal c) -> setHour8Value(c));
        hourSetterMap.put(9, (BigDecimal c) -> setHour9Value(c));
        hourSetterMap.put(10, (BigDecimal c) -> setHour10Value(c));
        hourSetterMap.put(11, (BigDecimal c) -> setHour11Value(c));
        hourSetterMap.put(12, (BigDecimal c) -> setHour12Value(c));
        hourSetterMap.put(13, (BigDecimal c) -> setHour13Value(c));
        hourSetterMap.put(14, (BigDecimal c) -> setHour14Value(c));
        hourSetterMap.put(15, (BigDecimal c) -> setHour15Value(c));
        hourSetterMap.put(16, (BigDecimal c) -> setHour16Value(c));
        hourSetterMap.put(17, (BigDecimal c) -> setHour17Value(c));
        hourSetterMap.put(18, (BigDecimal c) -> setHour18Value(c));
        hourSetterMap.put(19, (BigDecimal c) -> setHour19Value(c));
        hourSetterMap.put(20, (BigDecimal c) -> setHour20Value(c));
        hourSetterMap.put(21, (BigDecimal c) -> setHour21Value(c));
        hourSetterMap.put(22, (BigDecimal c) -> setHour22Value(c));
        hourSetterMap.put(23, (BigDecimal c) -> setHour23Value(c));
        hourSetterMap.put(24, (BigDecimal c) -> setHour24Value(c));
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, () -> getHour1Value());
        hourGetterMap.put(2, () -> getHour2Value());
        hourGetterMap.put(3, () -> getHour3Value());
        hourGetterMap.put(4, () -> getHour4Value());
        hourGetterMap.put(5, () -> getHour5Value());
        hourGetterMap.put(6, () -> getHour6Value());
        hourGetterMap.put(7, () -> getHour7Value());
        hourGetterMap.put(8, () -> getHour8Value());
        hourGetterMap.put(9, () -> getHour9Value());
        hourGetterMap.put(10, () -> getHour10Value());
        hourGetterMap.put(11, () -> getHour11Value());
        hourGetterMap.put(12, () -> getHour12Value());
        hourGetterMap.put(13, () -> getHour13Value());
        hourGetterMap.put(14, () -> getHour14Value());
        hourGetterMap.put(15, () -> getHour15Value());
        hourGetterMap.put(16, () -> getHour16Value());
        hourGetterMap.put(17, () -> getHour17Value());
        hourGetterMap.put(18, () -> getHour18Value());
        hourGetterMap.put(19, () -> getHour19Value());
        hourGetterMap.put(20, () -> getHour20Value());
        hourGetterMap.put(21, () -> getHour21Value());
        hourGetterMap.put(22, () -> getHour22Value());
        hourGetterMap.put(23, () -> getHour23Value());
        hourGetterMap.put(24, () -> getHour24Value());
    }
}
