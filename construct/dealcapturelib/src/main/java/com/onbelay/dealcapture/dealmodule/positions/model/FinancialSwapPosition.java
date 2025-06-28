package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionPriceDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FinancialSwap")
public class FinancialSwapPosition extends DealPosition {

    private PriceRiskFactor paysPriceRiskFactor;
    private FxRiskFactor paysFxRiskFactor;

    private PriceRiskFactor receivesPriceRiskFactor;
    private FxRiskFactor receivesFxRiskFactor;

    private FinancialSwapPositionPriceDetail priceDetail = new FinancialSwapPositionPriceDetail();

    public FinancialSwapPosition() {
        super(DealTypeCode.FINANCIAL_SWAP.getCode());

    }


    public void createWith(DealPositionSnapshot snapshot) {
        super.createWith(snapshot);
        FinancialSwapPositionSnapshot financialSwapPositionSnapshot = (FinancialSwapPositionSnapshot) snapshot;
        priceDetail.copyFrom(financialSwapPositionSnapshot.getPriceDetail());
        setAssociations(financialSwapPositionSnapshot);
        save();
        postCreateWith(snapshot);
    }



    public void updateWith(DealPositionSnapshot snapshot) {
        super.updateWith(snapshot);
        FinancialSwapPositionSnapshot financialSwapPositionSnapshot = (FinancialSwapPositionSnapshot) snapshot;
        priceDetail.copyFrom(financialSwapPositionSnapshot.getPriceDetail());
        setAssociations(financialSwapPositionSnapshot);
        postCreateWith(snapshot);
        update();
    }

    @Embedded
    public FinancialSwapPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(FinancialSwapPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    private void setAssociations(FinancialSwapPositionSnapshot snapshot) {


        if (snapshot.getPaysPriceRiskFactorId() != null)
            this.paysPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getPaysPriceRiskFactorId());

        if (snapshot.getPaysFxRiskFactorId() != null)
            this.paysFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getPaysFxRiskFactorId());

        if (snapshot.getReceivesPriceRiskFactorId() != null)
            this.receivesPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getReceivesPriceRiskFactorId());

        if (snapshot.getReceivesFxRiskFactorId() != null)
            this.receivesFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getReceivesFxRiskFactorId());


    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYS_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getPaysPriceRiskFactor() {
        return paysPriceRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYS_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getPaysFxRiskFactor() {
        return paysFxRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RECEIVES_PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getReceivesPriceRiskFactor() {
        return receivesPriceRiskFactor;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RECEIVES_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getReceivesFxRiskFactor() {
        return receivesFxRiskFactor;
    }

    public void setPaysPriceRiskFactor(PriceRiskFactor dealPriceRiskFactor) {
        this.paysPriceRiskFactor = dealPriceRiskFactor;
    }

    public void setPaysFxRiskFactor(FxRiskFactor dealPriceFxRiskFactor) {
        this.paysFxRiskFactor = dealPriceFxRiskFactor;
    }

    public void setReceivesPriceRiskFactor(PriceRiskFactor marketPriceRiskFactor) {
        this.receivesPriceRiskFactor = marketPriceRiskFactor;
    }

    public void setReceivesFxRiskFactor(FxRiskFactor marketPriceFxRiskFactor) {
        this.receivesFxRiskFactor = marketPriceFxRiskFactor;
    }

    @Transient
    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}
