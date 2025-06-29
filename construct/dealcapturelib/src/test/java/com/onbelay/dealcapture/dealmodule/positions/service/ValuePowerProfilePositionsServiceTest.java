package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValuePowerProfilePositionsServiceTest extends PowerProfilePositionsWithFxTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private ValuePowerProfilePositionsService valuePowerProfilePositionsService;

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    private LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 2, 43);

    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

    @Override
    public void setUp() {
        super.setUp();

        PriceIndexFixture.generateHourlyPriceCurves(
                settledHourlyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(4),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                offPeakDailyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(3),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                onPeakDailyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                observedDateTime);


    }

    @Test
    public void value7By24Positions() {

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

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePowerProfilePositionsService.valuePositions(
                powerProfile.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                LocalDateTime.now());
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
