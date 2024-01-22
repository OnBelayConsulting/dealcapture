package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.model.FxCurve;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FxIndexServiceTest extends DealCaptureSpringTestCase {

    private FxIndex fxIndex;

    private FxIndex fxUSDEUROIndex;

    private FxIndex fxMonthlyIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Autowired
    private FxCurveRepository fxCurveRepository;

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    @Override
    public void setUp() {
        super.setUp();

        fxIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.CAD,
                CurrencyCode.USD);

        flush();

        FxIndexFixture.generateDailyFxCurves(
                fxIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 1, 1, 0, 1))


        ;


        fxMonthlyIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                CurrencyCode.USD);

        fxUSDEUROIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.USD,
                CurrencyCode.EURO);
        flush();
    }

    @Test
    public void createFxIndex() {

        FxIndexSnapshot snapshot = new FxIndexSnapshot();
        snapshot.getDetail().setName("CAD=>EURO M");
        snapshot.getDetail().setDescription("CAD to EURO");
        snapshot.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);
        snapshot.getDetail().setFromCurrencyCode(CurrencyCode.CAD);
        snapshot.getDetail().setToCurrencyCode(CurrencyCode.EURO);
        TransactionResult result = fxIndexService.save(snapshot);
        flush();
        FxIndex index = fxIndexRepository.load(result.getEntityId());
        assertNotNull(index);

    }

    @Test
    public void findFxIndices() {
        QuerySelectedPage selectedPage = fxIndexService.findFxIndexIds(new DefinedQuery("FxIndex"));
        List<FxIndexSnapshot> snapshots = fxIndexService.findByIds(selectedPage);
        assertEquals(3, snapshots.size());
    }

    @Test
    public void findIndexByCurrencyCodes() {
        List<FxIndexSnapshot> snapshots = fxIndexService.findFxIndexByFromToCurrencyCodes(
                fxIndex.getDetail().getFromCurrencyCode(),
                fxIndex.getDetail().getToCurrencyCode());
        assertEquals(2, snapshots.size());
    }

    @Test
    public  void findFxCurves() {
        QuerySelectedPage selectedPage = fxIndexService.findFxCurveIds(new DefinedQuery("FxCurve"));
        List<FxCurveSnapshot> snapshots = fxIndexService.fetchFxCurvesByIds(selectedPage);
        assertEquals(31, snapshots.size());
    }

    @Test
    public void fetchFxCurveReports() {
        DefinedQuery definedQuery = new DefinedQuery("FxIndex");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "name",
                        ExpressionOperator.EQUALS,
                        fxIndex.getDetail().getName()));

        QuerySelectedPage selectedPage = fxIndexService.findFxIndexIds(definedQuery);
        fxIndexService.fetchFxCurveReports(
                selectedPage,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.now());
    }
    
    @Test
    public void saveFxCurves() {
        FxCurveSnapshot snapshot = new FxCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        TransactionResult result = fxIndexService.saveFxCurves(
                fxIndex.generateEntityId(),
                List.of(snapshot));
        flush();
        FxCurve curve = fxCurveRepository.load(result.getEntityId());
        assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), curve.getDetail().getObservedDateTime());
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(curve.getDetail().getCurveValue()));
        assertEquals(fxIndex.getId(), curve.getFxIndex().getId());
        
    }



}
