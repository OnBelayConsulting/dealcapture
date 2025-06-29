package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionStyleCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
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

@WithMockUser
public class VanillaOptionDealReaderTest extends DealCaptureAppSpringTestCase {

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;
    private PriceIndex priceIndex;

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

        flush();
    }

    @Test
    public void createVanillaOptionDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/vanillaoptionupload.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealFileReader dealFileReader = new DealFileReader(fileStream);
        dealFileReader.readContents();

        List<BaseDealSnapshot> snapshots = dealFileReader.getDealSnapshots();

        VanillaOptionDealSnapshot dealSnapshot = (VanillaOptionDealSnapshot) snapshots.get(0);
        assertEquals("OnBelay", dealSnapshot.getCompanyRoleId().getCode());
        assertEquals("OnBelay", dealSnapshot.getCounterpartyRoleId().getCode());
        assertEquals(CommodityCode.NATGAS, dealSnapshot.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, dealSnapshot.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, dealSnapshot.getDealDetail().getBuySell());
        assertEquals("gh_87", dealSnapshot.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), dealSnapshot.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), dealSnapshot.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(dealSnapshot.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, dealSnapshot.getDealDetail().getVolumeUnitOfMeasureCode());
        assertEquals(FrequencyCode.DAILY, dealSnapshot.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, dealSnapshot.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, dealSnapshot.getDealDetail().getSettlementCurrencyCode());

        assertEquals("AECO", dealSnapshot.getUnderlyingPriceIndexId().getCode());
        assertEquals(OptionExpiryDateRuleToken.POSITION_END_DATE, dealSnapshot.getDetail().getOptionExpiryDateRuleToken());
        assertEquals(TradeTypeCode.OTC, dealSnapshot.getDetail().getTradeTypeCode());
        assertEquals(OptionTypeCode.CALL, dealSnapshot.getDetail().getOptionTypeCode());
        assertEquals(OptionStyleCode.AMERICAN, dealSnapshot.getDetail().getOptionStyleCode());

        assertEquals(0, BigDecimal.valueOf(3.77).compareTo(dealSnapshot.getDetail().getStrikePriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, dealSnapshot.getDetail().getStrikePriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, dealSnapshot.getDetail().getStrikePriceCurrencyCode());

        assertEquals(0, BigDecimal.valueOf(2.00).compareTo(dealSnapshot.getDetail().getPremiumPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, dealSnapshot.getDetail().getPremuimPriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, dealSnapshot.getDetail().getPremiumPriceCurrencyCode());
    }
}
