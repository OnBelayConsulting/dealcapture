package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.*;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithMockUser
public class FinancialSwapDealRestAdapterBeanTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private DealRestAdapter dealRestAdapter;
    private BusinessContact contact;
    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;
    protected PriceIndex receivesIndex;
    protected PriceIndex paysIndex;

    protected FinancialSwapDeal fixed4FloatSellDeal;
    protected FinancialSwapDeal fixed4FloatBuyDeal;
    protected FinancialSwapDeal float4FloatBuyDeal;
    protected FinancialSwapDeal float4FloatPlusBuyDeal;
    protected FinancialSwapDeal fixed4PowerProfileBuyDeal;

    protected PriceIndex settledHourlyIndex;
    protected PriceIndex onPeakDailyIndex;
    protected PriceIndex offPeakDailyIndex;

    protected PowerProfile powerProfile;

    protected PricingLocation pricingLocation;

    private LocalDate startDate = LocalDate.of(2022, 1, 1);
    private LocalDate endDate = LocalDate.of(2022, 12, 31);

    @Autowired
    private DealRepository dealRepository;

    @Override
    public void setUp() {
        super.setUp();
        contact = BusinessContactFixture.createCompanyTrader("hans", "gruber", "gruber@terror.com");

        companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
        counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

        pricingLocation = PricingLocationFixture.createPricingLocation("west");


        settledHourlyIndex = PriceIndexFixture.createPriceIndex(
                "SETTLE",
                FrequencyCode.HOURLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        onPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "ON PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        offPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "OFF PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);


        powerProfile = PowerProfileFixture.createPowerProfileAllDaysAllHours(
                "24By7",
                settledHourlyIndex,
                offPeakDailyIndex,
                onPeakDailyIndex);



        receivesIndex = PriceIndexFixture.createPriceIndex(
                "AECO",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        paysIndex = PriceIndexFixture.createPriceIndex(
                "Pays",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        flush();

        FinancialSwapDealSnapshot snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.SELL,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "f4floatsell",
                companyRole,
                counterpartyRole,
                receivesIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        snapshot.setCompanyTraderId(contact.generateEntityId());
        fixed4FloatSellDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "f4floatbuy",
                companyRole,
                counterpartyRole,
                receivesIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        snapshot.setCompanyTraderId(contact.generateEntityId());

        fixed4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4floatbuy",
                companyRole,
                counterpartyRole,
                paysIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                receivesIndex);
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        snapshot.setCompanyTraderId(contact.generateEntityId());
        float4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4floatPlusbuy",
                companyRole,
                counterpartyRole,
                paysIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                receivesIndex,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.setCompanyTraderId(contact.generateEntityId());
        float4FloatPlusBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4powerprofbuy",
                companyRole,
                counterpartyRole,
                powerProfile,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));

        snapshot.setCompanyTraderId(contact.generateEntityId());

        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        fixed4PowerProfileBuyDeal = FinancialSwapDeal.create(snapshot);

        flush();
        clearCache();

        fixed4FloatSellDeal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());
        float4FloatBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatBuyDeal.generateEntityId());
        float4FloatPlusBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusBuyDeal.generateEntityId());
        fixed4PowerProfileBuyDeal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

    }

    @Test
    public void createFinancialSwapDeal() {
        Price paysPrice = new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ);

        FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "ghkk",
                companyRole,
                counterpartyRole,
                receivesIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                paysPrice);
        swapDealSnapshot.setCompanyTraderId(contact.generateEntityId());
        TransactionResult result = dealRestAdapter.save(swapDealSnapshot);
        flush();
        FinancialSwapDeal swapDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
        assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
        assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
        assertEquals(startDate, swapDeal.getDealDetail().getStartDate());
        assertEquals(endDate, swapDeal.getDealDetail().getEndDate());
        assertEquals(BuySellCode.BUY, swapDeal.getDealDetail().getBuySell());
        assertEquals(CurrencyCode.CAD, swapDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
        assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
        assertEquals(ValuationCode.INDEX, swapDeal.getDetail().getReceivesValuationCode());
        assertEquals(receivesIndex.getId(), swapDeal.getReceivesPriceIndex().getId());
        assertEquals(ValuationCode.FIXED, swapDeal.getDetail().getPaysValuationCode());
        assertEquals(paysPrice, swapDeal.getDealDetail().getFixedPrice());

    }


    @Test
    public void createFinancialSwapDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/fixed4floatswapdealupload.csv");

        TransactionResult result = dealRestAdapter.saveFile("test.csv", inputStream.readAllBytes());
        flush();
        FinancialSwapDeal physicalDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
        assertEquals("OnBelay", physicalDeal.getCompanyRole().getOrganization().getDetail().getShortName());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRole().getOrganization().getDetail().getShortName());
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
        assertEquals(0, BigDecimal.valueOf(2).compareTo(physicalDeal.getDealDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getFixedPriceUnitOfMeasureCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getFixedPriceCurrencyCode());
    }

}
