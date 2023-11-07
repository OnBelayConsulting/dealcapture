package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class PriceIndexServiceTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex priceIndex;
    private PriceIndex priceDailyIndex;


    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

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
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
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
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
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
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
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

}
