package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PHY")
@NamedQueries({
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_PHYSICAL_POSITION_REPORT_BY_DEAL,
                query = " SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport( " +
                        "       position.deal.id," +
                        "       deal.dealDetail.buySellCodeValue," +
                        "       position.id," +
                        "       position.dealPositionDetail.volumeQuantityValue," +
                        "       position.dealPositionDetail.volumeUnitOfMeasureValue," +
                        "       position.dealPositionDetail.currencyCodeValue," +
                        "       position.detail.dealPriceValuationValue," +
                        "       position.detail.fixedPriceValue," +
                        "       position.detail.fixedPriceCurrencyCodeValue," +
                        "       fixedFxFactor.detail.value," +
                        "       position.detail.fixedPriceUnitOfMeasureCodeValue," +
                        "       priceIndexFactor.detail.value, " +
                        "       priceIndex.detail.currencyCodeValue, " +
                        "       priceIndex.detail.unitOfMeasureCodeValue, " +
                        "       priceIndexFxFactor.detail.value, " +
                        "       position.detail.marketPriceValuationValue," +
                        "       marketIndexFactor.detail.value,  " +
                        "       marketIndex.detail.currencyCodeValue,  " +
                        "       marketIndex.detail.unitOfMeasureCodeValue," +
                        "       marketIndexFxFactor.detail.value) " +
                        "     FROM PhysicalPosition position " +
                        "LEFT JOIN position.fixedPriceFxRiskFactor as fixedFxFactor " +
                        "LEFT JOIN position.dealPriceRiskFactor as priceIndexFactor " +
                        "LEFT JOIN priceIndexFactor.index as priceIndex " +
                        "LEFT JOIN position.dealPriceFxRiskFactor as priceIndexFxFactor " +
                        "     JOIN position.marketPriceRiskFactor as marketIndexFactor " +
                        "     JOIN marketIndexFactor.index as marketIndex " +
                        "lEFT JOIN position.marketPriceFxRiskFactor as marketIndexFxFactor " +
                        "    WHERE position.deal.id = :dealId  "),
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_PHYSICAL_POSITION_REPORTS,
                query = " SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport( " +
                        "       position.deal.id," +
                        "       deal.dealDetail.buySellCodeValue," +
                        "       position.id," +
                        "       position.dealPositionDetail.volumeQuantityValue," +
                        "       position.dealPositionDetail.volumeUnitOfMeasureValue," +
                        "       position.dealPositionDetail.currencyCodeValue," +
                        "       position.detail.dealPriceValuationValue," +
                        "       position.detail.fixedPriceValue," +
                        "       position.detail.fixedPriceCurrencyCodeValue," +
                        "       fixedFxFactor.detail.value," +
                        "       position.detail.fixedPriceUnitOfMeasureCodeValue," +
                        "       priceIndexFactor.detail.value, " +
                        "       priceIndex.detail.currencyCodeValue, " +
                        "       priceIndex.detail.unitOfMeasureCodeValue, " +
                        "       priceIndexFxFactor.detail.value, " +
                        "       position.detail.marketPriceValuationValue," +
                        "       marketIndexFactor.detail.value,  " +
                        "       marketIndex.detail.currencyCodeValue,  " +
                        "       marketIndex.detail.unitOfMeasureCodeValue," +
                        "       marketIndexFxFactor.detail.value) " +
                        "     FROM PhysicalPosition position " +
                        "LEFT JOIN position.fixedPriceFxRiskFactor as fixedFxFactor " +
                        "LEFT JOIN position.dealPriceRiskFactor as priceIndexFactor " +
                        "LEFT JOIN priceIndexFactor.index as priceIndex " +
                        "LEFT JOIN position.dealPriceFxRiskFactor as priceIndexFxFactor " +
                        "     JOIN position.marketPriceRiskFactor as marketIndexFactor " +
                        "     JOIN marketIndexFactor.index as marketIndex " +
                        "lEFT JOIN position.marketPriceFxRiskFactor as marketIndexFxFactor " +
                        "    WHERE position.id in (:positionIds)  ")
})
public class PhysicalPosition extends DealPosition {


    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private FxRiskFactor fixedPriceFxRiskFactor;

    private PriceRiskFactor dealPriceRiskFactor;
    private FxRiskFactor dealPriceFxRiskFactor;

    private PriceRiskFactor marketPriceRiskFactor;
    private FxRiskFactor marketPriceFxRiskFactor;

    public PhysicalPosition() {
        super(DealTypeCode.PHYSICAL_DEAL.getCode());

    }

    @Embedded
    public PhysicalPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalPositionDetail detail) {
        this.detail = detail;
    }

    public void createWith(DealPositionSnapshot snapshot) {
        super.createWith(snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        detail.copyFrom(physicalPositionSnapshot.getDetail());
        setAssociations(physicalPositionSnapshot);
        save();
        postCreateWith(snapshot);
    }



    public void updateWith(DealPositionSnapshot snapshot) {
        super.updateWith(snapshot);
        PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) snapshot;
        detail.copyFrom(physicalPositionSnapshot.getDetail());
        setAssociations(physicalPositionSnapshot);
        postCreateWith(snapshot);
        update();
    }

    @Override
    public void valuePosition(LocalDateTime currentDateTime) {
        PhysicalPositionValuator valuator = new PhysicalPositionValuator(getPositionReport());
        PositionValuationResult result = valuator.valuePosition(currentDateTime);
        if (result.hasError() == false)
            getDealPositionDetail().setMarkToMarketValuation(result.getMtmValue());
        getDealPositionDetail().setErrorCode(result.getErrorCode());
        getDealPositionDetail().setCreateUpdateDateTime(currentDateTime);
    }

    private void setAssociations(PhysicalPositionSnapshot snapshot) {


        if (snapshot.getFixedPriceFxRiskFactorId() != null)
            this.fixedPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getFixedPriceFxRiskFactorId());

        if (snapshot.getDealPriceRiskFactorId() != null)
            this.dealPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getDealPriceRiskFactorId());

        if (snapshot.getDealPriceFxRiskFactorId() != null)
            this.dealPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getDealPriceFxRiskFactorId());

        if (snapshot.getMarketPriceRiskFactorId() != null)
            this.marketPriceRiskFactor = getPriceRiskFactorRepository().load(snapshot.getMarketPriceRiskFactorId());

        if (snapshot.getMarketPriceFxRiskFactorId() != null)
            this.marketPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getMarketPriceFxRiskFactorId());


    }

    @Transient
    public PhysicalPositionReport getPositionReport() {

        return new PhysicalPositionReport(
                getDeal().getId(),
                getDeal().getDealDetail().getBuySellCodeValue(),
                getId(),
                getDealPositionDetail().getVolumeQuantityValue(),
                getDealPositionDetail().getVolumeUnitOfMeasureValue(),
                getDealPositionDetail().getCurrencyCodeValue(),
                getDetail().getDealPriceValuationValue(),
                getDetail().getFixedPriceValue(),
                getDetail().getFixedPriceCurrencyCodeValue(),
                (dealPriceFxRiskFactor != null ? dealPriceFxRiskFactor.getDetail().getValue() : null),
                getDetail().getFixedPriceUnitOfMeasureCodeValue(),
                (dealPriceRiskFactor != null ? dealPriceRiskFactor.getDetail().getValue() : null),
                (dealPriceRiskFactor != null ? dealPriceRiskFactor.getIndex().getDetail().getCurrencyCodeValue() : null),
                (dealPriceRiskFactor != null ? dealPriceRiskFactor.getIndex().getDetail().getUnitOfMeasureCodeValue() : null),
                (dealPriceFxRiskFactor != null ? dealPriceFxRiskFactor.getDetail().getValue() : null),
                getDetail().getMarketPriceValuationValue(),
                marketPriceRiskFactor.getDetail().getValue(),
                marketPriceRiskFactor.getIndex().getDetail().getCurrencyCodeValue(),
                marketPriceRiskFactor.getIndex().getDetail().getUnitOfMeasureCodeValue(),
                (marketPriceFxRiskFactor != null ? marketPriceFxRiskFactor.getDetail().getValue() : null));
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FIXED_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getFixedPriceFxRiskFactor() {
        return fixedPriceFxRiskFactor;
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

    public void setFixedPriceFxRiskFactor(FxRiskFactor fixedPriceFxRiskFactor) {
        this.fixedPriceFxRiskFactor = fixedPriceFxRiskFactor;
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

    private static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}
