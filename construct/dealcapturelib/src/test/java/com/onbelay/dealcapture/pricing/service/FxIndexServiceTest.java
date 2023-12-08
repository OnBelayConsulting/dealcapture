package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.model.FxCurve;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
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

public class FxIndexServiceTest extends DealCaptureSpringTestCase {

    private FxIndex fxIndex;

    private FxIndex fxUSDEUROIndex;


    private FxIndex fxMonthlyIndex;

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
    public void findIndexByCurrencyCodes() {
        List<FxIndexSnapshot> snapshots = fxIndexService.findFxIndexByFromToCurrencyCodes(
                fxIndex.getDetail().getFromCurrencyCode(),
                fxIndex.getDetail().getToCurrencyCode());
        assertEquals(2, snapshots.size());
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
