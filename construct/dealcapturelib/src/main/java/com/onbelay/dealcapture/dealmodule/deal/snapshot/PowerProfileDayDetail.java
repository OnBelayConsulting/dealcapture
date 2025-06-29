package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PowerProfileDayDetail {

    private HashMap<Integer, Consumer<String>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<String>> hourGetterMap = new HashMap<>();

    private Integer dayOfWeek;

    private List<String> hours = new ArrayList<>();
    
    private String hour1FlowCodeValue;
    private String hour2FlowCodeValue;
    private String hour3FlowCodeValue;
    private String hour4FlowCodeValue;
    private String hour5FlowCodeValue;
    private String hour6FlowCodeValue;
    private String hour7FlowCodeValue;
    private String hour8FlowCodeValue;
    private String hour9FlowCodeValue;
    private String hour10FlowCodeValue;
    private String hour11FlowCodeValue;
    private String hour12FlowCodeValue;
    private String hour13FlowCodeValue;
    private String hour14FlowCodeValue;
    private String hour15FlowCodeValue;
    private String hour16FlowCodeValue;
    private String hour17FlowCodeValue;
    private String hour18FlowCodeValue;
    private String hour19FlowCodeValue;
    private String hour20FlowCodeValue;
    private String hour21FlowCodeValue;
    private String hour22FlowCodeValue;
    private String hour23FlowCodeValue;
    private String hour24FlowCodeValue;

    public PowerProfileDayDetail() {
        initializeGetterMap();
        initializeSetterMap();
    }

    public void setDefaults() {
        for (int i = 1; i < 25; i++)
            setPowerFlowCode(i, PowerFlowCode.NONE);
    }

    @Transient
    public List<String> getHours() {
        return hours;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    @Transient
    @JsonIgnore
    public PowerFlowCode getPowerFlowCode(int hourEnding) {
        return PowerFlowCode.lookUp(hourGetterMap.get(hourEnding).get());
    }

    public void setPowerFlowCode(int hourEnding, PowerFlowCode powerFlowCode) {
        assert(powerFlowCode != null);
        hourSetterMap.get(hourEnding).accept(powerFlowCode.getCode());
    }

    public void copyFrom(PowerProfileDayDetail copy) {

        if (copy.dayOfWeek != null)
            this.dayOfWeek = copy.dayOfWeek;


        for (int i=1; i < 25; i++) {
            this.hourSetterMap.get(i).accept(
                    copy.hourGetterMap.get(i).get());
        }
    }

    @Column(name = "DAY_OF_WEEK")
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Column(name = "HOUR_1_FLOW_CODE")
    public String getHour1FlowCodeValue() {
        return hour1FlowCodeValue;
    }

    public void setHour1FlowCodeValue(String hour1FlowCodeValue) {
        this.hour1FlowCodeValue = hour1FlowCodeValue;
    }

    @Column(name = "HOUR_2_FLOW_CODE")
    public String getHour2FlowCodeValue() {
        return hour2FlowCodeValue;
    }

    public void setHour2FlowCodeValue(String hour2FlowCodeValue) {
        this.hour2FlowCodeValue = hour2FlowCodeValue;
    }

    @Column(name = "HOUR_3_FLOW_CODE")
    public String getHour3FlowCodeValue() {
        return hour3FlowCodeValue;
    }

    public void setHour3FlowCodeValue(String hour3FlowCodeValue) {
        this.hour3FlowCodeValue = hour3FlowCodeValue;
    }

    @Column(name = "HOUR_4_FLOW_CODE")
    public String getHour4FlowCodeValue() {
        return hour4FlowCodeValue;
    }

    public void setHour4FlowCodeValue(String hour4FlowCodeValue) {
        this.hour4FlowCodeValue = hour4FlowCodeValue;
    }

    @Column(name = "HOUR_5_FLOW_CODE")
    public String getHour5FlowCodeValue() {
        return hour5FlowCodeValue;
    }

    public void setHour5FlowCodeValue(String hour5FlowCodeValue) {
        this.hour5FlowCodeValue = hour5FlowCodeValue;
    }

    @Column(name = "HOUR_6_FLOW_CODE")
    public String getHour6FlowCodeValue() {
        return hour6FlowCodeValue;
    }

    public void setHour6FlowCodeValue(String hour6FlowCodeValue) {
        this.hour6FlowCodeValue = hour6FlowCodeValue;
    }

    @Column(name = "HOUR_7_FLOW_CODE")
    public String getHour7FlowCodeValue() {
        return hour7FlowCodeValue;
    }

    public void setHour7FlowCodeValue(String hour7FlowCodeValue) {
        this.hour7FlowCodeValue = hour7FlowCodeValue;
    }

    @Column(name = "HOUR_8_FLOW_CODE")
    public String getHour8FlowCodeValue() {
        return hour8FlowCodeValue;
    }

    public void setHour8FlowCodeValue(String hour8FlowCodeValue) {
        this.hour8FlowCodeValue = hour8FlowCodeValue;
    }

    @Column(name = "HOUR_9_FLOW_CODE")
    public String getHour9FlowCodeValue() {
        return hour9FlowCodeValue;
    }

    public void setHour9FlowCodeValue(String hour9FlowCodeValue) {
        this.hour9FlowCodeValue = hour9FlowCodeValue;
    }

    @Column(name = "HOUR_10_FLOW_CODE")
    public String getHour10FlowCodeValue() {
        return hour10FlowCodeValue;
    }

    public void setHour10FlowCodeValue(String hour10FlowCodeValue) {
        this.hour10FlowCodeValue = hour10FlowCodeValue;
    }

    @Column(name = "HOUR_11_FLOW_CODE")
    public String getHour11FlowCodeValue() {
        return hour11FlowCodeValue;
    }

    public void setHour11FlowCodeValue(String hour11FlowCodeValue) {
        this.hour11FlowCodeValue = hour11FlowCodeValue;
    }

    @Column(name = "HOUR_12_FLOW_CODE")
    public String getHour12FlowCodeValue() {
        return hour12FlowCodeValue;
    }

    public void setHour12FlowCodeValue(String hour12FlowCodeValue) {
        this.hour12FlowCodeValue = hour12FlowCodeValue;
    }

    @Column(name = "HOUR_13_FLOW_CODE")
    public String getHour13FlowCodeValue() {
        return hour13FlowCodeValue;
    }

    public void setHour13FlowCodeValue(String hour13FlowCodeValue) {
        this.hour13FlowCodeValue = hour13FlowCodeValue;
    }

    @Column(name = "HOUR_14_FLOW_CODE")
    public String getHour14FlowCodeValue() {
        return hour14FlowCodeValue;
    }

    public void setHour14FlowCodeValue(String hour14FlowCodeValue) {
        this.hour14FlowCodeValue = hour14FlowCodeValue;
    }

    @Column(name = "HOUR_15_FLOW_CODE")
    public String getHour15FlowCodeValue() {
        return hour15FlowCodeValue;
    }

    public void setHour15FlowCodeValue(String hour15FlowCodeValue) {
        this.hour15FlowCodeValue = hour15FlowCodeValue;
    }

    @Column(name = "HOUR_16_FLOW_CODE")
    public String getHour16FlowCodeValue() {
        return hour16FlowCodeValue;
    }

    public void setHour16FlowCodeValue(String hour16FlowCodeValue) {
        this.hour16FlowCodeValue = hour16FlowCodeValue;
    }

    @Column(name = "HOUR_17_FLOW_CODE")
    public String getHour17FlowCodeValue() {
        return hour17FlowCodeValue;
    }

    public void setHour17FlowCodeValue(String hour17FlowCodeValue) {
        this.hour17FlowCodeValue = hour17FlowCodeValue;
    }

    @Column(name = "HOUR_18_FLOW_CODE")
    public String getHour18FlowCodeValue() {
        return hour18FlowCodeValue;
    }

    public void setHour18FlowCodeValue(String hour18FlowCodeValue) {
        this.hour18FlowCodeValue = hour18FlowCodeValue;
    }

    @Column(name = "HOUR_19_FLOW_CODE")
    public String getHour19FlowCodeValue() {
        return hour19FlowCodeValue;
    }

    public void setHour19FlowCodeValue(String hour19FlowCodeValue) {
        this.hour19FlowCodeValue = hour19FlowCodeValue;
    }

    @Column(name = "HOUR_20_FLOW_CODE")
    public String getHour20FlowCodeValue() {
        return hour20FlowCodeValue;
    }

    public void setHour20FlowCodeValue(String hour20FlowCodeValue) {
        this.hour20FlowCodeValue = hour20FlowCodeValue;
    }

    @Column(name = "HOUR_21_FLOW_CODE")
    public String getHour21FlowCodeValue() {
        return hour21FlowCodeValue;
    }

    public void setHour21FlowCodeValue(String hour21FlowCodeValue) {
        this.hour21FlowCodeValue = hour21FlowCodeValue;
    }

    @Column(name = "HOUR_22_FLOW_CODE")
    public String getHour22FlowCodeValue() {
        return hour22FlowCodeValue;
    }

    public void setHour22FlowCodeValue(String hour22FlowCodeValue) {
        this.hour22FlowCodeValue = hour22FlowCodeValue;
    }

    @Column(name = "HOUR_23_FLOW_CODE")
    public String getHour23FlowCodeValue() {
        return hour23FlowCodeValue;
    }

    public void setHour23FlowCodeValue(String hour23FlowCodeValue) {
        this.hour23FlowCodeValue = hour23FlowCodeValue;
    }

    @Column(name = "HOUR_24_FLOW_CODE")
    public String getHour24FlowCodeValue() {
        return hour24FlowCodeValue;
    }

    public void setHour24FlowCodeValue(String hour24FlowCodeValue) {
        this.hour24FlowCodeValue = hour24FlowCodeValue;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, (String c) -> setHour1FlowCodeValue(c));
        hourSetterMap.put(2, (String c) -> setHour2FlowCodeValue(c));
        hourSetterMap.put(3, (String c) -> setHour3FlowCodeValue(c));
        hourSetterMap.put(4, (String c) -> setHour4FlowCodeValue(c));
        hourSetterMap.put(5, (String c) -> setHour5FlowCodeValue(c));
        hourSetterMap.put(6, (String c) -> setHour6FlowCodeValue(c));
        hourSetterMap.put(7, (String c) -> setHour7FlowCodeValue(c));
        hourSetterMap.put(8, (String c) -> setHour8FlowCodeValue(c));
        hourSetterMap.put(9, (String c) -> setHour9FlowCodeValue(c));
        hourSetterMap.put(10, (String c) -> setHour10FlowCodeValue(c));
        hourSetterMap.put(11, (String c) -> setHour11FlowCodeValue(c));
        hourSetterMap.put(12, (String c) -> setHour12FlowCodeValue(c));
        hourSetterMap.put(13, (String c) -> setHour13FlowCodeValue(c));
        hourSetterMap.put(14, (String c) -> setHour14FlowCodeValue(c));
        hourSetterMap.put(15, (String c) -> setHour15FlowCodeValue(c));
        hourSetterMap.put(16, (String c) -> setHour16FlowCodeValue(c));
        hourSetterMap.put(17, (String c) -> setHour17FlowCodeValue(c));
        hourSetterMap.put(18, (String c) -> setHour18FlowCodeValue(c));
        hourSetterMap.put(19, (String c) -> setHour19FlowCodeValue(c));
        hourSetterMap.put(20, (String c) -> setHour20FlowCodeValue(c));
        hourSetterMap.put(21, (String c) -> setHour21FlowCodeValue(c));
        hourSetterMap.put(22, (String c) -> setHour22FlowCodeValue(c));
        hourSetterMap.put(23, (String c) -> setHour23FlowCodeValue(c));
        hourSetterMap.put(24, (String c) -> setHour24FlowCodeValue(c));
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, () -> getHour1FlowCodeValue());
        hourGetterMap.put(2, () -> getHour2FlowCodeValue());
        hourGetterMap.put(3, () -> getHour3FlowCodeValue());
        hourGetterMap.put(4, () -> getHour4FlowCodeValue());
        hourGetterMap.put(5, () -> getHour5FlowCodeValue());
        hourGetterMap.put(6, () -> getHour6FlowCodeValue());
        hourGetterMap.put(7, () -> getHour7FlowCodeValue());
        hourGetterMap.put(8, () -> getHour8FlowCodeValue());
        hourGetterMap.put(9, () -> getHour9FlowCodeValue());
        hourGetterMap.put(10, () -> getHour10FlowCodeValue());
        hourGetterMap.put(11, () -> getHour11FlowCodeValue());
        hourGetterMap.put(12, () -> getHour12FlowCodeValue());
        hourGetterMap.put(13, () -> getHour13FlowCodeValue());
        hourGetterMap.put(14, () -> getHour14FlowCodeValue());
        hourGetterMap.put(15, () -> getHour15FlowCodeValue());
        hourGetterMap.put(16, () -> getHour16FlowCodeValue());
        hourGetterMap.put(17, () -> getHour17FlowCodeValue());
        hourGetterMap.put(18, () -> getHour18FlowCodeValue());
        hourGetterMap.put(19, () -> getHour19FlowCodeValue());
        hourGetterMap.put(20, () -> getHour20FlowCodeValue());
        hourGetterMap.put(21, () -> getHour21FlowCodeValue());
        hourGetterMap.put(22, () -> getHour22FlowCodeValue());
        hourGetterMap.put(23, () -> getHour23FlowCodeValue());
        hourGetterMap.put(24, () -> getHour24FlowCodeValue());
    }
    
}
