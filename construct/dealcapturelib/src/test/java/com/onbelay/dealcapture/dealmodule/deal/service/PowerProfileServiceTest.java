package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerProfileServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfileRepository powerProfileRepository;

    private PricingLocation location;

    private PriceIndex monthlyPriceIndex;
    private PriceIndex priceDailyIndex;

    @Override
    public void setUp() {
        super.setUp();
        location = PricingLocationFixture.createPricingLocation("ddg");

        monthlyPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        priceDailyIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

    }

    @Test
    public void createPowerProfile() {

        PowerProfileSnapshot snapshot = PowerProfileFixture.createPowerProfileSnapshotAllDaysAllHours("mine");

        TransactionResult result = powerProfileService.save(snapshot);

        PowerProfile powerProfile = powerProfileRepository.load(result.getEntityId());
        assertEquals("mine", powerProfile.getDetail().getName());
    }

    @Test
    public void findPowerProfiles()  {
        PowerProfileFixture.createPowerProfileAllDaysAllHours(
                "5*12",
                priceDailyIndex);
        flush();
        DefinedQuery definedQuery = new DefinedQuery("PowerProfile");
        QuerySelectedPage selectedPage = powerProfileService.findPowerProfileIds(definedQuery);
        assertEquals(1, selectedPage.getIds().size());

    }

}
