package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CostPositionDetail {

    private String cost1Name;
    private BigDecimal cost1Amount;
    private String cost2Name;
    private BigDecimal cost2Amount;
    private String cost3Name;
    private BigDecimal cost3Amount;
    private String cost4Name;
    private BigDecimal cost4Amount;
    private String cost5Name;
    private BigDecimal cost5Amount;

    private HashMap<Integer, Consumer<String>> costNameSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<String>> costNameGetterMap = new HashMap<>();

    private HashMap<Integer, Consumer<BigDecimal>> costAmtSetterMap = new HashMap<>();
    private  HashMap<Integer, Supplier<BigDecimal>> costAmtGetterMap = new HashMap<>();

    public CostPositionDetail() {
        initializeSetterMaps();
        initializeGetterMaps();
    }

    @Transient
    @JsonIgnore
    public BigDecimal getCostAmount(Integer day) {
        return costAmtGetterMap.get(day).get();
    }

    public void setCostAmount(Integer day, BigDecimal value) {
        costAmtSetterMap.get(day).accept(value);
    }

    @Transient
    @JsonIgnore
    public String getCostName(Integer day) {
        return costNameGetterMap.get(day).get();
    }

    public CostTypeCode getCostTypeCode(Integer day) {
        String name = costNameGetterMap.get(day).get();
        if (name != null)
            return CostNameCode.lookUp(name).getCostTypeCode();
        else
            return null;
    }

    public void setCostName(Integer day, String value) {
        costNameSetterMap.get(day).accept(value);
    }


    public void copyFrom(CostPositionDetail copy) {
        if (copy == null)
            return;

        for (int i=0; i < 5; i++) {
            int selector = i+1;
            this.costAmtSetterMap.get(selector).accept(
                    copy.costAmtGetterMap.get(selector).get());
            this.costNameSetterMap.get(selector).accept(
                    copy.costNameGetterMap.get(selector).get());
        }
    }

    private void initializeSetterMaps() {
        costAmtSetterMap.put(1, (BigDecimal c) -> setCost1Amount(c));
        costAmtSetterMap.put(2, (BigDecimal c) -> setCost2Amount(c));
        costAmtSetterMap.put(3, (BigDecimal c) -> setCost3Amount(c));
        costAmtSetterMap.put(4, (BigDecimal c) -> setCost4Amount(c));
        costAmtSetterMap.put(5, (BigDecimal c) -> setCost5Amount(c));

        costNameSetterMap.put(1, (String c) -> setCost1Name(c));
        costNameSetterMap.put(2, (String c) -> setCost2Name(c));
        costNameSetterMap.put(3, (String c) -> setCost3Name(c));
        costNameSetterMap.put(4, (String c) -> setCost4Name(c));
        costNameSetterMap.put(5, (String c) -> setCost5Name(c));

    }

    private void initializeGetterMaps() {
        costAmtGetterMap.put(1, () -> getCost1Amount());
        costAmtGetterMap.put(2, () -> getCost2Amount());
        costAmtGetterMap.put(3, () -> getCost3Amount());
        costAmtGetterMap.put(4, () -> getCost4Amount());
        costAmtGetterMap.put(5, () -> getCost5Amount());

        costNameGetterMap.put(1, () -> getCost1Name());
        costNameGetterMap.put(2, () -> getCost2Name());
        costNameGetterMap.put(3, () -> getCost3Name());
        costNameGetterMap.put(4, () -> getCost4Name());
        costNameGetterMap.put(5, () -> getCost5Name());


    }

    @Column(name = "COST_1_NAME")
    public String getCost1Name() {
        return cost1Name;
    }

    public void setCost1Name(String cost1Name) {
        this.cost1Name = cost1Name;
    }

    @Column(name = "COST_1_AMOUNT")
    public BigDecimal getCost1Amount() {
        return cost1Amount;
    }

    public void setCost1Amount(BigDecimal cost1Amount) {
        this.cost1Amount = cost1Amount;
    }

    @Column(name = "COST_2_NAME")
    public String getCost2Name() {
        return cost2Name;
    }

    public void setCost2Name(String cost2Name) {
        this.cost2Name = cost2Name;
    }

    @Column(name = "COST_2_AMOUNT")
    public BigDecimal getCost2Amount() {
        return cost2Amount;
    }

    public void setCost2Amount(BigDecimal cost2Amount) {
        this.cost2Amount = cost2Amount;
    }

    @Column(name = "COST_3_NAME")
    public String getCost3Name() {
        return cost3Name;
    }

    public void setCost3Name(String cost3Name) {
        this.cost3Name = cost3Name;
    }

    @Column(name = "COST_3_AMOUNT")
    public BigDecimal getCost3Amount() {
        return cost3Amount;
    }

    public void setCost3Amount(BigDecimal cost3Amount) {
        this.cost3Amount = cost3Amount;
    }

    @Column(name = "COST_4_NAME")
    public String getCost4Name() {
        return cost4Name;
    }

    public void setCost4Name(String cost4Name) {
        this.cost4Name = cost4Name;
    }

    @Column(name = "COST_4_AMOUNT")
    public BigDecimal getCost4Amount() {
        return cost4Amount;
    }

    public void setCost4Amount(BigDecimal cost4Amount) {
        this.cost4Amount = cost4Amount;
    }

    @Column(name = "COST_5_NAME")
    public String getCost5Name() {
        return cost5Name;
    }

    public void setCost5Name(String cost5Name) {
        this.cost5Name = cost5Name;
    }

    @Column(name = "COST_5_AMOUNT")
    public BigDecimal getCost5Amount() {
        return cost5Amount;
    }

    public void setCost5Amount(BigDecimal cost5Amount) {
        this.cost5Amount = cost5Amount;
    }
}
