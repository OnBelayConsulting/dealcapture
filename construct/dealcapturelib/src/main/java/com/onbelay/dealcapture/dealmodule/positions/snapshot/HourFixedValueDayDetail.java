package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HourFixedValueDayDetail {

    private HashMap<Integer, Consumer<BigDecimal>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<BigDecimal>> hourGetterMap = new HashMap<>();
    
    private BigDecimal hour1FixedValue;
    private BigDecimal hour2FixedValue;
    private BigDecimal hour3FixedValue;
    private BigDecimal hour4FixedValue;
    private BigDecimal hour5FixedValue;
    private BigDecimal hour6FixedValue;
    private BigDecimal hour7FixedValue;
    private BigDecimal hour8FixedValue;
    private BigDecimal hour9FixedValue;
    private BigDecimal hour10FixedValue;
    private BigDecimal hour11FixedValue;
    private BigDecimal hour12FixedValue;
    private BigDecimal hour13FixedValue;
    private BigDecimal hour14FixedValue;
    private BigDecimal hour15FixedValue;
    private BigDecimal hour16FixedValue;
    private BigDecimal hour17FixedValue;
    private BigDecimal hour18FixedValue;
    private BigDecimal hour19FixedValue;
    private BigDecimal hour20FixedValue;
    private BigDecimal hour21FixedValue;
    private BigDecimal hour22FixedValue;
    private BigDecimal hour23FixedValue;
    private BigDecimal hour24FixedValue;

    public HourFixedValueDayDetail() {
        initializeGetterMap();
        initializeSetterMap();
    }

    @Transient
    public boolean isNotEmpty() {
        for (int i=1; i< 25; i++) {
            if (getHourFixedValue(i) != null) {
                return true;
            }
        }
        return false;
    }


    public void copyFrom(HourFixedValueDayDetail copy) {


        for (int i=1; i < 25; i++) {
            this.hourSetterMap.get(i).accept(
                    copy.hourGetterMap.get(i).get());
        }
    }

    public BigDecimal getHourFixedValue(int hourEnding) {
        return hourGetterMap.get(hourEnding).get();
    }

    public void setHourFixedValue(int hourEnding, BigDecimal hourQuantity) {
        this.hourSetterMap.get(hourEnding).accept(hourQuantity);
    }

    @Column(name = "HOUR_1_VALUE")
    public BigDecimal getHour1FixedValue() {
        return hour1FixedValue;
    }

    public void setHour1FixedValue(BigDecimal hour1FixedValue) {
        this.hour1FixedValue = hour1FixedValue;
    }

    @Column(name = "HOUR_2_VALUE")
    public BigDecimal getHour2FixedValue() {
        return hour2FixedValue;
    }

    public void setHour2FixedValue(BigDecimal hour2FixedValue) {
        this.hour2FixedValue = hour2FixedValue;
    }

    @Column(name = "HOUR_3_VALUE")
    public BigDecimal getHour3FixedValue() {
        return hour3FixedValue;
    }

    public void setHour3FixedValue(BigDecimal hour3FixedValue) {
        this.hour3FixedValue = hour3FixedValue;
    }

    @Column(name = "HOUR_4_VALUE")
    public BigDecimal getHour4FixedValue() {
        return hour4FixedValue;
    }

    public void setHour4FixedValue(BigDecimal hour4FixedValue) {
        this.hour4FixedValue = hour4FixedValue;
    }

    @Column(name = "HOUR_5_VALUE")
    public BigDecimal getHour5FixedValue() {
        return hour5FixedValue;
    }

    public void setHour5FixedValue(BigDecimal hour5FixedValue) {
        this.hour5FixedValue = hour5FixedValue;
    }

    @Column(name = "HOUR_6_VALUE")
    public BigDecimal getHour6FixedValue() {
        return hour6FixedValue;
    }

    public void setHour6FixedValue(BigDecimal hour6FixedValue) {
        this.hour6FixedValue = hour6FixedValue;
    }

    @Column(name = "HOUR_7_VALUE")
    public BigDecimal getHour7FixedValue() {
        return hour7FixedValue;
    }

    public void setHour7FixedValue(BigDecimal hour7FixedValue) {
        this.hour7FixedValue = hour7FixedValue;
    }

    @Column(name = "HOUR_8_VALUE")
    public BigDecimal getHour8FixedValue() {
        return hour8FixedValue;
    }

    public void setHour8FixedValue(BigDecimal hour8FixedValue) {
        this.hour8FixedValue = hour8FixedValue;
    }

    @Column(name = "HOUR_9_VALUE")
    public BigDecimal getHour9FixedValue() {
        return hour9FixedValue;
    }

    public void setHour9FixedValue(BigDecimal hour9FixedValue) {
        this.hour9FixedValue = hour9FixedValue;
    }

    @Column(name = "HOUR_10_VALUE")
    public BigDecimal getHour10FixedValue() {
        return hour10FixedValue;
    }

    public void setHour10FixedValue(BigDecimal hour10FixedValue) {
        this.hour10FixedValue = hour10FixedValue;
    }

    @Column(name = "HOUR_11_VALUE")
    public BigDecimal getHour11FixedValue() {
        return hour11FixedValue;
    }

    public void setHour11FixedValue(BigDecimal hour11FixedValue) {
        this.hour11FixedValue = hour11FixedValue;
    }

    @Column(name = "HOUR_12_VALUE")
    public BigDecimal getHour12FixedValue() {
        return hour12FixedValue;
    }

    public void setHour12FixedValue(BigDecimal hour12FixedValue) {
        this.hour12FixedValue = hour12FixedValue;
    }

    @Column(name = "HOUR_13_VALUE")
    public BigDecimal getHour13FixedValue() {
        return hour13FixedValue;
    }

    public void setHour13FixedValue(BigDecimal hour13FixedValue) {
        this.hour13FixedValue = hour13FixedValue;
    }

    @Column(name = "HOUR_14_VALUE")
    public BigDecimal getHour14FixedValue() {
        return hour14FixedValue;
    }

    public void setHour14FixedValue(BigDecimal hour14FixedValue) {
        this.hour14FixedValue = hour14FixedValue;
    }

    @Column(name = "HOUR_15_VALUE")
    public BigDecimal getHour15FixedValue() {
        return hour15FixedValue;
    }

    public void setHour15FixedValue(BigDecimal hour15FixedValue) {
        this.hour15FixedValue = hour15FixedValue;
    }

    @Column(name = "HOUR_16_VALUE")
    public BigDecimal getHour16FixedValue() {
        return hour16FixedValue;
    }

    public void setHour16FixedValue(BigDecimal hour16FixedValue) {
        this.hour16FixedValue = hour16FixedValue;
    }

    @Column(name = "HOUR_17_VALUE")
    public BigDecimal getHour17FixedValue() {
        return hour17FixedValue;
    }

    public void setHour17FixedValue(BigDecimal hour17FixedValue) {
        this.hour17FixedValue = hour17FixedValue;
    }

    @Column(name = "HOUR_18_VALUE")
    public BigDecimal getHour18FixedValue() {
        return hour18FixedValue;
    }

    public void setHour18FixedValue(BigDecimal hour18FixedValue) {
        this.hour18FixedValue = hour18FixedValue;
    }

    @Column(name = "HOUR_19_VALUE")
    public BigDecimal getHour19FixedValue() {
        return hour19FixedValue;
    }

    public void setHour19FixedValue(BigDecimal hour19FixedValue) {
        this.hour19FixedValue = hour19FixedValue;
    }

    @Column(name = "HOUR_20_VALUE")
    public BigDecimal getHour20FixedValue() {
        return hour20FixedValue;
    }

    public void setHour20FixedValue(BigDecimal hour20FixedValue) {
        this.hour20FixedValue = hour20FixedValue;
    }

    @Column(name = "HOUR_21_VALUE")
    public BigDecimal getHour21FixedValue() {
        return hour21FixedValue;
    }

    public void setHour21FixedValue(BigDecimal hour21FixedValue) {
        this.hour21FixedValue = hour21FixedValue;
    }

    @Column(name = "HOUR_22_VALUE")
    public BigDecimal getHour22FixedValue() {
        return hour22FixedValue;
    }

    public void setHour22FixedValue(BigDecimal hour22FixedValue) {
        this.hour22FixedValue = hour22FixedValue;
    }

    @Column(name = "HOUR_23_VALUE")
    public BigDecimal getHour23FixedValue() {
        return hour23FixedValue;
    }

    public void setHour23FixedValue(BigDecimal hour23FixedValue) {
        this.hour23FixedValue = hour23FixedValue;
    }

    @Column(name = "HOUR_24_VALUE")
    public BigDecimal getHour24FixedValue() {
        return hour24FixedValue;
    }

    public void setHour24FixedValue(BigDecimal hour24FixedValue) {
        this.hour24FixedValue = hour24FixedValue;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, (BigDecimal c) -> setHour1FixedValue(c));
        hourSetterMap.put(2, (BigDecimal c) -> setHour2FixedValue(c));
        hourSetterMap.put(3, (BigDecimal c) -> setHour3FixedValue(c));
        hourSetterMap.put(4, (BigDecimal c) -> setHour4FixedValue(c));
        hourSetterMap.put(5, (BigDecimal c) -> setHour5FixedValue(c));
        hourSetterMap.put(6, (BigDecimal c) -> setHour6FixedValue(c));
        hourSetterMap.put(7, (BigDecimal c) -> setHour7FixedValue(c));
        hourSetterMap.put(8, (BigDecimal c) -> setHour8FixedValue(c));
        hourSetterMap.put(9, (BigDecimal c) -> setHour9FixedValue(c));
        hourSetterMap.put(10, (BigDecimal c) -> setHour10FixedValue(c));
        hourSetterMap.put(11, (BigDecimal c) -> setHour11FixedValue(c));
        hourSetterMap.put(12, (BigDecimal c) -> setHour12FixedValue(c));
        hourSetterMap.put(13, (BigDecimal c) -> setHour13FixedValue(c));
        hourSetterMap.put(14, (BigDecimal c) -> setHour14FixedValue(c));
        hourSetterMap.put(15, (BigDecimal c) -> setHour15FixedValue(c));
        hourSetterMap.put(16, (BigDecimal c) -> setHour16FixedValue(c));
        hourSetterMap.put(17, (BigDecimal c) -> setHour17FixedValue(c));
        hourSetterMap.put(18, (BigDecimal c) -> setHour18FixedValue(c));
        hourSetterMap.put(19, (BigDecimal c) -> setHour19FixedValue(c));
        hourSetterMap.put(20, (BigDecimal c) -> setHour20FixedValue(c));
        hourSetterMap.put(21, (BigDecimal c) -> setHour21FixedValue(c));
        hourSetterMap.put(22, (BigDecimal c) -> setHour22FixedValue(c));
        hourSetterMap.put(23, (BigDecimal c) -> setHour23FixedValue(c));
        hourSetterMap.put(24, (BigDecimal c) -> setHour24FixedValue(c));
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, () -> getHour1FixedValue());
        hourGetterMap.put(2, () -> getHour2FixedValue());
        hourGetterMap.put(3, () -> getHour3FixedValue());
        hourGetterMap.put(4, () -> getHour4FixedValue());
        hourGetterMap.put(5, () -> getHour5FixedValue());
        hourGetterMap.put(6, () -> getHour6FixedValue());
        hourGetterMap.put(7, () -> getHour7FixedValue());
        hourGetterMap.put(8, () -> getHour8FixedValue());
        hourGetterMap.put(9, () -> getHour9FixedValue());
        hourGetterMap.put(10, () -> getHour10FixedValue());
        hourGetterMap.put(11, () -> getHour11FixedValue());
        hourGetterMap.put(12, () -> getHour12FixedValue());
        hourGetterMap.put(13, () -> getHour13FixedValue());
        hourGetterMap.put(14, () -> getHour14FixedValue());
        hourGetterMap.put(15, () -> getHour15FixedValue());
        hourGetterMap.put(16, () -> getHour16FixedValue());
        hourGetterMap.put(17, () -> getHour17FixedValue());
        hourGetterMap.put(18, () -> getHour18FixedValue());
        hourGetterMap.put(19, () -> getHour19FixedValue());
        hourGetterMap.put(20, () -> getHour20FixedValue());
        hourGetterMap.put(21, () -> getHour21FixedValue());
        hourGetterMap.put(22, () -> getHour22FixedValue());
        hourGetterMap.put(23, () -> getHour23FixedValue());
        hourGetterMap.put(24, () -> getHour24FixedValue());
    }
    
}
