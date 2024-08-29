package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionPriceDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PHY")
public class PhysicalPosition extends DealPosition {

    private PriceRiskFactor dealPriceRiskFactor;
    private FxRiskFactor dealPriceFxRiskFactor;

    private PriceRiskFactor marketPriceRiskFactor;
    private FxRiskFactor marketPriceFxRiskFactor;

    private PhysicalPositionPriceDetail priceDetail = new PhysicalPositionPriceDetail();

    public PhysicalPosition() {
        super(DealTypeCode.PHYSICAL_DEAL.getCode());

    }


    public void createWith(DealPositionSnapshot snapshot) {
        super.createWith(snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        priceDetail.copyFrom(physicalPositionSnapshot.getPriceDetail());
        setAssociations(physicalPositionSnapshot);
        save();
        postCreateWith(snapshot);
    }



    public void updateWith(DealPositionSnapshot snapshot) {
        super.updateWith(snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        priceDetail.copyFrom(physicalPositionSnapshot.getPriceDetail());
        setAssociations(physicalPositionSnapshot);
        postCreateWith(snapshot);
        update();
    }

    private void setAssociations(PhysicalPositionSnapshot snapshot) {


        if (snapshot.getDealPriceRiskFactorId() != null)
            this.dealPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getDealPriceRiskFactorId());

        if (snapshot.getDealPriceFxRiskFactorId() != null)
            this.dealPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getDealPriceFxRiskFactorId());

        if (snapshot.getMarketPriceRiskFactorId() != null)
            this.marketPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getMarketPriceRiskFactorId());

        if (snapshot.getMarketPriceFxRiskFactorId() != null)
            this.marketPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getMarketPriceFxRiskFactorId());


    }

    @Embedded
    public PhysicalPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(PhysicalPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEAL_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getDealPriceRiskFactor() {
        return dealPriceRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getDealPriceFxRiskFactor() {
        return dealPriceFxRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MKT_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getMarketPriceRiskFactor() {
        return marketPriceRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MKT_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getMarketPriceFxRiskFactor() {
        return marketPriceFxRiskFactor;
    }

    public void setDealPriceRiskFactor(PriceRiskFactor dealPriceRiskFactor) {
        this.dealPriceRiskFactor = dealPriceRiskFactor;
    }

    public void setDealPriceFxRiskFactor(FxRiskFactor dealPriceFxRiskFactor) {
        this.dealPriceFxRiskFactor = dealPriceFxRiskFactor;
    }

    public void setMarketPriceRiskFactor(PriceRiskFactor marketPriceRiskFactor) {
        this.marketPriceRiskFactor = marketPriceRiskFactor;
    }

    public void setMarketPriceFxRiskFactor(FxRiskFactor marketPriceFxRiskFactor) {
        this.marketPriceFxRiskFactor = marketPriceFxRiskFactor;
    }

    @Transient
    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}
