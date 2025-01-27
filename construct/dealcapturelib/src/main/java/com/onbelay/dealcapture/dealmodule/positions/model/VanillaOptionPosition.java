package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionPriceDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionPriceDetail;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("VO")
public class VanillaOptionPosition extends DealPosition {

    private PriceRiskFactor underlyingPriceRiskFactor;
    private FxRiskFactor underlyingFxRiskFactor;

    private VanillaOptionPositionPriceDetail priceDetail = new VanillaOptionPositionPriceDetail();

    public VanillaOptionPosition() {
        super(DealTypeCode.VANILLA_OPTION.getCode());

    }


    public void createWith(DealPositionSnapshot snapshot) {
        super.createWith(snapshot);
        VanillaOptionPositionSnapshot optionPositionSnapshot = (VanillaOptionPositionSnapshot) snapshot;
        priceDetail.copyFrom(optionPositionSnapshot.getPriceDetail());
        setAssociations(optionPositionSnapshot);
        save();
        postCreateWith(snapshot);
    }



    public void updateWith(DealPositionSnapshot snapshot) {
        super.updateWith(snapshot);
        VanillaOptionPositionSnapshot optionPositionSnapshot = (VanillaOptionPositionSnapshot) snapshot;
        priceDetail.copyFrom(optionPositionSnapshot.getPriceDetail());
        setAssociations(optionPositionSnapshot);
        postCreateWith(snapshot);
        update();
    }

    @Embedded
    public VanillaOptionPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(VanillaOptionPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    private void setAssociations(VanillaOptionPositionSnapshot snapshot) {


        if (snapshot.getUnderlyingPriceRiskFactorId() != null)
            this.underlyingPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getUnderlyingPriceRiskFactorId());

        if (snapshot.getUnderlyingFxRiskFactorId() != null)
            this.underlyingFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getUnderlyingFxRiskFactorId());
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UNDERLYING_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getUnderlyingPriceRiskFactor() {
        return underlyingPriceRiskFactor;
    }

    public void setUnderlyingPriceRiskFactor(PriceRiskFactor dealPriceRiskFactor) {
        this.underlyingPriceRiskFactor = dealPriceRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UNDERLYING_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getUnderlyingFxRiskFactor() {
        return underlyingFxRiskFactor;
    }

    public void setUnderlyingFxRiskFactor(FxRiskFactor underlyingFxRiskFactor) {
        this.underlyingFxRiskFactor = underlyingFxRiskFactor;
    }

    @Transient
    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}
