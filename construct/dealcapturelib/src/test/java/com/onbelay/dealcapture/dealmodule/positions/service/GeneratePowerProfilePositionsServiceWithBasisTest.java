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

public class GeneratePowerProfilePositionsServiceWithBasisTest extends PowerProfilePositionsWithFxTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Test
    public void generatePowerPositionsWithBasis() {

        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfileWithBasis.getId()));

        EvaluationContext context = new EvaluationContext(
                createdDateTime,
                fromMarketDate,
                toMarketDate);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfileWithBasis.getId()));

        flush();
        clearCache();

        List<PowerProfilePositionView> views = powerProfilePositionsService.fetchPowerProfilePositionViews(
                fromMarketDate,
                toMarketDate,
                createdDateTime);

        assertEquals(129, views.size());

        PowerProfileSnapshot snapshot =  powerProfileService.load(powerProfileWithBasis.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, snapshot.getDetail().getPositionGenerationStatusCode());
        assertNotNull(snapshot.getDetail().getPositionGenerationDateTime());
        List<PowerProfilePositionSnapshot> positionSnapshots = powerProfilePositionsService.findByPowerProfile(powerProfileWithBasis.generateEntityId());
        assertEquals(129, positionSnapshots.size());

        List<PowerProfilePositionSnapshot> startPositionSnapshots = positionSnapshots.stream().filter( c -> c.getDetail().getStartDate().isEqual(fromMarketDate)).toList();
        assertEquals(1, startPositionSnapshots.size());

        LocalDate nextMarketDate = LocalDate.of(2024, 1, 2);
        List<PowerProfilePositionSnapshot> nextPositionSnapshots = positionSnapshots.stream().filter( c -> c.getDetail().getStartDate().isEqual(nextMarketDate)).toList();
        assertEquals(2, nextPositionSnapshots.size());

        LocalDate nextMonthDate = LocalDate.of(2024, 2, 1);
        List<PowerProfilePositionSnapshot> nextMonthPositionSnapshots = positionSnapshots.stream().filter( c -> c.getDetail().getStartDate().isEqual(nextMonthDate)).toList();
        assertEquals(2, nextMonthPositionSnapshots.size());

    }


}
