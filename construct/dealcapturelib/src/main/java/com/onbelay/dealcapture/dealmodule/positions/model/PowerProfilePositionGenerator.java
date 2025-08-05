package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceIndexPositionDateContainer;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PowerProfilePositionGenerator implements ProfilePositionGenerator {

    private PowerProfileSnapshot powerProfile;

    protected RiskFactorManager riskFactorManager;

    private List<PowerProfilePositionHolder> positionHolders = new ArrayList<>();

    private List<PowerProfilePositionHolder> basisHolders = new ArrayList<>();


    public PowerProfilePositionGenerator(PowerProfileSnapshot powerProfile, RiskFactorManager riskFactorManager) {
        this.powerProfile = powerProfile;
        this.riskFactorManager = riskFactorManager;
    }

    @Override
    public void generatePositionHolders(EvaluationContext context) {

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

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
                    holder.getSnapshot().getDetail().setDefaults();
                    holder.getSnapshot().getDetail().setStartDate(currentDate);
                    holder.getSnapshot().getDetail().setEndDate(currentDate);
                    holder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
                    holder.getSnapshot().setPriceIndexId(powerProfile.getSettledPriceIndexId());

                    holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                    holder.getSnapshot().getDetail().setErrorCode("0");
                    holder.getSnapshot().getDetail().setPowerFlowCode(PowerFlowCode.SETTLED);
                    determineHourlySettledRiskFactors(holder);
                    PriceIndexPositionDateContainer container = riskFactorManager.findPriceIndexContainer(holder.getSnapshot().getPriceIndexId().getId());
                    if (container.isBasis())
                        determineBasisPositions(
                                container,
                                holder);

                    positionHolders.add(holder);
                } else {
                    if (currentDate.isBefore(endOfMonthDate) && powerProfile.getRestOfMonthPriceIndexId() != null) {
                            PowerProfilePositionHolder holder = new PowerProfilePositionHolder();
                            holder.getSnapshot().getDetail().setDefaults();
                            holder.getSnapshot().getDetail().setStartDate(currentDate);
                            holder.getSnapshot().getDetail().setEndDate(currentDate);
                            holder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
                            holder.getSnapshot().setPriceIndexId(powerProfile.getRestOfMonthPriceIndexId());

                            holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());
                            holder.getSnapshot().getDetail().setErrorCode("0");
                            holder.getSnapshot().getDetail().setPowerFlowCode(PowerFlowCode.END_OF_MTH);
                            determineHourlySettledRiskFactors(holder);
                            PriceIndexPositionDateContainer container = riskFactorManager.findPriceIndexContainer(holder.getSnapshot().getPriceIndexId().getId());
                            if (container.isBasis())
                                determineBasisPositions(
                                        container,
                                        holder);

                            positionHolders.add(holder);
                    } else {

                        HashSet<PowerFlowCode> codes = new HashSet<>();
                        for (int i = 1; i < 25; i++) {
                            if (profileDaySnapshot.getDetail().getPowerFlowCode(i) != PowerFlowCode.NONE)
                                codes.add(profileDaySnapshot.getDetail().getPowerFlowCode(i));
                        }

                        for (PowerFlowCode code : codes) {
                            EntityId priceIndexId = powerProfile.findPriceIndexMappingByFlowCode(code);
                            PriceIndexSnapshot priceIndex = riskFactorManager.findPriceIndex(priceIndexId.getId());

                            PowerProfilePositionHolder holder = new PowerProfilePositionHolder();
                            holder.getSnapshot().getDetail().setDefaults();
                            holder.getSnapshot().setPriceIndexId(new EntityId(priceIndexId));
                            holder.getSnapshot().getDetail().setPowerFlowCode(code);
                            holder.getSnapshot().getDetail().setStartDate(currentDate);
                            holder.getSnapshot().getDetail().setEndDate(currentDate);
                            holder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
                            holder.getSnapshot().getDetail().setErrorCode("0");

                            holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());

                            if (priceIndex.getDetail().getFrequencyCode() != FrequencyCode.HOURLY) {
                                holder.setPriceRiskFactorHolder(riskFactorManager.determinePriceRiskFactor(
                                        priceIndexId.getId(),
                                        currentDate));
                            }

                            int totalHours = 0;
                            for (int i = 1; i < 25; i++) {

                                if (profileDaySnapshot.getDetail().getPowerFlowCode(i) == code) {
                                    totalHours++;
                                    if (priceIndex.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
                                        PriceRiskFactorHolder priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                                                priceIndexId.getId(),
                                                currentDate,
                                                i);
                                        holder.getHourHolderMap().setHourPriceHolder(i, priceRiskFactorHolder);
                                    }

                                }
                            }
                            holder.getSnapshot().getDetail().setNumberOfHours(totalHours);

                            PriceIndexPositionDateContainer container = riskFactorManager.findPriceIndexContainer(holder.getSnapshot().getPriceIndexId().getId());
                            if (container.isBasis())
                                determineBasisPositions(
                                        container,
                                        holder);

                            positionHolders.add(holder);
                        }
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }


    }

    private void determineBasisPositions(
            PriceIndexPositionDateContainer container,
            PowerProfilePositionHolder holder) {

        holder.getSnapshot().getDetail().setIndexTypeCode(IndexType.BASIS);
        holder.getSnapshot().getDetail().setBasisNo(1);

        int basisNo = 1;
        PriceIndexPositionDateContainer basisContainer = container.getBasisToHubContainer();
        while (basisContainer != null) {
            basisNo++;
            PriceIndexSnapshot priceIndex = basisContainer.getPriceIndex();
            PowerProfilePositionHolder basisPositionHolder = new PowerProfilePositionHolder();
            basisPositionHolder.getSnapshot().getDetail().setDefaults();
            basisPositionHolder.getSnapshot().setPriceIndexId(priceIndex.getEntityId());
            basisPositionHolder.getSnapshot().getDetail().setPowerFlowCode(holder.getSnapshot().getDetail().getPowerFlowCode());
            basisPositionHolder.getSnapshot().getDetail().setStartDate(holder.getSnapshot().getDetail().getStartDate());
            basisPositionHolder.getSnapshot().getDetail().setEndDate(holder.getSnapshot().getDetail().getEndDate());
            basisPositionHolder.getSnapshot().getDetail().setEndDate(holder.getSnapshot().getDetail().getEndDate());
            basisPositionHolder.getSnapshot().getDetail().setNumberOfHours(holder.getSnapshot().getDetail().getNumberOfHours());

            if (basisContainer.isBasis())
                basisPositionHolder.getSnapshot().getDetail().setIndexTypeCode(IndexType.BASIS);
            else
                basisPositionHolder.getSnapshot().getDetail().setIndexTypeCode(IndexType.HUB);
            basisPositionHolder.getSnapshot().getDetail().setBasisNo(basisNo);

            basisPositionHolder.getSnapshot().setPowerProfileId(powerProfile.getEntityId());
            basisPositionHolder.getSnapshot().getDetail().setErrorCode("0");

            basisPositionHolder.getSnapshot().getDetail().setCreatedDateTime(holder.getSnapshot().getDetail().getCreatedDateTime());
            if (priceIndex.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
                for (int i = 1; i < 25; i++) {
                    if (holder.getHourHolderMap().getHourPriceHolder(i) != null) {
                        PriceRiskFactorHolder currentHolder = holder.getHourHolderMap().getHourPriceHolder(i);

                        PriceRiskFactorHolder nextPriceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                                basisContainer,
                                currentHolder.getMarketDate(),
                                i);
                        basisPositionHolder.getHourHolderMap().setHourPriceHolder(i, nextPriceRiskFactorHolder);
                    }
                }
            } else {
                basisPositionHolder.setPriceRiskFactorHolder(
                        riskFactorManager.determinePriceRiskFactor(
                            basisContainer,
                            holder.getSnapshot().getDetail().getStartDate()));
            }
            basisHolders.add(basisPositionHolder);
            basisContainer = basisContainer.getBasisToHubContainer();
        }

    }

    private void determineHourlySettledRiskFactors(PowerProfilePositionHolder holder) {
        int dayOfWeek = holder.getSnapshot().getDetail().getStartDate().getDayOfWeek().getValue();
        PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);

        PriceIndexSnapshot priceIndex = riskFactorManager.findPriceIndex(holder.getSnapshot().getPriceIndexId().getId());

        if (priceIndex.getDetail().getFrequencyCode() != FrequencyCode.HOURLY) {
            holder.setPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            priceIndex.getEntityId().getId(),
                            holder.getSnapshot().getDetail().getStartDate()));
        }

        int totalHours = 0;
        for (int i =1; i < 25; i++) {
            if (profileDaySnapshot.getDetail().getPowerFlowCode(i)!= PowerFlowCode.NONE) {
                totalHours++;
                if (priceIndex.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
                    PriceRiskFactorHolder priceHolder = riskFactorManager.determinePriceRiskFactor(
                            holder.getSnapshot().getPriceIndexId().getId(),
                            holder.getSnapshot().getDetail().getStartDate(),
                            i);

                    holder.getHourHolderMap().setHourPriceHolder(
                            i,
                            priceHolder);
                }

            }
        }
        holder.getSnapshot().getDetail().setNumberOfHours(totalHours);
    }

    @Override
    public Collection<? extends PowerProfilePositionSnapshot> generatePowerProfilePositionSnapshots() {
        ArrayList<PowerProfilePositionSnapshot> snapshots = new ArrayList<>();
        for (PowerProfilePositionHolder holder : positionHolders) {

            snapshots.add(
                    createAndSetSnapshot(holder));

        }
        for (PowerProfilePositionHolder holder : basisHolders) {

            snapshots.add(
                    createAndSetSnapshot(holder));

        }
        return snapshots;
    }

    private PowerProfilePositionSnapshot createAndSetSnapshot(PowerProfilePositionHolder holder) {
        PowerProfilePositionSnapshot snapshot = holder.getSnapshot();

        int dayOfWeek = holder.getSnapshot().getDetail().getStartDate().getDayOfWeek().getValue();
        PowerProfileDaySnapshot profileDaySnapshot = powerProfile.getPowerProfileDayByDayOfWeek(dayOfWeek);

        if (holder.getPriceRiskFactorHolder() != null) {
            PriceRiskFactorHolder priceHolder = holder.getPriceRiskFactorHolder();
            for (int i=1; i < 25; i++ ) {

                if (profileDaySnapshot.getDetail().getPowerFlowCode(i) != PowerFlowCode.NONE) {
                    PowerFlowCode powerFlowCode = profileDaySnapshot.getDetail().getPowerFlowCode(i);

                    if (snapshot.getDetail().getPowerFlowCode() == PowerFlowCode.SETTLED
                            || snapshot.getDetail().getPowerFlowCode() == PowerFlowCode.END_OF_MTH) {
                        snapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                                i,
                                priceHolder.getRiskFactor().getEntityId().getId());
                    } else if (snapshot.getDetail().getPowerFlowCode() == powerFlowCode) {
                        snapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                                i,
                                priceHolder.getRiskFactor().getEntityId().getId());
                    }
                }
            }


        } else {

            for (int i = 1; i < 25; i++) {
                PriceRiskFactorHolder priceHolder = holder.getHourHolderMap().getHourPriceHolder(i);
                if (priceHolder != null) {
                    snapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                            i,
                            priceHolder.getRiskFactor().getEntityId().getId());
                }
            }
        }
        return snapshot;
    }

    public PowerProfileSnapshot getPowerProfile() {
        return powerProfile;
    }

    public List<PowerProfilePositionHolder> getPositionHolders() {
        return positionHolders;
    }
}
