package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HourPriceDayDetail {

    private HashMap<Integer, Consumer<BigDecimal>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<BigDecimal>> hourGetterMap = new HashMap<>();
    
    private BigDecimal hour1PriceValue;
    private BigDecimal hour2PriceValue;
    private BigDecimal hour3PriceValue;
    private BigDecimal hour4PriceValue;
    private BigDecimal hour5PriceValue;
    private BigDecimal hour6PriceValue;
    private BigDecimal hour7PriceValue;
    private BigDecimal hour8PriceValue;
    private BigDecimal hour9PriceValue;
    private BigDecimal hour10PriceValue;
    private BigDecimal hour11PriceValue;
    private BigDecimal hour12PriceValue;
    private BigDecimal hour13PriceValue;
    private BigDecimal hour14PriceValue;
    private BigDecimal hour15PriceValue;
    private BigDecimal hour16PriceValue;
    private BigDecimal hour17PriceValue;
    private BigDecimal hour18PriceValue;
    private BigDecimal hour19PriceValue;
    private BigDecimal hour20PriceValue;
    private BigDecimal hour21PriceValue;
    private BigDecimal hour22PriceValue;
    private BigDecimal hour23PriceValue;
    private BigDecimal hour24PriceValue;

    public HourPriceDayDetail() {
        initializeGetterMap();
        initializeSetterMap();
    }

    @Transient
    public boolean isNotEmpty() {
        for (int i=1; i< 25; i++) {
            if (getHourPrice(i) != null) {
                return true;
            }
        }
        return false;
    }


    public void copyFrom(HourPriceDayDetail copy) {


        for (int i=1; i < 25; i++) {
            this.hourSetterMap.get(i).accept(
                    copy.hourGetterMap.get(i).get());
        }
    }

    public BigDecimal getHourPrice(int hourEnding) {
        return hourGetterMap.get(hourEnding).get();
    }

    public void setHourPrice(int hourEnding, BigDecimal hourPrice) {
        this.hourSetterMap.get(hourEnding).accept(hourPrice);
    }

    @Column(name = "HOUR_1_PRICE")
    public BigDecimal getHour1PriceValue() {
        return hour1PriceValue;
    }

    public void setHour1PriceValue(BigDecimal hour1PriceValue) {
        this.hour1PriceValue = hour1PriceValue;
    }

    @Column(name = "HOUR_2_PRICE")
    public BigDecimal getHour2PriceValue() {
        return hour2PriceValue;
    }

    public void setHour2PriceValue(BigDecimal hour2PriceValue) {
        this.hour2PriceValue = hour2PriceValue;
    }

    @Column(name = "HOUR_3_PRICE")
    public BigDecimal getHour3PriceValue() {
        return hour3PriceValue;
    }

    public void setHour3PriceValue(BigDecimal hour3PriceValue) {
        this.hour3PriceValue = hour3PriceValue;
    }

    @Column(name = "HOUR_4_PRICE")
    public BigDecimal getHour4PriceValue() {
        return hour4PriceValue;
    }

    public void setHour4PriceValue(BigDecimal hour4PriceValue) {
        this.hour4PriceValue = hour4PriceValue;
    }

    @Column(name = "HOUR_5_PRICE")
    public BigDecimal getHour5PriceValue() {
        return hour5PriceValue;
    }

    public void setHour5PriceValue(BigDecimal hour5PriceValue) {
        this.hour5PriceValue = hour5PriceValue;
    }

    @Column(name = "HOUR_6_PRICE")
    public BigDecimal getHour6PriceValue() {
        return hour6PriceValue;
    }

    public void setHour6PriceValue(BigDecimal hour6PriceValue) {
        this.hour6PriceValue = hour6PriceValue;
    }

    @Column(name = "HOUR_7_PRICE")
    public BigDecimal getHour7PriceValue() {
        return hour7PriceValue;
    }

    public void setHour7PriceValue(BigDecimal hour7PriceValue) {
        this.hour7PriceValue = hour7PriceValue;
    }

    @Column(name = "HOUR_8_PRICE")
    public BigDecimal getHour8PriceValue() {
        return hour8PriceValue;
    }

    public void setHour8PriceValue(BigDecimal hour8PriceValue) {
        this.hour8PriceValue = hour8PriceValue;
    }

    @Column(name = "HOUR_9_PRICE")
    public BigDecimal getHour9PriceValue() {
        return hour9PriceValue;
    }

    public void setHour9PriceValue(BigDecimal hour9PriceValue) {
        this.hour9PriceValue = hour9PriceValue;
    }

    @Column(name = "HOUR_10_PRICE")
    public BigDecimal getHour10PriceValue() {
        return hour10PriceValue;
    }

    public void setHour10PriceValue(BigDecimal hour10PriceValue) {
        this.hour10PriceValue = hour10PriceValue;
    }

    @Column(name = "HOUR_11_PRICE")
    public BigDecimal getHour11PriceValue() {
        return hour11PriceValue;
    }

    public void setHour11PriceValue(BigDecimal hour11PriceValue) {
        this.hour11PriceValue = hour11PriceValue;
    }

    @Column(name = "HOUR_12_PRICE")
    public BigDecimal getHour12PriceValue() {
        return hour12PriceValue;
    }

    public void setHour12PriceValue(BigDecimal hour12PriceValue) {
        this.hour12PriceValue = hour12PriceValue;
    }

    @Column(name = "HOUR_13_PRICE")
    public BigDecimal getHour13PriceValue() {
        return hour13PriceValue;
    }

    public void setHour13PriceValue(BigDecimal hour13PriceValue) {
        this.hour13PriceValue = hour13PriceValue;
    }

    @Column(name = "HOUR_14_PRICE")
    public BigDecimal getHour14PriceValue() {
        return hour14PriceValue;
    }

    public void setHour14PriceValue(BigDecimal hour14PriceValue) {
        this.hour14PriceValue = hour14PriceValue;
    }

    @Column(name = "HOUR_15_PRICE")
    public BigDecimal getHour15PriceValue() {
        return hour15PriceValue;
    }

    public void setHour15PriceValue(BigDecimal hour15PriceValue) {
        this.hour15PriceValue = hour15PriceValue;
    }

    @Column(name = "HOUR_16_PRICE")
    public BigDecimal getHour16PriceValue() {
        return hour16PriceValue;
    }

    public void setHour16PriceValue(BigDecimal hour16PriceValue) {
        this.hour16PriceValue = hour16PriceValue;
    }

    @Column(name = "HOUR_17_PRICE")
    public BigDecimal getHour17PriceValue() {
        return hour17PriceValue;
    }

    public void setHour17PriceValue(BigDecimal hour17PriceValue) {
        this.hour17PriceValue = hour17PriceValue;
    }

    @Column(name = "HOUR_18_PRICE")
    public BigDecimal getHour18PriceValue() {
        return hour18PriceValue;
    }

    public void setHour18PriceValue(BigDecimal hour18PriceValue) {
        this.hour18PriceValue = hour18PriceValue;
    }

    @Column(name = "HOUR_19_PRICE")
    public BigDecimal getHour19PriceValue() {
        return hour19PriceValue;
    }

    public void setHour19PriceValue(BigDecimal hour19PriceValue) {
        this.hour19PriceValue = hour19PriceValue;
    }

    @Column(name = "HOUR_20_PRICE")
    public BigDecimal getHour20PriceValue() {
        return hour20PriceValue;
    }

    public void setHour20PriceValue(BigDecimal hour20PriceValue) {
        this.hour20PriceValue = hour20PriceValue;
    }

    @Column(name = "HOUR_21_PRICE")
    public BigDecimal getHour21PriceValue() {
        return hour21PriceValue;
    }

    public void setHour21PriceValue(BigDecimal hour21PriceValue) {
        this.hour21PriceValue = hour21PriceValue;
    }

    @Column(name = "HOUR_22_PRICE")
    public BigDecimal getHour22PriceValue() {
        return hour22PriceValue;
    }

    public void setHour22PriceValue(BigDecimal hour22PriceValue) {
        this.hour22PriceValue = hour22PriceValue;
    }

    @Column(name = "HOUR_23_PRICE")
    public BigDecimal getHour23PriceValue() {
        return hour23PriceValue;
    }

    public void setHour23PriceValue(BigDecimal hour23PriceValue) {
        this.hour23PriceValue = hour23PriceValue;
    }

    @Column(name = "HOUR_24_PRICE")
    public BigDecimal getHour24PriceValue() {
        return hour24PriceValue;
    }

    public void setHour24PriceValue(BigDecimal hour24PriceValue) {
        this.hour24PriceValue = hour24PriceValue;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, (BigDecimal c) -> setHour1PriceValue(c));
        hourSetterMap.put(2, (BigDecimal c) -> setHour2PriceValue(c));
        hourSetterMap.put(3, (BigDecimal c) -> setHour3PriceValue(c));
        hourSetterMap.put(4, (BigDecimal c) -> setHour4PriceValue(c));
        hourSetterMap.put(5, (BigDecimal c) -> setHour5PriceValue(c));
        hourSetterMap.put(6, (BigDecimal c) -> setHour6PriceValue(c));
        hourSetterMap.put(7, (BigDecimal c) -> setHour7PriceValue(c));
        hourSetterMap.put(8, (BigDecimal c) -> setHour8PriceValue(c));
        hourSetterMap.put(9, (BigDecimal c) -> setHour9PriceValue(c));
        hourSetterMap.put(10, (BigDecimal c) -> setHour10PriceValue(c));
        hourSetterMap.put(11, (BigDecimal c) -> setHour11PriceValue(c));
        hourSetterMap.put(12, (BigDecimal c) -> setHour12PriceValue(c));
        hourSetterMap.put(13, (BigDecimal c) -> setHour13PriceValue(c));
        hourSetterMap.put(14, (BigDecimal c) -> setHour14PriceValue(c));
        hourSetterMap.put(15, (BigDecimal c) -> setHour15PriceValue(c));
        hourSetterMap.put(16, (BigDecimal c) -> setHour16PriceValue(c));
        hourSetterMap.put(17, (BigDecimal c) -> setHour17PriceValue(c));
        hourSetterMap.put(18, (BigDecimal c) -> setHour18PriceValue(c));
        hourSetterMap.put(19, (BigDecimal c) -> setHour19PriceValue(c));
        hourSetterMap.put(20, (BigDecimal c) -> setHour20PriceValue(c));
        hourSetterMap.put(21, (BigDecimal c) -> setHour21PriceValue(c));
        hourSetterMap.put(22, (BigDecimal c) -> setHour22PriceValue(c));
        hourSetterMap.put(23, (BigDecimal c) -> setHour23PriceValue(c));
        hourSetterMap.put(24, (BigDecimal c) -> setHour24PriceValue(c));
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, () -> getHour1PriceValue());
        hourGetterMap.put(2, () -> getHour2PriceValue());
        hourGetterMap.put(3, () -> getHour3PriceValue());
        hourGetterMap.put(4, () -> getHour4PriceValue());
        hourGetterMap.put(5, () -> getHour5PriceValue());
        hourGetterMap.put(6, () -> getHour6PriceValue());
        hourGetterMap.put(7, () -> getHour7PriceValue());
        hourGetterMap.put(8, () -> getHour8PriceValue());
        hourGetterMap.put(9, () -> getHour9PriceValue());
        hourGetterMap.put(10, () -> getHour10PriceValue());
        hourGetterMap.put(11, () -> getHour11PriceValue());
        hourGetterMap.put(12, () -> getHour12PriceValue());
        hourGetterMap.put(13, () -> getHour13PriceValue());
        hourGetterMap.put(14, () -> getHour14PriceValue());
        hourGetterMap.put(15, () -> getHour15PriceValue());
        hourGetterMap.put(16, () -> getHour16PriceValue());
        hourGetterMap.put(17, () -> getHour17PriceValue());
        hourGetterMap.put(18, () -> getHour18PriceValue());
        hourGetterMap.put(19, () -> getHour19PriceValue());
        hourGetterMap.put(20, () -> getHour20PriceValue());
        hourGetterMap.put(21, () -> getHour21PriceValue());
        hourGetterMap.put(22, () -> getHour22PriceValue());
        hourGetterMap.put(23, () -> getHour23PriceValue());
        hourGetterMap.put(24, () -> getHour24PriceValue());
    }
    
}
