package com.onbelay.dealcapture.riskfactor.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshotCollection;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WithMockUser
public class FxRiskFactorRestAdapterTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private FxRiskFactorRestAdapter fxRiskFactorRestAdapter;


    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Autowired
    private FxCurveRepository fxCurveRepository;

    private FxIndex cadUsdfxIndex;
    private FxIndex cadEurFxIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

    @Override
    public void setUp() {
        super.setUp();

        cadUsdfxIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.DAILY,
                CurrencyCode.USD,
                CurrencyCode.CAD);

        FxIndexFixture.generateDailyFxCurves(
                cadUsdfxIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(10, 1, 1, 1, 1));

        cadEurFxIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                CurrencyCode.EURO);

        FxRiskFactorFixture.createFxRiskFactors(
                cadUsdfxIndex,
                fromMarketDate,
                toMarketDate);

        FxIndexFixture.generateDailyFxCurves(
                cadUsdfxIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 10, 1, 0, 0));


        flush();
    }

    @Test
    public void fetchRiskFactors() {
        FxRiskFactorSnapshotCollection collection = fxRiskFactorRestAdapter.find(
                "WHERE indexId eq " + cadUsdfxIndex.getId(),
                0,
                100);

        assertEquals(31, collection.getSnapshots().size());
    }

    @Test
    public void createRiskFactors() {
        FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setMarketDate(LocalDate.of(2022, 1, 1));

        TransactionResult result = fxRiskFactorRestAdapter.save(
                cadUsdfxIndex.generateEntityId(),
                List.of(snapshot));

        FxRiskFactorSnapshot saved = fxRiskFactorService.load(result.getEntityId());
        assertNotNull(saved);
    }

}
