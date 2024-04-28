package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileDay;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;

import java.util.List;

public class PowerProfileDayAssembler extends EntityAssembler {
    
    public PowerProfileDaySnapshot assemble(PowerProfileDay profileDay) {
        PowerProfileDaySnapshot powerProfileSnapshot = new PowerProfileDaySnapshot();
        setEntityAttributes(profileDay, powerProfileSnapshot);
        powerProfileSnapshot.getDetail().copyFrom(profileDay.getDetail());
        return powerProfileSnapshot;
    }
    
    public List<PowerProfileDaySnapshot> assemble(List<PowerProfileDay> profileDays) {
        
        return profileDays
                .stream()
                .map(this::assemble)
                .toList();
        
    }
    
}
