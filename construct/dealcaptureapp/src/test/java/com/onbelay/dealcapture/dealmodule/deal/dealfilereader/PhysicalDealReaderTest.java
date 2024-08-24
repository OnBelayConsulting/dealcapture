package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@WithMockUser
public class PhysicalDealReaderTest extends DealCaptureAppSpringTestCase {

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;
    private PriceIndex priceIndex;
    private PriceIndex dealPriceIndex;

    private LocalDate startDate = LocalDate.of(2022, 1, 1);
    private LocalDate endDate = LocalDate.of(2022, 12, 31);

    @Autowired
    private DealRepository dealRepository;

    @Override
    public void setUp() {
        super.setUp();
        companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
        counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

        PricingLocation location = PricingLocationFixture.createPricingLocation("west");
        priceIndex = PriceIndexFixture.createPriceIndex(
                "AECO",
                location);
        dealPriceIndex = PriceIndexFixture.createPriceIndex(
                "CCVV",
                location);

        flush();
    }

    @Test
    public void createFixedPhysicalDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/physicaldealfixedupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        PhysicalDealSnapshot physicalDeal = (PhysicalDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", physicalDeal.getCompanyRoleId().getCode());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, physicalDeal.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, physicalDeal.getDealDetail().getBuySell());
        assertEquals("gh_87", physicalDeal.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), physicalDeal.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), physicalDeal.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(physicalDeal.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, physicalDeal.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getSettlementCurrencyCode());
        assertEquals(ValuationCode.FIXED, physicalDeal.getDetail().getDealPriceValuationCode());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(physicalDeal.getDealDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getFixedPriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getFixedPriceCurrencyCode());
        assertEquals(ValuationCode.INDEX, physicalDeal.getDetail().getMarketValuationCode());
        assertEquals("AECO", physicalDeal.getMarketPriceIndexId().getCode());
    }


    @Test
    public void createIndexPhysicalDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/physicaldealindexupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        PhysicalDealSnapshot physicalDeal = (PhysicalDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", physicalDeal.getCompanyRoleId().getCode());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, physicalDeal.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, physicalDeal.getDealDetail().getBuySell());
        assertEquals("gh_87", physicalDeal.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), physicalDeal.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), physicalDeal.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(physicalDeal.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, physicalDeal.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getSettlementCurrencyCode());
        assertEquals(ValuationCode.INDEX, physicalDeal.getDetail().getDealPriceValuationCode());
        assertEquals("CCVV", physicalDeal.getDealPriceIndexId().getCode());
        assertNull(physicalDeal.getDealDetail().getFixedPriceValue());
        assertEquals("AECO", physicalDeal.getMarketPriceIndexId().getCode());
    }

}
