package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileDay;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileIndexMapping;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileIndexMappingSnapshot;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PowerProfileServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfileRepository powerProfileRepository;

    private PricingLocation location;

    private PriceIndex monthlyPriceIndex;
    private PriceIndex priceDailyIndex;
    protected PriceIndex onPeakDailyIndex;
    protected PriceIndex offPeakDailyIndex;

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

        onPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "ON PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        offPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "OFF PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);



    }

    @Test
    public void createPowerProfile() {

        PowerProfileSnapshot snapshot = PowerProfileFixture.createPowerProfileSnapshotAllDaysAllHours(
                "mine",
                monthlyPriceIndex,
                onPeakDailyIndex);

        TransactionResult result = powerProfileService.save(snapshot);

        PowerProfile powerProfile = powerProfileRepository.load(result.getEntityId());
        assertEquals("mine", powerProfile.getDetail().getName());
        assertEquals(7, powerProfile.fetchPowerProfileDays().size());
        PowerProfileDay day = powerProfile.fetchPowerProfileDays().get(0);
        for (int i = 1; i < 25; i++) {
            assertNotNull(day.getDetail().getPowerFlowCode(i));
        }
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


    @Test
    public void updatePowerProfile()  {
        PowerProfile profile =PowerProfileFixture.createPowerProfileWeekDaysSomeHours(
                "5*12",
                priceDailyIndex,
                offPeakDailyIndex,
                onPeakDailyIndex);
        flush();

        PowerProfileSnapshot snapshot = powerProfileService.load(profile.generateEntityId());

        Optional<PowerProfileIndexMappingSnapshot> mappingOpt = snapshot.getIndexMappings()
                .stream()
                .filter( c->  c.getDetail().getPowerFlowCode() == PowerFlowCode.ON_PEAK)
                .findFirst();
        assertTrue(mappingOpt.isPresent());
        PowerProfileIndexMappingSnapshot mapping = mappingOpt.get();
        mapping.setEntityState(EntityState.MODIFIED);
        mapping.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);

        assertEquals(5, snapshot.getProfileDays().size());
        PowerProfileDaySnapshot day = snapshot.getProfileDays().get(0);
        assertEquals(PowerFlowCode.NONE, day.getDetail().getPowerFlowCode(24));

        for (int i = 1; i < 5; i++) {
            snapshot.getProfileDays().get(i).setEntityState(EntityState.DELETE);
        }


        for (int i = 1; i < 25; i++) {
            day.getDetail().setPowerFlowCode(i, PowerFlowCode.NONE);
        }
        day.getDetail().setPowerFlowCode(24, PowerFlowCode.OFF_PEAK);
        day.setEntityState(EntityState.MODIFIED);
        snapshot.setEntityState(EntityState.MODIFIED);
        powerProfileService.save(snapshot);
        flush();

        PowerProfileSnapshot updatedSnapshot = powerProfileService.load(profile.generateEntityId());
        assertEquals(1, updatedSnapshot.getProfileDays().size());
        PowerProfileDaySnapshot day2 = updatedSnapshot.getProfileDays().get(0);

        for (int i = 1; i < 24; i++) {
            assertEquals(PowerFlowCode.NONE, day2.getDetail().getPowerFlowCode(i));
        }
        assertEquals(PowerFlowCode.OFF_PEAK, day2.getDetail().getPowerFlowCode(24));
    }


    @Test
    public void updatePowerProfileWithChanged()  {
        PowerProfile profile =PowerProfileFixture.createPowerProfileWeekDaysSomeHours(
                "5*12",
                priceDailyIndex,
                offPeakDailyIndex,
                onPeakDailyIndex);
        flush();

        PowerProfileSnapshot snapshot = powerProfileService.load(profile.generateEntityId());

        Optional<PowerProfileIndexMappingSnapshot> mappingOpt = snapshot.getIndexMappings()
                .stream()
                .filter( c->  c.getDetail().getPowerFlowCode() == PowerFlowCode.ON_PEAK)
                .findFirst();
        assertTrue(mappingOpt.isPresent());
        PowerProfileIndexMappingSnapshot mapping = mappingOpt.get();
        mapping.setEntityState(EntityState.MODIFIED);
        mapping.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);

        assertEquals(5, snapshot.getProfileDays().size());
        PowerProfileDaySnapshot day = snapshot.getProfileDays().get(0);
        assertEquals(PowerFlowCode.NONE, day.getDetail().getPowerFlowCode(24));

        for (int i = 1; i < 5; i++) {
            snapshot.getProfileDays().get(i).setEntityState(EntityState.DELETE);
        }


        for (int i = 1; i < 25; i++) {
            day.getDetail().setPowerFlowCode(i, PowerFlowCode.NONE);
        }
        day.getDetail().setPowerFlowCode(24, PowerFlowCode.OFF_PEAK);
        day.setEntityState(EntityState.MODIFIED);
        snapshot.setEntityState(EntityState.MODIFIED);
        powerProfileService.save(snapshot);
        flush();

        PowerProfileSnapshot updatedSnapshot = powerProfileService.load(profile.generateEntityId());
        assertEquals(1, updatedSnapshot.getProfileDays().size());
        PowerProfileDaySnapshot day2 = updatedSnapshot.getProfileDays().get(0);

        for (int i = 1; i < 24; i++) {
            assertEquals(PowerFlowCode.NONE, day2.getDetail().getPowerFlowCode(i));
        }
        assertEquals(PowerFlowCode.OFF_PEAK, day2.getDetail().getPowerFlowCode(24));


    }


}
