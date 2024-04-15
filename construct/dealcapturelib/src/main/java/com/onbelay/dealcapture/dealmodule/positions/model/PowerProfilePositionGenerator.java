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

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            if (currentDate.isAfter(todayDate) == false) {

                PowerProfilePositionHolder holder = new PowerProfilePositionHolder();
                holder.getSnapshot().getDetail().setPowerFlowCode(PowerFlowCode.SETTLED);
                holder.getSnapshot().getDetail().setStartDate(currentDate);
                holder.getSnapshot().getDetail().setEndDate(currentDate);

                holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                holder.getSnapshot().getDetail().setCurrencyCode(context.getCurrencyCode());
                determineHourlySettledRiskFactors(holder);

                positionHolders.add(holder);
            } else {
                int dayOfWeek = currentDate.getDayOfWeek().getValue();
                PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);

                HashSet<PowerFlowCode> codes = new HashSet<>();
                for (int i =1; i < 25; i++) {

                    codes.add(profileDaySnapshot.getDetail().getPowerFlowCode(i));
                }

                for (PowerFlowCode code : codes) {
                    EntityId priceIndexId = powerProfile.findPriceIndexMappingByFlowCode(code);
                    PowerProfilePositionHolder holder = new PowerProfilePositionHolder();



                    holder.getSnapshot().getDetail().setPowerFlowCode(code);
                    holder.getSnapshot().getDetail().setStartDate(currentDate);
                    holder.getSnapshot().getDetail().setEndDate(currentDate);

                    holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                    holder.getSnapshot().getDetail().setCurrencyCode(context.getCurrencyCode());
                    holder.setPriceRiskFactorHolder(
                            riskFactorManager.determinePriceRiskFactor(
                                priceIndexId.getId(),
                                currentDate));

                    int totalHours = 0;
                    for (int i =1; i < 25; i++) {

                        if (profileDaySnapshot.getDetail().getPowerFlowCode(i) == code) {
                            totalHours++;
                            holder.getHourHolderMap().setHourPriceHolder(i, holder.getPriceRiskFactorHolder());
                        }
                    }
                    holder.getSnapshot().getDetail().setNumberOfHours(totalHours);

                    positionHolders.add(holder);
                    }

            }
        }


    }

    private void determineHourlySettledRiskFactors(PowerProfilePositionHolder holder) {
        int dayOfWeek = holder.getSnapshot().getDetail().getStartDate().getDayOfWeek().getValue();
        PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);
        for (int i =1; i < 25; i++) {
            if (profileDaySnapshot.getDetail().getPowerFlowCode(i)!= null) {

                PriceRiskFactorHolder priceHolder = riskFactorManager.determinePriceRiskFactor(
                        powerProfile.getSettledPriceIndexId().getId(),
                        holder.getSnapshot().getDetail().getStartDate(),
                        i);

                holder.getHourHolderMap().setHourPriceHolder(
                        i,
                        priceHolder);

            }
        }
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

            if (holder.getPriceRiskFactorHolder() != null) {
                Integer priceRiskFactorId = holder.getPriceRiskFactorHolder().getRiskFactor().getEntityId().getId();
                snapshot.setPriceRiskFactorId(new EntityId(priceRiskFactorId) );
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
