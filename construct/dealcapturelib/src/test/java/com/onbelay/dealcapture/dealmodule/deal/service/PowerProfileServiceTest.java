package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerProfileServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfileRepository powerProfileRepository;

    @Test
    public void createPowerProfile() {

        PowerProfileSnapshot snapshot = PowerProfileFixture.createPowerProfileSnapshotAllDaysAllHours("mine");

        TransactionResult result = powerProfileService.save(snapshot);

        PowerProfile powerProfile = powerProfileRepository.load(result.getEntityId());
        assertEquals("mine", powerProfile.getDetail().getName());
    }

}
