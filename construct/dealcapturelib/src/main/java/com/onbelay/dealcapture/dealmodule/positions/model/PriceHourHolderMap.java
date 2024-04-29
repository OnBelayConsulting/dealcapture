package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PriceHourHolderMap {

    private HashMap<Integer, Consumer<PriceRiskFactorHolder>> hourSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<PriceRiskFactorHolder>> hourGetterMap = new HashMap<>();
    
    private PriceRiskFactorHolder hour1PriceHolder;
    private PriceRiskFactorHolder hour2PriceHolder;
    private PriceRiskFactorHolder hour3PriceHolder;
    private PriceRiskFactorHolder hour4PriceHolder;
    private PriceRiskFactorHolder hour5PriceHolder;
    private PriceRiskFactorHolder hour6PriceHolder;
    private PriceRiskFactorHolder hour7PriceHolder;
    private PriceRiskFactorHolder hour8PriceHolder;
    private PriceRiskFactorHolder hour9PriceHolder;
    private PriceRiskFactorHolder hour10PriceHolder;
    private PriceRiskFactorHolder hour11PriceHolder;
    private PriceRiskFactorHolder hour12PriceHolder;
    private PriceRiskFactorHolder hour13PriceHolder;
    private PriceRiskFactorHolder hour14PriceHolder;
    private PriceRiskFactorHolder hour15PriceHolder;
    private PriceRiskFactorHolder hour16PriceHolder;
    private PriceRiskFactorHolder hour17PriceHolder;
    private PriceRiskFactorHolder hour18PriceHolder;
    private PriceRiskFactorHolder hour19PriceHolder;
    private PriceRiskFactorHolder hour20PriceHolder;
    private PriceRiskFactorHolder hour21PriceHolder;
    private PriceRiskFactorHolder hour22PriceHolder;
    private PriceRiskFactorHolder hour23PriceHolder;
    private PriceRiskFactorHolder hour24PriceHolder;

    public PriceHourHolderMap() {
        initializeGetterMap();
        initializeSetterMap();
    }

    public boolean isNotEmpty() {
        for (int i=1; i< 25; i++) {
            if (getHourPriceHolder(i) != null) {
                return true;
            }
        }
        return false;
    }


    public PriceRiskFactorHolder getHourPriceHolder(int hourEnding) {
        return hourGetterMap.get(hourEnding).get();
    }

    public void setHourPriceHolder(int hourEnding, PriceRiskFactorHolder hourPriceHolder) {
        hourSetterMap.get(hourEnding).accept(hourPriceHolder);
    }

    public PriceRiskFactorHolder getHour1PriceHolder() {
        return hour1PriceHolder;
    }

    public void setHour1PriceHolder(PriceRiskFactorHolder hour1PriceHolder) {
        this.hour1PriceHolder = hour1PriceHolder;
    }

    public PriceRiskFactorHolder getHour2PriceHolder() {
        return hour2PriceHolder;
    }

    public void setHour2PriceHolder(PriceRiskFactorHolder hour2PriceHolder) {
        this.hour2PriceHolder = hour2PriceHolder;
    }

    public PriceRiskFactorHolder getHour3PriceHolder() {
        return hour3PriceHolder;
    }

    public void setHour3PriceHolder(PriceRiskFactorHolder hour3PriceHolder) {
        this.hour3PriceHolder = hour3PriceHolder;
    }

    public PriceRiskFactorHolder getHour4PriceHolder() {
        return hour4PriceHolder;
    }

    public void setHour4PriceHolder(PriceRiskFactorHolder hour4PriceHolder) {
        this.hour4PriceHolder = hour4PriceHolder;
    }

    public PriceRiskFactorHolder getHour5PriceHolder() {
        return hour5PriceHolder;
    }

    public void setHour5PriceHolder(PriceRiskFactorHolder hour5PriceHolder) {
        this.hour5PriceHolder = hour5PriceHolder;
    }

    public PriceRiskFactorHolder getHour6PriceHolder() {
        return hour6PriceHolder;
    }

    public void setHour6PriceHolder(PriceRiskFactorHolder hour6PriceHolder) {
        this.hour6PriceHolder = hour6PriceHolder;
    }

    public PriceRiskFactorHolder getHour7PriceHolder() {
        return hour7PriceHolder;
    }

    public void setHour7PriceHolder(PriceRiskFactorHolder hour7PriceHolder) {
        this.hour7PriceHolder = hour7PriceHolder;
    }

    public PriceRiskFactorHolder getHour8PriceHolder() {
        return hour8PriceHolder;
    }

    public void setHour8PriceHolder(PriceRiskFactorHolder hour8PriceHolder) {
        this.hour8PriceHolder = hour8PriceHolder;
    }

    public PriceRiskFactorHolder getHour9PriceHolder() {
        return hour9PriceHolder;
    }

    public void setHour9PriceHolder(PriceRiskFactorHolder hour9PriceHolder) {
        this.hour9PriceHolder = hour9PriceHolder;
    }

    public PriceRiskFactorHolder getHour10PriceHolder() {
        return hour10PriceHolder;
    }

    public void setHour10PriceHolder(PriceRiskFactorHolder hour10PriceHolder) {
        this.hour10PriceHolder = hour10PriceHolder;
    }

    public PriceRiskFactorHolder getHour11PriceHolder() {
        return hour11PriceHolder;
    }

    public void setHour11PriceHolder(PriceRiskFactorHolder hour11PriceHolder) {
        this.hour11PriceHolder = hour11PriceHolder;
    }

    public PriceRiskFactorHolder getHour12PriceHolder() {
        return hour12PriceHolder;
    }

    public void setHour12PriceHolder(PriceRiskFactorHolder hour12PriceHolder) {
        this.hour12PriceHolder = hour12PriceHolder;
    }

    public PriceRiskFactorHolder getHour13PriceHolder() {
        return hour13PriceHolder;
    }

    public void setHour13PriceHolder(PriceRiskFactorHolder hour13PriceHolder) {
        this.hour13PriceHolder = hour13PriceHolder;
    }

    public PriceRiskFactorHolder getHour14PriceHolder() {
        return hour14PriceHolder;
    }

    public void setHour14PriceHolder(PriceRiskFactorHolder hour14PriceHolder) {
        this.hour14PriceHolder = hour14PriceHolder;
    }

    public PriceRiskFactorHolder getHour15PriceHolder() {
        return hour15PriceHolder;
    }

    public void setHour15PriceHolder(PriceRiskFactorHolder hour15PriceHolder) {
        this.hour15PriceHolder = hour15PriceHolder;
    }

    public PriceRiskFactorHolder getHour16PriceHolder() {
        return hour16PriceHolder;
    }

    public void setHour16PriceHolder(PriceRiskFactorHolder hour16PriceHolder) {
        this.hour16PriceHolder = hour16PriceHolder;
    }

    public PriceRiskFactorHolder getHour17PriceHolder() {
        return hour17PriceHolder;
    }

    public void setHour17PriceHolder(PriceRiskFactorHolder hour17PriceHolder) {
        this.hour17PriceHolder = hour17PriceHolder;
    }

    public PriceRiskFactorHolder getHour18PriceHolder() {
        return hour18PriceHolder;
    }

    public void setHour18PriceHolder(PriceRiskFactorHolder hour18PriceHolder) {
        this.hour18PriceHolder = hour18PriceHolder;
    }

    public PriceRiskFactorHolder getHour19PriceHolder() {
        return hour19PriceHolder;
    }

    public void setHour19PriceHolder(PriceRiskFactorHolder hour19PriceHolder) {
        this.hour19PriceHolder = hour19PriceHolder;
    }

    public PriceRiskFactorHolder getHour20PriceHolder() {
        return hour20PriceHolder;
    }

    public void setHour20PriceHolder(PriceRiskFactorHolder hour20PriceHolder) {
        this.hour20PriceHolder = hour20PriceHolder;
    }

    public PriceRiskFactorHolder getHour21PriceHolder() {
        return hour21PriceHolder;
    }

    public void setHour21PriceHolder(PriceRiskFactorHolder hour21PriceHolder) {
        this.hour21PriceHolder = hour21PriceHolder;
    }

    public PriceRiskFactorHolder getHour22PriceHolder() {
        return hour22PriceHolder;
    }

    public void setHour22PriceHolder(PriceRiskFactorHolder hour22PriceHolder) {
        this.hour22PriceHolder = hour22PriceHolder;
    }

    public PriceRiskFactorHolder getHour23PriceHolder() {
        return hour23PriceHolder;
    }

    public void setHour23PriceHolder(PriceRiskFactorHolder hour23PriceHolder) {
        this.hour23PriceHolder = hour23PriceHolder;
    }

    public PriceRiskFactorHolder getHour24PriceHolder() {
        return hour24PriceHolder;
    }

    public void setHour24PriceHolder(PriceRiskFactorHolder hour24PriceHolder) {
        this.hour24PriceHolder = hour24PriceHolder;
    }


    private void initializeSetterMap() {
        hourSetterMap.put(1, this::setHour1PriceHolder);
        hourSetterMap.put(2, this::setHour2PriceHolder);
        hourSetterMap.put(3, this::setHour3PriceHolder);
        hourSetterMap.put(4, this::setHour4PriceHolder);
        hourSetterMap.put(5, this::setHour5PriceHolder);
        hourSetterMap.put(6, this::setHour6PriceHolder);
        hourSetterMap.put(7, this::setHour7PriceHolder);
        hourSetterMap.put(8, this::setHour8PriceHolder);
        hourSetterMap.put(9, this::setHour9PriceHolder);
        hourSetterMap.put(10, this::setHour10PriceHolder);
        hourSetterMap.put(11, this::setHour11PriceHolder);
        hourSetterMap.put(12, this::setHour12PriceHolder);
        hourSetterMap.put(13, this::setHour13PriceHolder);
        hourSetterMap.put(14, this::setHour14PriceHolder);
        hourSetterMap.put(15, this::setHour15PriceHolder);
        hourSetterMap.put(16, this::setHour16PriceHolder);
        hourSetterMap.put(17, this::setHour17PriceHolder);
        hourSetterMap.put(18, this::setHour18PriceHolder);
        hourSetterMap.put(19, this::setHour19PriceHolder);
        hourSetterMap.put(20, this::setHour20PriceHolder);
        hourSetterMap.put(21, this::setHour21PriceHolder);
        hourSetterMap.put(22, this::setHour22PriceHolder);
        hourSetterMap.put(23, this::setHour23PriceHolder);
        hourSetterMap.put(24, this::setHour24PriceHolder);
    }

    private void initializeGetterMap() {
        hourGetterMap.put(1, this::getHour1PriceHolder);
        hourGetterMap.put(2, this::getHour2PriceHolder);
        hourGetterMap.put(3, this::getHour3PriceHolder);
        hourGetterMap.put(4, this::getHour4PriceHolder);
        hourGetterMap.put(5, this::getHour5PriceHolder);
        hourGetterMap.put(6, this::getHour6PriceHolder);
        hourGetterMap.put(7, this::getHour7PriceHolder);
        hourGetterMap.put(8, this::getHour8PriceHolder);
        hourGetterMap.put(9, this::getHour9PriceHolder);
        hourGetterMap.put(10, this::getHour10PriceHolder);
        hourGetterMap.put(11, this::getHour11PriceHolder);
        hourGetterMap.put(12, this::getHour12PriceHolder);
        hourGetterMap.put(13, this::getHour13PriceHolder);
        hourGetterMap.put(14, this::getHour14PriceHolder);
        hourGetterMap.put(15, this::getHour15PriceHolder);
        hourGetterMap.put(16, this::getHour16PriceHolder);
        hourGetterMap.put(17, this::getHour17PriceHolder);
        hourGetterMap.put(18, this::getHour18PriceHolder);
        hourGetterMap.put(19, this::getHour19PriceHolder);
        hourGetterMap.put(20, this::getHour20PriceHolder);
        hourGetterMap.put(21, this::getHour21PriceHolder);
        hourGetterMap.put(22, this::getHour22PriceHolder);
        hourGetterMap.put(23, this::getHour23PriceHolder);
        hourGetterMap.put(24, this::getHour24PriceHolder);
    }
}
