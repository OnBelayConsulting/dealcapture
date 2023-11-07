package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorAudit;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FxRiskFactorServiceTest extends DealCaptureSpringTestCase {

    private FxIndex fxIndex;

    private FxIndex fxDailyIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 10, 31);

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    @Override
    public void setUp() {
        super.setUp();

        fxIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.CAD,
                CurrencyCode.US);

        fxDailyIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.US,
                CurrencyCode.CAD);


        FxRiskFactorFixture.createFxRiskFactors(
                fxIndex,
                fromMarketDate,
                toMarketDate);
        flush();
    }

    @Test
    public void createRiskFactor() {
        FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setMarketDate(LocalDate.of(2023, 6, 23));
        snapshot.getDetail().setCreateUpdateDate(LocalDateTime.of(2023, 7, 1, 11, 6));
        snapshot.getDetail().setValue(BigDecimal.ONE);
        fxRiskFactorService.saveFxRiskFactors(
                fxDailyIndex.generateEntityId(),
                List.of(snapshot));
        flush();

        DefinedQuery query = new DefinedQuery("FxRiskFactor");
        List<Integer> ids = fxRiskFactorRepository.findFxRiskFactorIds(query);

        FxRiskFactor factor = fxRiskFactorRepository.fetchByMarketDate(
                fxDailyIndex.generateEntityId(),
                LocalDate.of(2023, 6, 23));
        assertNotNull(factor);
        assertEquals(0, BigDecimal.ONE.compareTo(factor.getDetail().getValue()));
        assertEquals(LocalDateTime.of(2023, 7, 1, 11, 6), factor.getDetail().getCreateUpdateDate());
        FxRiskFactorAudit audit = FxRiskFactorAudit.findRecentHistory(factor);
        assertNotNull(audit);
    }

    @Test
    public void testFindByMarketDate() {
        FxRiskFactorSnapshot snapshot = fxRiskFactorService.findByMarketDate(
                fxIndex.generateEntityId(),
                LocalDate.of(2023, 2, 4));

        assertNotNull(snapshot.getFxIndexId());
        assertEquals(LocalDate.of(2023, 2, 4),snapshot.getDetail().getMarketDate());

    }

}
