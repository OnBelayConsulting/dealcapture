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

public class ValuePowerProfilePositionsServiceWithBasisTest extends PowerProfilePositionsWithFxTestCase {

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

    @Override
    public void setUp() {
        super.setUp();

        PriceIndexFixture.generateHourlyPriceCurves(
                settledHourlyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(4),
                observedDateTime);

        PriceIndexFixture.generateHourlyPriceCurves(
                forwardHourlyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(8.03),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                offPeakDailyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(3),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                hubIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(8),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                onPeakDailyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                observedDateTime);


    }

    @Test
    public void valuePositionsWithBasis() {

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

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePowerProfilePositionsService.valuePositions(
                powerProfileWithBasis.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();


        PowerProfileSnapshot snapshot =  powerProfileService.load(powerProfileWithBasis.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, snapshot.getDetail().getPositionGenerationStatusCode());
        assertNotNull(snapshot.getDetail().getPositionGenerationDateTime());
        List<PowerProfilePositionSnapshot> positionSnapshots = powerProfilePositionsService.findByPowerProfile(powerProfileWithBasis.generateEntityId());
        assertEquals(129, positionSnapshots.size());
        for (PowerProfilePositionSnapshot positionSnapshot : positionSnapshots) {
            for (int i=1; i < 25; i++) {
                if (positionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i) != null)
                    assertNotNull(positionSnapshot.getHourPriceDayDetail().getHourPrice(i));
            }
        }
    }


}
