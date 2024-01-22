package com.onbelay.dealcapture.riskfactor.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshotCollection;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WithMockUser
public class PriceRiskFactorRestAdapterTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRestAdapter priceRiskFactorRestAdapter;

    @Autowired
    private PricingLocationRepository pricingLocationRepository;

    private PriceIndex aadcPriceIndex;
    private PriceIndex bdddPriceIndex;

    @Override
    public void setUp() {
        super.setUp();
        PricingLocation pricingLocation = PricingLocationFixture.createPricingLocation("west");

        aadcPriceIndex = PriceIndexFixture.createPriceIndex("AADC", pricingLocation);
        bdddPriceIndex = PriceIndexFixture.createPriceIndex("Bddd", pricingLocation);
        flush();

        PriceRiskFactorFixture.createPriceRiskFactors(
                bdddPriceIndex,
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 1, 31));

        flush();
    }

    @Test
    public void fetchRiskFactors() {
        PriceRiskFactorSnapshotCollection collection = priceRiskFactorRestAdapter.find(
                "WHERE indexId eq " + bdddPriceIndex.getId(),
                0,
                100);

        assertEquals(31, collection.getSnapshots().size());
    }

    @Test
    public void valueRiskFactors() {
        TransactionResult result = priceRiskFactorRestAdapter.valueRiskFactors("WHERE " );

    }

    @Test
    public void createRiskFactors() {
        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setMarketDate(LocalDate.of(2022, 1, 1));

        TransactionResult result = priceRiskFactorRestAdapter.save(
                aadcPriceIndex.generateEntityId(),
                List.of(snapshot));

        PriceRiskFactorSnapshot saved = priceRiskFactorService.load(result.getEntityId());
        assertNotNull(saved);
    }

}
