package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileIndexMapping;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingSnapshot;

import java.util.List;

public class PowerProfileIndexMappingAssembler extends EntityAssembler {

    public PowerProfileIndexMappingSnapshot assemble(PowerProfileIndexMapping indexMapping) {
        PowerProfileIndexMappingSnapshot powerProfileIndexMappingSnapshot = new PowerProfileIndexMappingSnapshot();
        setEntityAttributes(indexMapping, powerProfileIndexMappingSnapshot);
        powerProfileIndexMappingSnapshot.getDetail().copyFrom(indexMapping.getDetail());

        powerProfileIndexMappingSnapshot.setPowerProfileId(indexMapping.getPowerProfile().generateEntityId());
        powerProfileIndexMappingSnapshot.setPriceIndexId(indexMapping.getPriceIndex().generateEntityId());

        return powerProfileIndexMappingSnapshot;
    }

    public List<PowerProfileIndexMappingSnapshot> assemble(List<PowerProfileIndexMapping> indexMappings) {

        return indexMappings
                .stream()
                .map(this::assemble)
                .toList();

    }

}
