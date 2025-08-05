package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;

import java.time.DayOfWeek;

public class PowerProfileFixture {


    public static PowerProfile createPowerProfileAllDaysAllHours(
                String name,
                PriceIndex hourlyIndex) {

            PowerProfileSnapshot powerProfileSnapshot = createPowerProfileSnapshotAllDaysAllHours(
                    name,
                    hourlyIndex,
                    hourlyIndex);
            powerProfileSnapshot.setSettledPriceIndexId(hourlyIndex.generateEntityId());

            return PowerProfile.create(powerProfileSnapshot);
    }


    public static PowerProfile createPowerProfileAllDaysAllHours(
            String name,
            PriceIndex settledIndex,
            PriceIndex offPeakIndex,
            PriceIndex onPeakIndex) {

        PowerProfileSnapshot powerProfileSnapshot = createPowerProfileSnapshotAllDaysAllHours(
                name,
                offPeakIndex,
                onPeakIndex);
        powerProfileSnapshot.setSettledPriceIndexId(settledIndex.generateEntityId());


        return PowerProfile.create(powerProfileSnapshot);
    }


    public static PowerProfile createPowerProfileWeekDaysSomeHours(
            String name,
            PriceIndex settledIndex,
            PriceIndex offPeakIndex,
            PriceIndex onPeakIndex) {

        PowerProfileSnapshot powerProfileSnapshot = createPowerProfileSnapshotWeekDaysSomeHours(name);
        powerProfileSnapshot.setSettledPriceIndexId(settledIndex.generateEntityId());

        // settled
        PowerProfileIndexMappingSnapshot indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(offPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.OFF_PEAK);
        powerProfileSnapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(onPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.ON_PEAK);
        powerProfileSnapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        return PowerProfile.create(powerProfileSnapshot);
    }


    public static PowerProfile createPowerProfileWeekDaysSomeHours(
            String name,
            PriceIndex settledIndex,
            PriceIndex hourlyForwardIndex,
            PriceIndex offPeakIndex,
            PriceIndex onPeakIndex) {

        PowerProfileSnapshot powerProfileSnapshot = createPowerProfileSnapshotWeekDaysSomeHours(name);
        powerProfileSnapshot.setSettledPriceIndexId(settledIndex.generateEntityId());
        powerProfileSnapshot.setRestOfMonthPriceIndexId(hourlyForwardIndex.generateEntityId());

        PowerProfileIndexMappingSnapshot indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(offPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.OFF_PEAK);
        powerProfileSnapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(onPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.ON_PEAK);
        powerProfileSnapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        return PowerProfile.create(powerProfileSnapshot);
    }



    public static PowerProfileSnapshot createPowerProfileSnapshotAllDaysAllHours(
            String name,
            PriceIndex offPeakIndex,
            PriceIndex onPeakIndex
            ) {
        PowerProfileSnapshot snapshot = new PowerProfileSnapshot();
        snapshot.getDetail().setName(name);

        for (int i=1; i < 8; i++) {
            PowerProfileDaySnapshot powerProfileDaySnapshot = new PowerProfileDaySnapshot();
            DayOfWeek dayOfWeek = DayOfWeek.of(i);
            powerProfileDaySnapshot.getDetail().setDayOfWeek(i);
            if (dayOfWeek == DayOfWeek.SATURDAY  || dayOfWeek == DayOfWeek.SUNDAY) {
                setAllHours(powerProfileDaySnapshot, PowerFlowCode.OFF_PEAK);
            } else {
                setAllHours(powerProfileDaySnapshot, PowerFlowCode.ON_PEAK);
            }
            snapshot.addPowerProfileDay(powerProfileDaySnapshot);
        }

        PowerProfileIndexMappingSnapshot indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(offPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.OFF_PEAK);
        snapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        indexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        indexMappingSnapshot.setPriceIndexId(onPeakIndex.generateEntityId());
        indexMappingSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.ON_PEAK);
        snapshot.addIndexMappingSnapshot(indexMappingSnapshot);

        return snapshot;
    }

    private static PowerProfileSnapshot createPowerProfileSnapshotWeekDaysSomeHours(String name) {
        PowerProfileSnapshot snapshot = new PowerProfileSnapshot();
        snapshot.getDetail().setName(name);

        for (int i=1; i < 8; i++) {
            PowerProfileDaySnapshot powerProfileDaySnapshot = new PowerProfileDaySnapshot();
            DayOfWeek dayOfWeek = DayOfWeek.of(i);
            if (! (dayOfWeek == DayOfWeek.SATURDAY  || dayOfWeek == DayOfWeek.SUNDAY) ) {
                powerProfileDaySnapshot.getDetail().setDayOfWeek(i);
                setSelectedHours(
                        powerProfileDaySnapshot,
                        PowerFlowCode.OFF_PEAK,
                        6,
                        9);
                setSelectedHours(
                        powerProfileDaySnapshot,
                        PowerFlowCode.ON_PEAK,
                        10,
                        18);
                snapshot.addPowerProfileDay(powerProfileDaySnapshot);
            }
        }

        return snapshot;
    }

    public static void setAllHours(PowerProfileDaySnapshot snapshot, PowerFlowCode flowCode) {
        for (int i=1; i < 25; i++) {
            snapshot.getDetail().setPowerFlowCode(i, flowCode);
        }
    }

    public static void setSelectedHours(
            PowerProfileDaySnapshot snapshot,
            PowerFlowCode flowCode,
            int hourStart,
            int hourEnd) {

        for (int i=hourStart; i <= hourEnd; i++) {
            snapshot.getDetail().setPowerFlowCode(i, flowCode);
        }
    }
}
