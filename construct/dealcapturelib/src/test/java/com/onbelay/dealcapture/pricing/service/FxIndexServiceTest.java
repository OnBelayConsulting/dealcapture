package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FxIndexServiceTest extends DealCaptureSpringTestCase {

    private FxIndex fxIndex;

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Override
    public void setUp() {
        super.setUp();

        fxIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.CAD,
                CurrencyCode.US);
        flush();
    }

    @Test
    public void createFxIndex() {

        FxIndexSnapshot snapshot = new FxIndexSnapshot();
        snapshot.getDetail().setName("CAD=>USD M");
        snapshot.getDetail().setDescription("desc");
        snapshot.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);
        snapshot.getDetail().setFromCurrencyCode(CurrencyCode.CAD);
        snapshot.getDetail().setToCurrencyCode(CurrencyCode.US);
        TransactionResult result = fxIndexService.save(snapshot);
        flush();
        FxIndex index = fxIndexRepository.load(result.getEntityId());
        assertNotNull(index);

    }


}
