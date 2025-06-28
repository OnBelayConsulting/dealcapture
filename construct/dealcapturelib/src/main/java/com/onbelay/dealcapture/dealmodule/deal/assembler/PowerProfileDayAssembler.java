package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileDay;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;

import java.util.List;

public class PowerProfileDayAssembler extends EntityAssembler {
    
    public PowerProfileDaySnapshot assemble(PowerProfileDay profileDay) {
        PowerProfileDaySnapshot daySnapshot = new PowerProfileDaySnapshot();
        setEntityAttributes(profileDay, daySnapshot);
        daySnapshot.getDetail().copyFrom(profileDay.getDetail());
        for (int i = 1 ; i <= 24; i++) {
            daySnapshot.getDetail().getHours().add(
                    daySnapshot.getDetail().getPowerFlowCode(i).getCode());
        }
        return daySnapshot;
    }
    
    public List<PowerProfileDaySnapshot> assemble(List<PowerProfileDay> profileDays) {
        
        return profileDays
                .stream()
                .map(this::assemble)
                .toList();
        
    }
    
}
