package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhysicalDealPositionGenerator implements DealPositionGenerator {

    private BaseDealSnapshot dealSnapshot;

    private RiskFactorManager riskFactorManager;

    private List<PositionHolder> positionHolders = new ArrayList<>();

    public static DealPositionGenerator newGenerator(
            BaseDealSnapshot dealSnapshot,
            RiskFactorManager riskFactorManager) {
        return new PhysicalDealPositionGenerator(
                dealSnapshot,
                riskFactorManager);
    }

    private PhysicalDealPositionGenerator(
            BaseDealSnapshot dealSnapshot,
            RiskFactorManager riskFactorManager) {
        this.dealSnapshot = dealSnapshot;
        this.riskFactorManager = riskFactorManager;
    }

    @Override
    public void generatePositionHolders(EvaluationContext context) {

        PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot)dealSnapshot;

        LocalDate currentDate = physicalDealSnapshot.getDealDetail().getStartDate();
        while (currentDate.isAfter(physicalDealSnapshot.getDealDetail().getEndDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(new PhysicalPositionSnapshot());
            PhysicalPositionSnapshot position = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();
            position.getDealPositionDetail().setFrequencyCode(FrequencyCode.DAILY);

            position.getDealPositionDetail().setVolumeQuantity(physicalDealSnapshot.getDealDetail().getVolumeQuantity());
            position.getDealPositionDetail().setVolumeUnitOfMeasure(physicalDealSnapshot.getDealDetail().getVolumeUnitOfMeasure());

            position.getDealPositionDetail().setStartDate(currentDate);
            position.getDealPositionDetail().setEndDate(currentDate);
            position.getDealPositionDetail().setCreateUpdateDateTime(context.getObservedDateTime());

            position.getDealPositionDetail().setCurrencyCode(context.getCurrencyCode());

            // Deal Price
            position.getDetail().setDealPriceValuationCode(physicalDealSnapshot.getDetail().getDealPriceValuationCode());

            // if deal price valuation is fixed or INDEX Plus
            if (position.getDetail().getDealPriceValuationCode() != ValuationCode.INDEX) {
                position.getDetail().setDealPrice(physicalDealSnapshot.getDetail().getDealPriceValue());

                if (physicalDealSnapshot.getDetail().getDealPriceCurrency() != context.getCurrencyCode()) {
                    physicalPositionHolder.setDealPriceFxRiskFactorHolder(
                            riskFactorManager.determineFxRiskFactor(
                                    physicalDealSnapshot.getDetail().getDealPriceCurrency(),
                                    context.getCurrencyCode(),
                                    currentDate));
                }
            } else {
                physicalPositionHolder.setDealPriceRiskFactorHolder(
                        riskFactorManager.determinePriceRiskFactor(
                                physicalDealSnapshot.getDealPriceIndexId().getCode(),
                                currentDate)
                );

            }

            // Market
            position.getDetail().setDealMarketValuationCode(physicalDealSnapshot.getDetail().getMarketValuationCode());
            physicalPositionHolder.setMarketRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            physicalDealSnapshot.getMarketPriceIndexId().getCode(),
                            currentDate)
            );
            if (physicalDealSnapshot.getMarketCurrencyCode() != context.getCurrencyCode()) {
                physicalPositionHolder.setMarketFxRiskFactorHolder(
                        riskFactorManager.determineFxRiskFactor(
                                physicalDealSnapshot.getMarketCurrencyCode(),
                                context.getCurrencyCode(),
                                currentDate));

            }
            positionHolders.add(physicalPositionHolder);
            currentDate = currentDate.plusDays(1);
        }
    }

    public List<DealPositionSnapshot> generateDealPositionSnapshots() {
        List<DealPositionSnapshot> positions = new ArrayList<>();

        for (PositionHolder holder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) holder;

            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) holder.getDealPositionSnapshot();

            positionSnapshot.setMarketPriceRiskFactorId(physicalPositionHolder.getMarketRiskFactorHolder().getRiskFactor().getEntityId());
            if (physicalPositionHolder.getMarketFxRiskFactorHolder() != null) {
                positionSnapshot.setMarketFxRiskFactorId(physicalPositionHolder.getMarketFxRiskFactorHolder().getRiskFactor().getFxIndexId());
            }

            if (physicalPositionHolder.getDealPriceRiskFactorHolder() != null) {
                positionSnapshot.setDealPriceRiskFactorId(physicalPositionHolder.getDealPriceRiskFactorHolder().getRiskFactor().getEntityId());
            }

            if (physicalPositionHolder.getDealPriceFxRiskFactorHolder() != null) {
                positionSnapshot.setDealPriceFxRiskFactorId(physicalPositionHolder.getDealPriceFxRiskFactorHolder().getRiskFactor().getEntityId());
            }
            positions.add(positionSnapshot);
        }
        return positions;
    }

    @Override
    public BaseDealSnapshot getDealSnapshot() {
        return dealSnapshot;
    }

    @Override
    public List<PositionHolder> getPositionHolders() {
        return positionHolders;
    }
}
