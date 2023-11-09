package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@Table (name = "PHYSICAL_POSITION")
public class PhysicalPosition extends DealPosition {


    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private FxRiskFactor dealPriceFxRiskFactor;

    private PriceRiskFactor marketPriceRiskFactor;
    private FxRiskFactor marketPriceFxRiskFactor;

    public PhysicalPosition() {
        super.getDealPositionDetail().setDealTypeCode(DealTypeCode.PHYSICAL_DEAL);
    }

    @Embedded
    public PhysicalPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalPositionDetail detail) {
        this.detail = detail;
    }

    public void createWith(
            BaseDeal deal,
            DealPositionSnapshot snapshot) {
        super.createWith(deal, snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        detail.copyFrom(physicalPositionSnapshot.getDetail());
        setAssociations(physicalPositionSnapshot);
        deal.addPosition(this);
    }

    public void updateWith(DealPositionSnapshot snapshot) {
        super.updateWith(snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        detail.copyFrom(physicalPositionSnapshot.getDetail());
        setAssociations(physicalPositionSnapshot);
        update();
    }

    private void setAssociations(PhysicalPositionSnapshot snapshot) {
        if (snapshot.getDealPriceFxRiskFactorId() != null)
            this.dealPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getDealPriceFxRiskFactorId());
        if (snapshot.getMarketPriceRiskFactorId() != null)
            this.marketPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getMarketPriceRiskFactorId());

        if (snapshot.getMarketFxRiskFactorId() != null)
            this.marketPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getMarketFxRiskFactorId());


    }

    @ManyToOne
    @JoinColumn(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getDealPriceFxRiskFactor() {
        return dealPriceFxRiskFactor;
    }

    public void setDealPriceFxRiskFactor(FxRiskFactor dealPriceFxRiskFactor) {
        this.dealPriceFxRiskFactor = dealPriceFxRiskFactor;
    }

    @ManyToOne
    @JoinColumn(name = "MKT_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getMarketPriceRiskFactor() {
        return marketPriceRiskFactor;
    }

    public void setMarketPriceRiskFactor(PriceRiskFactor marketPriceRiskFactor) {
        this.marketPriceRiskFactor = marketPriceRiskFactor;
    }

    @ManyToOne
    @JoinColumn(name = "MKT_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getMarketPriceFxRiskFactor() {
        return marketPriceFxRiskFactor;
    }

    public void setMarketPriceFxRiskFactor(FxRiskFactor marketPriceFxRiskFactor) {
        this.marketPriceFxRiskFactor = marketPriceFxRiskFactor;
    }

    @Override
    protected AuditAbstractEntity createHistory() {
        return PhysicalPositionAudit.create(this);
    }


    @Override
    public AuditAbstractEntity fetchRecentHistory() {
        return PhysicalPositionAudit.findRecentHistory(this);
    }

    private static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}