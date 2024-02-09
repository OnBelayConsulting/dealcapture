package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.shared.enums.BuySellCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("PHY")
public class PhysicalPosition extends DealPosition {


    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private Integer fixedPriceFxRiskFactorId;

    private Integer dealPriceRiskFactorId;
    private Integer dealPriceFxRiskFactorId;

    private Integer marketPriceRiskFactorId;
    private Integer marketPriceFxRiskFactorId;

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
        Price dealPrice =  switch (detail.getDealPriceValuationCode()) {

            case FIXED -> getDealPrice();

            case INDEX -> getDealIndexPrice();

            case INDEX_PLUS -> getDealIndexPrice().add(getDealPrice());

            default -> new Price(CalculatedErrorType.ERROR);
        };

        Price marketPrice = getMarketIndexPrice();
        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();
            Price netPrice;
            if (getDeal().getDealDetail().getBuySell() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(getDealPositionDetail().getQuantity());

            if (amount.isInError()) {
                getDealPositionDetail().setErrorCode(amount.getError().getCode());
            } else {
                getDealPositionDetail().setMarkToMarketValuation(amount.getValue());
                getDealPositionDetail().setErrorCode("SUCCESS");
            }
        } else {
            getDealPositionDetail().setErrorCode(PositionErrorCode.ERROR_VALUE_POSITION_MISSING_PRICES.getCode());
        }
        getDealPositionDetail().setCreateUpdateDateTime(currentDateTime);
    }

    @Transient
    public Price getMarketIndexPrice() {

        Price price = getMarketPriceRiskFactor().fetchCurrentPrice();
        price = price.multiply(detail.getMarketPriceUOMConversion());

        if (marketPriceFxRiskFactorId != null) {
            price =  price.apply(
                    getMarketPriceFxRiskFactor().fetchFxRate(
                            getDealPositionDetail().getCurrencyCode(),
                            price.getCurrency()));
        }
        List<PositionRiskFactorMappingSummary> summaries = findMappingSummaries(PriceTypeCode.MARKET_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                price = price.add(summary.calculateConvertedPrice(
                        price.getCurrency(),
                        price.getUnitOfMeasure()));
            }
        }
        return price;
    }

    @Transient
    public Price getDealIndexPrice() {
        Price price = getDealPriceRiskFactor().fetchCurrentPrice();
        price = price.multiply(detail.getDealPriceUOMConversion());

        if (dealPriceFxRiskFactorId != null) {
            price = price.multiply(this.getDealPriceFxRiskFactor().getDetail().getValue());
        }

        List<PositionRiskFactorMappingSummary> summaries = findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                price = price.add(summary.calculateConvertedPrice(
                        price.getCurrency(),
                        price.getUnitOfMeasure()));
            }
        }

        return price;
    }

    @Transient
    public Price getDealPrice() {
        Price dealPrice = detail.getDealPrice();
        if (getDealPositionDetail().getCurrencyCode() != dealPrice.getCurrency()) {
            dealPrice = dealPrice.apply(
                    getFixedPriceFxRiskFactor()
                            .fetchFxRate(
                                    getDealPositionDetail().getCurrencyCode(),
                                    dealPrice.getCurrency()));
        }

        if (getDealPositionDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            dealPrice = dealPrice.multiply(detail.getDealPriceUOMConversion());
        }
        return dealPrice;
    }

    private void setAssociations(PhysicalPositionSnapshot snapshot) {


        if (snapshot.getFixedPriceFxRiskFactorId() != null)
            this.fixedPriceFxRiskFactorId = snapshot.getFixedPriceFxRiskFactorId().getId();

        if (snapshot.getDealPriceRiskFactorId() != null)
            this.dealPriceRiskFactorId = snapshot.getDealPriceRiskFactorId().getId();

        if (snapshot.getDealPriceFxRiskFactorId() != null)
            this.dealPriceFxRiskFactorId = snapshot.getDealPriceFxRiskFactorId().getId();

        if (snapshot.getMarketPriceRiskFactorId() != null)
            this.marketPriceRiskFactorId = snapshot.getMarketPriceRiskFactorId().getId();

        if (snapshot.getMarketPriceFxRiskFactorId() != null)
            this.marketPriceFxRiskFactorId = snapshot.getMarketPriceFxRiskFactorId().getId();


    }

    @Transient
    public FxRiskFactor getFixedPriceFxRiskFactor() {
        return getFxRiskFactorRepository().load(new EntityId(fixedPriceFxRiskFactorId));
    }

    @Column(name = "FIXED_PRICE_FX_RISK_FACTOR_ID")
    public Integer getFixedPriceFxRiskFactorId() {
        return fixedPriceFxRiskFactorId;
    }

    public void setFixedPriceFxRiskFactorId(Integer fixedDealPriceFxRiskFactorId) {
        this.fixedPriceFxRiskFactorId = fixedDealPriceFxRiskFactorId;
    }

    @Transient
    public PriceRiskFactor getDealPriceRiskFactor() {
        return getPriceRiskFactorRepository().load(new EntityId(dealPriceRiskFactorId));
    }

    @Column(name = "DEAL_PRICE_RISK_FACTOR_ID")
    public Integer getDealPriceRiskFactorId() {
        return dealPriceRiskFactorId;
    }

    public void setDealPriceRiskFactorId(Integer dealPriceRiskFactorId) {
        this.dealPriceRiskFactorId = dealPriceRiskFactorId;
    }

    @Transient
    public FxRiskFactor getDealPriceFxRiskFactor() {
        return getFxRiskFactorRepository().load(new EntityId(dealPriceFxRiskFactorId));
    }
    @Column(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
    public Integer getDealPriceFxRiskFactorId() {
        return dealPriceFxRiskFactorId;
    }

    public void setDealPriceFxRiskFactorId(Integer dealPriceFxRiskFactorId) {
        this.dealPriceFxRiskFactorId = dealPriceFxRiskFactorId;
    }

    @Transient
    public PriceRiskFactor getMarketPriceRiskFactor() {
        return getPriceRiskFactorRepository().load(new EntityId(marketPriceRiskFactorId));
    }

    @Column(name = "MKT_PRICE_RISK_FACTOR_ID")
    public Integer getMarketPriceRiskFactorId() {
        return marketPriceRiskFactorId;
    }

    public void setMarketPriceRiskFactorId(Integer marketPriceRiskFactorId) {
        this.marketPriceRiskFactorId = marketPriceRiskFactorId;
    }

    @Transient
    public FxRiskFactor getMarketPriceFxRiskFactor() {
        return getFxRiskFactorRepository().load(new EntityId(marketPriceFxRiskFactorId));
    }

    @Column(name = "MKT_PRICE_FX_RISK_FACTOR_ID")
    public Integer getMarketPriceFxRiskFactorId() {
        return marketPriceFxRiskFactorId;
    }

    public void setMarketPriceFxRiskFactorId(Integer marketPriceFxRiskFactorId) {
        this.marketPriceFxRiskFactorId = marketPriceFxRiskFactorId;
    }

    private static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

    private static PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

}
