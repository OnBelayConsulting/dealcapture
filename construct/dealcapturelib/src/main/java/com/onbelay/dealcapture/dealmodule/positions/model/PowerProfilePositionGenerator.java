package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PowerProfilePositionGenerator implements ProfilePositionGenerator {

    private PowerProfileSnapshot powerProfile;

    protected RiskFactorManager riskFactorManager;

    private List<PowerProfilePositionHolder> positionHolders = new ArrayList<>();

    public PowerProfilePositionGenerator(PowerProfileSnapshot powerProfile, RiskFactorManager riskFactorManager) {
        this.powerProfile = powerProfile;
        this.riskFactorManager = riskFactorManager;
    }

    @Override
    public void generatePositionHolders(EvaluationContext context) {

        LocalDate todayDate = context.getCreatedDateTime().toLocalDate();
        LocalDate endOfMonthDate = todayDate.plusMonths(1);
        endOfMonthDate = endOfMonthDate.withDayOfMonth(1);

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();
            PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);
            if (profileDaySnapshot != null) {

                if (currentDate.isAfter(todayDate) == false) {
                    PowerProfilePositionHolder holder = new PowerProfilePositionHolder();
                    holder.getSnapshot().getDetail().setStartDate(currentDate);
                    holder.getSnapshot().getDetail().setEndDate(currentDate);
                    holder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
                    holder.getSnapshot().setPriceIndexId(powerProfile.getSettledPriceIndexId());

                    holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                    holder.getSnapshot().getDetail().setCurrencyCode(context.getCurrencyCode());
                    holder.getSnapshot().getDetail().setErrorCode("0");
                    holder.getSnapshot().getDetail().setPowerFlowCode(PowerFlowCode.SETTLED);
                    determineHourlySettledRiskFactors(holder);

                    positionHolders.add(holder);
                } else {

                    HashSet<PowerFlowCode> codes = new HashSet<>();
                    for (int i = 1; i < 25; i++) {
                        if (profileDaySnapshot.getDetail().getPowerFlowCode(i) != null)
                            codes.add(profileDaySnapshot.getDetail().getPowerFlowCode(i));
                    }

                    for (PowerFlowCode code : codes) {
                        EntityId priceIndexId = null;
                        if (currentDate.isBefore(endOfMonthDate)) {
                            priceIndexId = powerProfile.findPriceIndexMappingByFlowCode(PowerFlowCode.HOURLY);
                        }
                        if (priceIndexId == null)
                            priceIndexId = powerProfile.findPriceIndexMappingByFlowCode(code);
                        PowerProfilePositionHolder holder = new PowerProfilePositionHolder();
                        holder.getSnapshot().setPriceIndexId(new EntityId(priceIndexId));
                        holder.getSnapshot().getDetail().setPowerFlowCode(code);
                        holder.getSnapshot().getDetail().setStartDate(currentDate);
                        holder.getSnapshot().getDetail().setEndDate(currentDate);
                        holder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
                        holder.getSnapshot().getDetail().setErrorCode("0");

                        holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                        holder.getSnapshot().getDetail().setCurrencyCode(context.getCurrencyCode());

                        int totalHours = 0;
                        for (int i = 1; i < 25; i++) {

                            if (profileDaySnapshot.getDetail().getPowerFlowCode(i) == code) {
                                totalHours++;
                                PriceRiskFactorHolder priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                                        priceIndexId.getId(),
                                        currentDate);

                                holder.getHourHolderMap().setHourPriceHolder(i, priceRiskFactorHolder);
                            }
                        }
                        holder.getSnapshot().getDetail().setNumberOfHours(totalHours);

                        positionHolders.add(holder);
                    }

                }
            }
            currentDate = currentDate.plusDays(1);
        }


    }

    private void determineHourlySettledRiskFactors(PowerProfilePositionHolder holder) {
        int dayOfWeek = holder.getSnapshot().getDetail().getStartDate().getDayOfWeek().getValue();
        PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);
        int totalHours = 0;
        for (int i =1; i < 25; i++) {
            if (profileDaySnapshot.getDetail().getPowerFlowCode(i)!= null) {
                totalHours++;
                PriceRiskFactorHolder priceHolder = riskFactorManager.determinePriceRiskFactor(
                        powerProfile.getSettledPriceIndexId().getId(),
                        holder.getSnapshot().getDetail().getStartDate(),
                        i);

                holder.getHourHolderMap().setHourPriceHolder(
                        i,
                        priceHolder);

            }
        }
        holder.getSnapshot().getDetail().setNumberOfHours(totalHours);
    }

    @Override
    public Collection<? extends PowerProfilePositionSnapshot> generatePowerProfilePositionSnapshots() {
        ArrayList<PowerProfilePositionSnapshot> snapshots = new ArrayList<>();
        for (PowerProfilePositionHolder holder : positionHolders) {

            PowerProfilePositionSnapshot snapshot = holder.getSnapshot();

            for (int i=1; i < 25; i++ ) {
                PriceRiskFactorHolder priceHolder = holder.getHourHolderMap().getHourPriceHolder(i);
                if (priceHolder != null) {
                    snapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                            i,
                            priceHolder.getRiskFactor().getEntityId().getId());
                }
            }

            snapshots.add(snapshot);

        }
        return snapshots;
    }

    public PowerProfileSnapshot getPowerProfile() {
        return powerProfile;
    }

    public List<PowerProfilePositionHolder> getPositionHolders() {
        return positionHolders;
    }
}
