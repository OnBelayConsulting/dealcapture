package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.shared.enums.FrequencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class VanillaOptionDealPositionGenerator extends BaseDealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    public static DealPositionGenerator newGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {

        return new VanillaOptionDealPositionGenerator(
                dealSummary,
                riskFactorManager);
    }

    private VanillaOptionDealPositionGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
        super(
                dealSummary,
                riskFactorManager);
    }

    @Override
    public void generatePositionHolders() {
        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(getVanillaOptionDealSummary().getUnderlyingPriceIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.DAILY)
            generatePositionHoldersDaily();
        else
            generatePositionHoldersMonthly();
   }

    public VanillaOptionDealSummary getVanillaOptionDealSummary() {
        return (VanillaOptionDealSummary) dealSummary;
    }


    public void generatePositionHoldersDaily() {

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            VanillaOptionPositionHolder vanillaOptionPositionHolder = new VanillaOptionPositionHolder(getVanillaOptionDealSummary());

            setBasePositionHolderAttributes(
                    vanillaOptionPositionHolder,
                    currentDate);
            vanillaOptionPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.DAILY);
            determinePositionQuantity(vanillaOptionPositionHolder);

            vanillaOptionPositionHolder.setOptionExpiryDate(currentDate);

            generateCostPositionHolders(vanillaOptionPositionHolder);

            determineUnderlyingPriceRiskFactors(vanillaOptionPositionHolder);

            positionHolders.add(vanillaOptionPositionHolder);
            currentDate = currentDate.plusDays(1);
        }
    }


    public void generatePositionHoldersMonthly() {

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            VanillaOptionPositionHolder vanillaOptionPositionHolder = new VanillaOptionPositionHolder(getVanillaOptionDealSummary());

            setBasePositionHolderAttributes(
                    vanillaOptionPositionHolder,
                    currentDate);
            vanillaOptionPositionHolder.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);

            if (getVanillaOptionDealSummary().getDetail().getOptionExpiryDateRuleToken() == OptionExpiryDateRuleToken.POSITION_START_DATE) {
                vanillaOptionPositionHolder.getDetail().setEndDate(currentDate);
                vanillaOptionPositionHolder.setOptionExpiryDate(currentDate);
            } else {
                LocalDate endOfMonth = currentDate.plusMonths(1).minusDays(1);
                vanillaOptionPositionHolder.getDetail().setEndDate(endOfMonth);
                vanillaOptionPositionHolder.setOptionExpiryDate(endOfMonth);
            }

            vanillaOptionPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.MONTHLY);
            determinePositionQuantity(vanillaOptionPositionHolder);

            generateCostPositionHolders(vanillaOptionPositionHolder);

            determineUnderlyingPriceRiskFactors(vanillaOptionPositionHolder);

            positionHolders.add(vanillaOptionPositionHolder);
            currentDate = currentDate.plusMonths(1);
        }
    }



    private void determineUnderlyingPriceRiskFactors(VanillaOptionPositionHolder vanillaOptionPositionHolder) {


        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(getVanillaOptionDealSummary().getUnderlyingPriceIndexId());

        vanillaOptionPositionHolder.setUnderlyingPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            getVanillaOptionDealSummary().getUnderlyingPriceIndexId(),
                            vanillaOptionPositionHolder.getDetail().getStartDate()));

        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            vanillaOptionPositionHolder.setUnderlyingFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            vanillaOptionPositionHolder.getDetail().getStartDate()));
        }

    }

    @Override
    public PositionGenerationResult generatePositionSnapshots() {

        PositionGenerationResult generationResult = new PositionGenerationResult();

        for (BasePositionHolder positionHolder : positionHolders) {
            VanillaOptionPositionHolder vanillaOptionPositionHolder = (VanillaOptionPositionHolder) positionHolder;
            VanillaOptionPositionSnapshot positionSnapshot = new VanillaOptionPositionSnapshot();

            positionSnapshot.setDealId(new EntityId(positionHolder.getDealSummary().getId()));
            positionSnapshot.getPositionDetail().copyFrom(positionHolder.getDetail());
            positionSnapshot.getSettlementDetail().copyFrom(positionHolder.getSettlementDetail());
            positionSnapshot.getPositionDetail().setErrorCode("0");

            positionSnapshot.getPriceDetail().setOptionExpiryDate(vanillaOptionPositionHolder.getOptionExpiryDate());


            // Underlying
            if (vanillaOptionPositionHolder.getUnderlyingPriceRiskFactorHolder() != null)
                positionSnapshot.setUnderlyingPriceRiskFactorId(vanillaOptionPositionHolder.getUnderlyingPriceRiskFactorHolder().getRiskFactor().getEntityId());

            if (vanillaOptionPositionHolder.getUnderlyingFxHolder() != null) {
                positionSnapshot.setUnderlyingFxRiskFactorId(
                        vanillaOptionPositionHolder.getUnderlyingFxHolder().getRiskFactor().getEntityId());
            }

            generationResult.addDealPositionSnapshot(positionSnapshot);
        }

        generationResult.setCostPositionSnapshots(generateCostPositionSnapshots());

        return generationResult;
    }
}
