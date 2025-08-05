package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileDay;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;

import java.util.List;

public class PowerProfileAssembler extends EntityAssembler {

    public PowerProfileSnapshot assemble(PowerProfile powerProfile) {
        PowerProfileSnapshot powerProfileSnapshot = new PowerProfileSnapshot();
        setEntityAttributes(powerProfile, powerProfileSnapshot);
        powerProfileSnapshot.getDetail().copyFrom(powerProfile.getDetail());

        if (powerProfile.getSettledPriceIndex() != null)
            powerProfileSnapshot.setSettledPriceIndexId(powerProfile.getSettledPriceIndex().generateEntityId());

        int totalHours = 0;
        List<PowerProfileDay> days = powerProfile.fetchPowerProfileDays();
        for (PowerProfileDay day : days) {
            totalHours = totalHours + day.calculateHours();
        }

        powerProfileSnapshot.setTotalHours(totalHours);

        PowerProfileDayAssembler dayAssembler = new PowerProfileDayAssembler();
        powerProfileSnapshot.setProfileDays(
                dayAssembler.assemble(days));

        PowerProfileIndexMappingAssembler indexMappingAssembler = new PowerProfileIndexMappingAssembler();
        powerProfileSnapshot.setIndexMappings(indexMappingAssembler.assemble(powerProfile.fetchPowerProfileIndexMappings()));
        return powerProfileSnapshot;
    }

    public List<PowerProfileSnapshot> assemble(List<PowerProfile> powerProfiles) {

        return powerProfiles
                .stream()
                .map(this::assemble)
                .toList();

    }

}
