package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class FinancialSwapPositionSnapshot extends DealPositionSnapshot {

    private String receivesPriceIndexName;
    private EntityId receivesPriceRiskFactorId;

    private EntityId receivesFxRiskFactorId;

    private String paysPriceIndexName;
    private EntityId paysPriceRiskFactorId;

    private EntityId paysFxRiskFactorId;

    private FinancialSwapPositionPriceDetail priceDetail = new FinancialSwapPositionPriceDetail();

    public FinancialSwapPositionSnapshot() {
        super(DealTypeCode.FINANCIAL_SWAP);
    }

    public FinancialSwapPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(FinancialSwapPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    public EntityId getReceivesPriceRiskFactorId() {
        return receivesPriceRiskFactorId;
    }

    public void setReceivesPriceRiskFactorId(EntityId receivesPriceRiskFactorId) {
        this.receivesPriceRiskFactorId = receivesPriceRiskFactorId;
    }

    public EntityId getReceivesFxRiskFactorId() {
        return receivesFxRiskFactorId;
    }

    public void setReceivesFxRiskFactorId(EntityId receivesFxRiskFactorId) {
        this.receivesFxRiskFactorId = receivesFxRiskFactorId;
    }

    public EntityId getPaysPriceRiskFactorId() {
        return paysPriceRiskFactorId;
    }

    public void setPaysPriceRiskFactorId(EntityId paysPriceRiskFactorId) {
        this.paysPriceRiskFactorId = paysPriceRiskFactorId;
    }

    public EntityId getPaysFxRiskFactorId() {
        return paysFxRiskFactorId;
    }

    public void setPaysFxRiskFactorId(EntityId paysFxRiskFactorId) {
        this.paysFxRiskFactorId = paysFxRiskFactorId;
    }

    public String getReceivesPriceIndexName() {
        return receivesPriceIndexName;
    }

    public void setReceivesPriceIndexName(String receivesPriceIndexName) {
        this.receivesPriceIndexName = receivesPriceIndexName;
    }

    public String getPaysPriceIndexName() {
        return paysPriceIndexName;
    }

    public void setPaysPriceIndexName(String paysPriceIndexName) {
        this.paysPriceIndexName = paysPriceIndexName;
    }
}
