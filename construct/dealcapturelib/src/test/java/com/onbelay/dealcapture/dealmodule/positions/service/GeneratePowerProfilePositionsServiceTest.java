package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.service.DealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
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

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate)
                .withEndPositionDate(toMarketDate);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfile.getId()));

        flush();
        clearCache();

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

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate)
                .withEndPositionDate(toMarketDate);

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
