package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePowerProfilePositionsServiceTest extends PowerProfilePositionsWithFxTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

    @Test
    public void generate7By24Positions() {

        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfile.getId()));

        EvaluationContext context = new EvaluationContext(
                createdDateTime,
                fromMarketDate,
                toMarketDate);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfile.getId()));

        flush();
        clearCache();

        List<PowerProfilePositionView> views = powerProfilePositionsService.fetchPowerProfilePositionViews(
                fromMarketDate,
                toMarketDate,
                createdDateTime);

        assertEquals(31, views.size());

        PowerProfileSnapshot snapshot =  powerProfileService.load(powerProfile.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, snapshot.getDetail().getPositionGenerationStatusCode());
        assertNotNull(snapshot.getDetail().getPositionGenerationDateTime());
        List<PowerProfilePositionSnapshot> positionSnapshots = powerProfilePositionsService.findByPowerProfile(powerProfile.generateEntityId());
        assertEquals(31, positionSnapshots.size());

        PowerProfilePositionSnapshot settledPosition = positionSnapshots.get(0);
        assertEquals(PowerFlowCode.SETTLED, settledPosition.getDetail().getPowerFlowCode());
        assertEquals(settledHourlyIndex.getId(), settledPosition.getPriceIndexId().getId());
        assertEquals(powerProfile.getId(), settledPosition.getPowerProfileId().getId());

        PowerProfilePositionSnapshot offPeakPosition = positionSnapshots.get(1);
        assertNotNull(offPeakPosition);
        PowerProfilePositionSnapshot onPeakPosition = positionSnapshots.get(2);
        assertNotNull(onPeakPosition);
    }

    @Test
    public void generate5By12Positions() {

        powerProfileService.updatePositionGenerationStatusToPending(List.of(mixedPowerProfile.getId()));

        EvaluationContext context = new EvaluationContext(
                createdDateTime,
                fromMarketDate,
                toMarketDate);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(mixedPowerProfile.getId()));

        flush();
        clearCache();

        PowerProfileSnapshot snapshot =  powerProfileService.load(mixedPowerProfile.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, snapshot.getDetail().getPositionGenerationStatusCode());
        assertNotNull(snapshot.getDetail().getPositionGenerationDateTime());
        List<PowerProfilePositionSnapshot> positionSnapshots = powerProfilePositionsService.findByPowerProfile(mixedPowerProfile.generateEntityId());
        assertEquals(45, positionSnapshots.size());

        PowerProfilePositionSnapshot settledPosition = positionSnapshots.get(0);
        assertEquals(PowerFlowCode.SETTLED, settledPosition.getDetail().getPowerFlowCode());
        assertEquals(settledHourlyIndex.getId(), settledPosition.getPriceIndexId().getId());
        for (int i=1; i <25 ; i++) {
            Integer rfId = settledPosition.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i);
            if (rfId != null) {
                if (i < 6 || i > 18)
                    fail();
            }
        }

        PowerProfilePositionSnapshot offPeakPosition = positionSnapshots.get(1);
        assertNotNull(offPeakPosition);
        PowerProfilePositionSnapshot onPeakPosition = positionSnapshots.get(2);
        assertNotNull(onPeakPosition);
    }


}
