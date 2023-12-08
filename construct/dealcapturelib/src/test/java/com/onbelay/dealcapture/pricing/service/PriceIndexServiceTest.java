package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PriceIndexServiceTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex priceIndex;
    private PriceIndex priceDailyIndex;


    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceCurveRepository priceCurveRepository;


    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                location);

        priceDailyIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                location);

    }

    @Test
    public void fetchIndices() {
        PriceIndexSnapshot snapshot = priceIndexService.findPriceIndexByName("ACEE");
        assertNotNull(snapshot);
        assertEquals(FrequencyCode.MONTHLY, snapshot.getDetail().getFrequencyCode());
    }

    @Test
    public void saveHubIndex() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.setPricingLocationId(location.generateEntityId());
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setDaysOffsetForExpiry(6);

        TransactionResult result = priceIndexService.save(snapshot);
        flush();

        PriceIndex index = priceIndexRepository.load(result.getEntityId());
        assertNotNull(index);
        assertEquals(6, index.getDetail().getDaysOffsetForExpiry().intValue());
    }


    @Test
    public void saveBasisIndexFailMissingLocation() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.BASIS);
        snapshot.setBaseIndexId(priceDailyIndex.generateEntityId());

        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setDaysOffsetForExpiry(6);

        try {
            TransactionResult result = priceIndexService.save(snapshot);
            fail("should have thrown exception");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.MISSING_PRICING_LOCATION.getCode(), e.getErrorCode());
            return;
        }
        fail("should have thrown exception");
    }


    @Test
    public void saveBasisIndexFailMissingHub() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.setPricingLocationId(location.generateEntityId());
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.BASIS);

        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setDaysOffsetForExpiry(6);

        try {
            TransactionResult result = priceIndexService.save(snapshot);
            fail("should have thrown exception");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.MISSING_BASE_INDEX.getCode(), e.getErrorCode());

            return;
        }
        fail("should have thrown exception");
    }

    @Test
    public void savePriceCurves() {
        PriceCurveSnapshot snapshot = new PriceCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        TransactionResult result = priceIndexService.savePrices(
                priceIndex.generateEntityId(),
                List.of(snapshot));
        flush();
        assertEquals(1, result.getEntityIds().size());

        PriceCurve curve = priceCurveRepository.load(result.getEntityId());
        assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), curve.getDetail().getObservedDateTime());
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(curve.getDetail().getCurveValue()));
        assertEquals(priceIndex.getId(), curve.getPriceIndex().getId());
    }

}
