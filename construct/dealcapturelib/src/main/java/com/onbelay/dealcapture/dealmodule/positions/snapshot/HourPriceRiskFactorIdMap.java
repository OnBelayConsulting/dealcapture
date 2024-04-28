package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HourPriceRiskFactorIdMap {

    private HashMap<Integer, Consumer<Integer>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<Integer>> hourGetterMap = new HashMap<>();
    
    private Integer hour1PriceRiskFactorId;
    private Integer hour2PriceRiskFactorId;
    private Integer hour3PriceRiskFactorId;
    private Integer hour4PriceRiskFactorId;
    private Integer hour5PriceRiskFactorId;
    private Integer hour6PriceRiskFactorId;
    private Integer hour7PriceRiskFactorId;
    private Integer hour8PriceRiskFactorId;
    private Integer hour9PriceRiskFactorId;
    private Integer hour10PriceRiskFactorId;
    private Integer hour11PriceRiskFactorId;
    private Integer hour12PriceRiskFactorId;
    private Integer hour13PriceRiskFactorId;
    private Integer hour14PriceRiskFactorId;
    private Integer hour15PriceRiskFactorId;
    private Integer hour16PriceRiskFactorId;
    private Integer hour17PriceRiskFactorId;
    private Integer hour18PriceRiskFactorId;
    private Integer hour19PriceRiskFactorId;
    private Integer hour20PriceRiskFactorId;
    private Integer hour21PriceRiskFactorId;
    private Integer hour22PriceRiskFactorId;
    private Integer hour23PriceRiskFactorId;
    private Integer hour24PriceRiskFactorId;

    public HourPriceRiskFactorIdMap() {
        initializeGetterMap();
        initializeSetterMap();
    }

    @Transient
    public boolean isNotEmpty() {
        for (int i=1; i< 25; i++) {
            if (getHourPriceRiskFactorId(i) != null) {
                return true;
            }
        }
        return false;
    }


    public void copyFrom(HourPriceRiskFactorIdMap copy) {


        for (int i=1; i < 25; i++) {
            this.hourSetterMap.get(i).accept(
                    copy.hourGetterMap.get(i).get());
        }
    }

    public Integer getHourPriceRiskFactorId(int hourEnding) {
        return hourGetterMap.get(hourEnding).get();
    }

    public void setHourPriceRiskFactorId(int hourEnding, Integer riskFactorId ) {
        this.hourSetterMap.get(hourEnding).accept(riskFactorId);
    }

    @Column(name = "HOUR_1_RF_ID")
    public Integer getHour1PriceRiskFactorId() {
        return hour1PriceRiskFactorId;
    }

    public void setHour1PriceRiskFactorId(Integer hour1PriceRiskFactorId) {
        this.hour1PriceRiskFactorId = hour1PriceRiskFactorId;
    }

    @Column(name = "HOUR_2_RF_ID")
    public Integer getHour2PriceRiskFactorId() {
        return hour2PriceRiskFactorId;
    }

    public void setHour2PriceRiskFactorId(Integer hour2PriceRiskFactorId) {
        this.hour2PriceRiskFactorId = hour2PriceRiskFactorId;
    }

    @Column(name = "HOUR_3_RF_ID")
    public Integer getHour3PriceRiskFactorId() {
        return hour3PriceRiskFactorId;
    }

    public void setHour3PriceRiskFactorId(Integer hour3PriceRiskFactorId) {
        this.hour3PriceRiskFactorId = hour3PriceRiskFactorId;
    }

    @Column(name = "HOUR_4_RF_ID")
    public Integer getHour4PriceRiskFactorId() {
        return hour4PriceRiskFactorId;
    }

    public void setHour4PriceRiskFactorId(Integer hour4PriceRiskFactorId) {
        this.hour4PriceRiskFactorId = hour4PriceRiskFactorId;
    }

    @Column(name = "HOUR_5_RF_ID")
    public Integer getHour5PriceRiskFactorId() {
        return hour5PriceRiskFactorId;
    }

    public void setHour5PriceRiskFactorId(Integer hour5PriceRiskFactorId) {
        this.hour5PriceRiskFactorId = hour5PriceRiskFactorId;
    }

    @Column(name = "HOUR_6_RF_ID")
    public Integer getHour6PriceRiskFactorId() {
        return hour6PriceRiskFactorId;
    }

    public void setHour6PriceRiskFactorId(Integer hour6PriceRiskFactorId) {
        this.hour6PriceRiskFactorId = hour6PriceRiskFactorId;
    }

    @Column(name = "HOUR_7_RF_ID")
    public Integer getHour7PriceRiskFactorId() {
        return hour7PriceRiskFactorId;
    }

    public void setHour7PriceRiskFactorId(Integer hour7PriceRiskFactorId) {
        this.hour7PriceRiskFactorId = hour7PriceRiskFactorId;
    }

    @Column(name = "HOUR_8_RF_ID")
    public Integer getHour8PriceRiskFactorId() {
        return hour8PriceRiskFactorId;
    }

    public void setHour8PriceRiskFactorId(Integer hour8PriceRiskFactorId) {
        this.hour8PriceRiskFactorId = hour8PriceRiskFactorId;
    }

    @Column(name = "HOUR_9_RF_ID")
    public Integer getHour9PriceRiskFactorId() {
        return hour9PriceRiskFactorId;
    }

    public void setHour9PriceRiskFactorId(Integer hour9PriceRiskFactorId) {
        this.hour9PriceRiskFactorId = hour9PriceRiskFactorId;
    }

    @Column(name = "HOUR_10_RF_ID")
    public Integer getHour10PriceRiskFactorId() {
        return hour10PriceRiskFactorId;
    }

    public void setHour10PriceRiskFactorId(Integer hour10PriceRiskFactorId) {
        this.hour10PriceRiskFactorId = hour10PriceRiskFactorId;
    }

    @Column(name = "HOUR_11_RF_ID")
    public Integer getHour11PriceRiskFactorId() {
        return hour11PriceRiskFactorId;
    }

    public void setHour11PriceRiskFactorId(Integer hour11PriceRiskFactorId) {
        this.hour11PriceRiskFactorId = hour11PriceRiskFactorId;
    }

    @Column(name = "HOUR_12_RF_ID")
    public Integer getHour12PriceRiskFactorId() {
        return hour12PriceRiskFactorId;
    }

    public void setHour12PriceRiskFactorId(Integer hour12PriceRiskFactorId) {
        this.hour12PriceRiskFactorId = hour12PriceRiskFactorId;
    }

    @Column(name = "HOUR_13_RF_ID")
    public Integer getHour13PriceRiskFactorId() {
        return hour13PriceRiskFactorId;
    }

    public void setHour13PriceRiskFactorId(Integer hour13PriceRiskFactorId) {
        this.hour13PriceRiskFactorId = hour13PriceRiskFactorId;
    }

    @Column(name = "HOUR_14_RF_ID")
    public Integer getHour14PriceRiskFactorId() {
        return hour14PriceRiskFactorId;
    }

    public void setHour14PriceRiskFactorId(Integer hour14PriceRiskFactorId) {
        this.hour14PriceRiskFactorId = hour14PriceRiskFactorId;
    }

    @Column(name = "HOUR_15_RF_ID")
    public Integer getHour15PriceRiskFactorId() {
        return hour15PriceRiskFactorId;
    }

    public void setHour15PriceRiskFactorId(Integer hour15PriceRiskFactorId) {
        this.hour15PriceRiskFactorId = hour15PriceRiskFactorId;
    }

    @Column(name = "HOUR_16_RF_ID")
    public Integer getHour16PriceRiskFactorId() {
        return hour16PriceRiskFactorId;
    }

    public void setHour16PriceRiskFactorId(Integer hour16PriceRiskFactorId) {
        this.hour16PriceRiskFactorId = hour16PriceRiskFactorId;
    }

    @Column(name = "HOUR_17_RF_ID")
    public Integer getHour17PriceRiskFactorId() {
        return hour17PriceRiskFactorId;
    }

    public void setHour17PriceRiskFactorId(Integer hour17PriceRiskFactorId) {
        this.hour17PriceRiskFactorId = hour17PriceRiskFactorId;
    }

    @Column(name = "HOUR_18_RF_ID")
    public Integer getHour18PriceRiskFactorId() {
        return hour18PriceRiskFactorId;
    }

    public void setHour18PriceRiskFactorId(Integer hour18PriceRiskFactorId) {
        this.hour18PriceRiskFactorId = hour18PriceRiskFactorId;
    }

    @Column(name = "HOUR_19_RF_ID")
    public Integer getHour19PriceRiskFactorId() {
        return hour19PriceRiskFactorId;
    }

    public void setHour19PriceRiskFactorId(Integer hour19PriceRiskFactorId) {
        this.hour19PriceRiskFactorId = hour19PriceRiskFactorId;
    }

    @Column(name = "HOUR_20_RF_ID")
    public Integer getHour20PriceRiskFactorId() {
        return hour20PriceRiskFactorId;
    }

    public void setHour20PriceRiskFactorId(Integer hour20PriceRiskFactorId) {
        this.hour20PriceRiskFactorId = hour20PriceRiskFactorId;
    }

    @Column(name = "HOUR_21_RF_ID")
    public Integer getHour21PriceRiskFactorId() {
        return hour21PriceRiskFactorId;
    }

    public void setHour21PriceRiskFactorId(Integer hour21PriceRiskFactorId) {
        this.hour21PriceRiskFactorId = hour21PriceRiskFactorId;
    }

    @Column(name = "HOUR_22_RF_ID")
    public Integer getHour22PriceRiskFactorId() {
        return hour22PriceRiskFactorId;
    }

    public void setHour22PriceRiskFactorId(Integer hour22PriceRiskFactorId) {
        this.hour22PriceRiskFactorId = hour22PriceRiskFactorId;
    }

    @Column(name = "HOUR_23_RF_ID")
    public Integer getHour23PriceRiskFactorId() {
        return hour23PriceRiskFactorId;
    }

    public void setHour23PriceRiskFactorId(Integer hour23PriceRiskFactorId) {
        this.hour23PriceRiskFactorId = hour23PriceRiskFactorId;
    }

    @Column(name = "HOUR_24_RF_ID")
    public Integer getHour24PriceRiskFactorId() {
        return hour24PriceRiskFactorId;
    }

    public void setHour24PriceRiskFactorId(Integer hour24PriceRiskFactorId) {
        this.hour24PriceRiskFactorId = hour24PriceRiskFactorId;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, (Integer c) -> setHour1PriceRiskFactorId(c));
        hourSetterMap.put(2, (Integer c) -> setHour2PriceRiskFactorId(c));
        hourSetterMap.put(3, (Integer c) -> setHour3PriceRiskFactorId(c));
        hourSetterMap.put(4, (Integer c) -> setHour4PriceRiskFactorId(c));
        hourSetterMap.put(5, (Integer c) -> setHour5PriceRiskFactorId(c));
        hourSetterMap.put(6, (Integer c) -> setHour6PriceRiskFactorId(c));
        hourSetterMap.put(7, (Integer c) -> setHour7PriceRiskFactorId(c));
        hourSetterMap.put(8, (Integer c) -> setHour8PriceRiskFactorId(c));
        hourSetterMap.put(9, (Integer c) -> setHour9PriceRiskFactorId(c));
        hourSetterMap.put(10, (Integer c) -> setHour10PriceRiskFactorId(c));
        hourSetterMap.put(11, (Integer c) -> setHour11PriceRiskFactorId(c));
        hourSetterMap.put(12, (Integer c) -> setHour12PriceRiskFactorId(c));
        hourSetterMap.put(13, (Integer c) -> setHour13PriceRiskFactorId(c));
        hourSetterMap.put(14, (Integer c) -> setHour14PriceRiskFactorId(c));
        hourSetterMap.put(15, (Integer c) -> setHour15PriceRiskFactorId(c));
        hourSetterMap.put(16, (Integer c) -> setHour16PriceRiskFactorId(c));
        hourSetterMap.put(17, (Integer c) -> setHour17PriceRiskFactorId(c));
        hourSetterMap.put(18, (Integer c) -> setHour18PriceRiskFactorId(c));
        hourSetterMap.put(19, (Integer c) -> setHour19PriceRiskFactorId(c));
        hourSetterMap.put(20, (Integer c) -> setHour20PriceRiskFactorId(c));
        hourSetterMap.put(21, (Integer c) -> setHour21PriceRiskFactorId(c));
        hourSetterMap.put(22, (Integer c) -> setHour22PriceRiskFactorId(c));
        hourSetterMap.put(23, (Integer c) -> setHour23PriceRiskFactorId(c));
        hourSetterMap.put(24, (Integer c) -> setHour24PriceRiskFactorId(c));
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, () -> getHour1PriceRiskFactorId());
        hourGetterMap.put(2, () -> getHour2PriceRiskFactorId());
        hourGetterMap.put(3, () -> getHour3PriceRiskFactorId());
        hourGetterMap.put(4, () -> getHour4PriceRiskFactorId());
        hourGetterMap.put(5, () -> getHour5PriceRiskFactorId());
        hourGetterMap.put(6, () -> getHour6PriceRiskFactorId());
        hourGetterMap.put(7, () -> getHour7PriceRiskFactorId());
        hourGetterMap.put(8, () -> getHour8PriceRiskFactorId());
        hourGetterMap.put(9, () -> getHour9PriceRiskFactorId());
        hourGetterMap.put(10, () -> getHour10PriceRiskFactorId());
        hourGetterMap.put(11, () -> getHour11PriceRiskFactorId());
        hourGetterMap.put(12, () -> getHour12PriceRiskFactorId());
        hourGetterMap.put(13, () -> getHour13PriceRiskFactorId());
        hourGetterMap.put(14, () -> getHour14PriceRiskFactorId());
        hourGetterMap.put(15, () -> getHour15PriceRiskFactorId());
        hourGetterMap.put(16, () -> getHour16PriceRiskFactorId());
        hourGetterMap.put(17, () -> getHour17PriceRiskFactorId());
        hourGetterMap.put(18, () -> getHour18PriceRiskFactorId());
        hourGetterMap.put(19, () -> getHour19PriceRiskFactorId());
        hourGetterMap.put(20, () -> getHour20PriceRiskFactorId());
        hourGetterMap.put(21, () -> getHour21PriceRiskFactorId());
        hourGetterMap.put(22, () -> getHour22PriceRiskFactorId());
        hourGetterMap.put(23, () -> getHour23PriceRiskFactorId());
        hourGetterMap.put(24, () -> getHour24PriceRiskFactorId());
    }
    
}
