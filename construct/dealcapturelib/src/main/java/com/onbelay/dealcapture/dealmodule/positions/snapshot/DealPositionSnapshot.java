package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dealTypeValue")
@JsonSubTypes( {
        @JsonSubTypes.Type(value = PhysicalPositionSnapshot.class, name = "PHY"),
        @JsonSubTypes.Type(value = ErrorDealPositionSnapshot.class, name = "E"),
})

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DealPositionSnapshot extends AbstractSnapshot {

    private EntityId dealId;

    private DealPositionDetail detail = new DealPositionDetail();

    private PositionSettlementDetail settlementDetail = new PositionSettlementDetail();

    private List<PositionRiskFactorMappingSnapshot> riskFactorMappingSnapshots = new ArrayList<>();

    private List<DealHourlyPositionSnapshot> hourlyPositionSnapshots = new ArrayList<>();

    private String dealTypeValue;
    private EntityId fixedPriceFxRiskFactorId;

    public DealPositionSnapshot() {
    }
    
    public DealPositionSnapshot(DealTypeCode code) {
        this.dealTypeValue = code.getCode();
    }

    public DealPositionSnapshot(DealTypeCode dealType, String errorCode) {
        super(errorCode);
        this.dealTypeValue = dealType.getCode();
    }

    public DealPositionSnapshot(
            DealTypeCode dealType,
            String errorCode,
            boolean isPermissionException) {
        super(errorCode, isPermissionException);
        this.dealTypeValue = dealType.getCode();
    }

    public DealPositionSnapshot(
            DealTypeCode dealType,
            String errorCode,
            List<String> parameters) {
        super(errorCode, parameters);
        this.dealTypeValue = dealType.getCode();
    }
    

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }


    public String getDealTypeValue() {
        return dealTypeValue;
    }

    public void setDealTypeValue(String dealTypeValue) {
        this.dealTypeValue = dealTypeValue;
    }

    @JsonIgnore
    public DealTypeCode getDealType() {
        return DealTypeCode.lookUp(dealTypeValue);
    }


    public DealPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealPositionDetail detail) {
        this.detail = detail;
    }

    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public void setSettlementDetail(PositionSettlementDetail settlementDetail) {
        this.settlementDetail = settlementDetail;
    }

    public void addRiskFactorMappingSnapshot(PositionRiskFactorMappingSnapshot snapshot) {
        riskFactorMappingSnapshots.add(snapshot);
    }

    public List<PositionRiskFactorMappingSnapshot> getRiskFactorMappingSnapshots() {
        return riskFactorMappingSnapshots;
    }

    public void setRiskFactorMappingSnapshots(List<PositionRiskFactorMappingSnapshot> riskFactorMappingSnapshots) {
        this.riskFactorMappingSnapshots = riskFactorMappingSnapshots;
    }

    public List<DealHourlyPositionSnapshot> getHourlyPositionSnapshots() {
        return hourlyPositionSnapshots;
    }

    public void setHourlyPositionSnapshots(List<DealHourlyPositionSnapshot> hourlyPositionSnapshots) {
        this.hourlyPositionSnapshots = hourlyPositionSnapshots;
    }

    @Transient
    @JsonIgnore
    public List<PositionRiskFactorMappingSnapshot> getDealPriceMappings() {
        return riskFactorMappingSnapshots.stream().filter( c-> c.getDetail().getPriceTypeCode() == PriceTypeCode.DEAL_PRICE).collect(Collectors.toList());
    }


    public void setIdInMappings() {
        riskFactorMappingSnapshots.forEach( c->c.setDealPositionId(getEntityId()));
    }


    @Transient
    @JsonIgnore
    public List<PositionRiskFactorMappingSnapshot> getMarketPriceMappings() {
        return riskFactorMappingSnapshots.stream().filter( c-> c.getDetail().getPriceTypeCode() == PriceTypeCode.MARKET_PRICE).collect(Collectors.toList());
    }

    public EntityId getFixedPriceFxRiskFactorId() {
        return fixedPriceFxRiskFactorId;
    }

    public void setFixedPriceFxRiskFactorId(EntityId fixedPriceFxRiskFactorId) {
        this.fixedPriceFxRiskFactorId = fixedPriceFxRiskFactorId;
    }
}
