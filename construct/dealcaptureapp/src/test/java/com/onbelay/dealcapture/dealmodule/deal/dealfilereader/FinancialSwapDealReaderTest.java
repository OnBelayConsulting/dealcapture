package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
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
public class FinancialSwapDealReaderTest extends DealCaptureAppSpringTestCase {

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;
    private PriceIndex receivesIndex;
    private PriceIndex paysIndex;

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
        receivesIndex = PriceIndexFixture.createPriceIndex(
                "AECO",
                location);
        paysIndex = PriceIndexFixture.createPriceIndex(
                "PAYS",
                location);

        flush();
    }

    @Test
    public void createFixedForFloatSwapDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/fixed4floatswapdealupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        FinancialSwapDealSnapshot swapDealSnapshot = (FinancialSwapDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", swapDealSnapshot.getCompanyRoleId().getCode());
        assertEquals("OnBelay", swapDealSnapshot.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, swapDealSnapshot.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, swapDealSnapshot.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, swapDealSnapshot.getDealDetail().getBuySell());
        assertEquals("gh_87", swapDealSnapshot.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), swapDealSnapshot.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), swapDealSnapshot.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(swapDealSnapshot.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, swapDealSnapshot.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, swapDealSnapshot.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getSettlementCurrencyCode());
        assertEquals(ValuationCode.FIXED, swapDealSnapshot.getDetail().getPaysValuationCode());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(swapDealSnapshot.getDealDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, swapDealSnapshot.getDealDetail().getFixedPriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getFixedPriceCurrencyCode());
        assertEquals("AECO", swapDealSnapshot.getReceivesPriceIndexId().getCode());
        assertEquals(ValuationCode.INDEX, swapDealSnapshot.getDetail().getReceivesValuationCode());
    }


    @Test
    public void createFloatForFloatSwapDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/float4floatswapdealupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        FinancialSwapDealSnapshot swapDealSnapshot = (FinancialSwapDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", swapDealSnapshot.getCompanyRoleId().getCode());
        assertEquals("OnBelay", swapDealSnapshot.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, swapDealSnapshot.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, swapDealSnapshot.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, swapDealSnapshot.getDealDetail().getBuySell());
        assertEquals("gh_87", swapDealSnapshot.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), swapDealSnapshot.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), swapDealSnapshot.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(swapDealSnapshot.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, swapDealSnapshot.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, swapDealSnapshot.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getSettlementCurrencyCode());
        assertEquals(ValuationCode.INDEX, swapDealSnapshot.getDetail().getPaysValuationCode());
        assertNull(swapDealSnapshot.getDealDetail().getFixedPriceValue());
        assertEquals("PAYS", swapDealSnapshot.getPaysPriceIndexId().getCode());
        assertEquals(ValuationCode.INDEX, swapDealSnapshot.getDetail().getReceivesValuationCode());
        assertEquals("AECO", swapDealSnapshot.getReceivesPriceIndexId().getCode());
    }


    @Test
    public void createFloatPlusFixedForFloatSwapDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/floatplusfixed4floatswapdealupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        FinancialSwapDealSnapshot swapDealSnapshot = (FinancialSwapDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", swapDealSnapshot.getCompanyRoleId().getCode());
        assertEquals("OnBelay", swapDealSnapshot.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, swapDealSnapshot.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, swapDealSnapshot.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, swapDealSnapshot.getDealDetail().getBuySell());
        assertEquals("gh_87", swapDealSnapshot.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), swapDealSnapshot.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), swapDealSnapshot.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(swapDealSnapshot.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, swapDealSnapshot.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, swapDealSnapshot.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getSettlementCurrencyCode());
        assertEquals(ValuationCode.INDEX_PLUS, swapDealSnapshot.getDetail().getPaysValuationCode());
        assertEquals("PAYS", swapDealSnapshot.getPaysPriceIndexId().getCode());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(swapDealSnapshot.getDealDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, swapDealSnapshot.getDealDetail().getFixedPriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, swapDealSnapshot.getDealDetail().getFixedPriceCurrencyCode());
        assertEquals("AECO", swapDealSnapshot.getReceivesPriceIndexId().getCode());
        assertEquals(ValuationCode.INDEX, swapDealSnapshot.getDetail().getReceivesValuationCode());
    }

}
