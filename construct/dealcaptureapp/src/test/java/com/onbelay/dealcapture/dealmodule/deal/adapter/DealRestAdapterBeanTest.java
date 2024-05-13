package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithMockUser
public class DealRestAdapterBeanTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private DealRestAdapter dealRestAdapter;

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
    public void createDeal() {
        PhysicalDealSnapshot dealSnapshot = DealFixture.createPhysicalDealSnapshot(
                CommodityCode.CRUDE,
                BuySellCode.SELL,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31),
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "mydeal",
                companyRole,
                counterpartyRole,
                priceIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.GJ));

        TransactionResult result = dealRestAdapter.save(dealSnapshot);
        flush();
        PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
        assertEquals("OnBelay", physicalDeal.getCompanyRole().getOrganization().getDetail().getShortName());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRole().getOrganization().getDetail().getShortName());
        assertEquals(CommodityCode.CRUDE, physicalDeal.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.SELL, physicalDeal.getDealDetail().getBuySell());
        assertEquals("mydeal", physicalDeal.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2023, 1, 1), physicalDeal.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2023, 1, 31), physicalDeal.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(physicalDeal.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getVolumeUnitOfMeasure());
        assertEquals(FrequencyCode.DAILY, physicalDeal.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getSettlementCurrencyCode());

        assertEquals(priceIndex.getId(), physicalDeal.getMarketPriceIndex().getId());

        assertEquals(0, BigDecimal.valueOf(1).compareTo(physicalDeal.getDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDetail().getFixedPriceUnitOfMeasure());
        assertEquals(CurrencyCode.USD, physicalDeal.getDetail().getFixedPriceCurrencyCode());

    }

    @Test
    public void createIndexPhysicalDeal() {
        PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

        dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
        dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

        dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
        dealSnapshot.getDealDetail().setCommodityCode(CommodityCode.NATGAS);
        dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
        dealSnapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
        dealSnapshot.getDealDetail().setStartDate(startDate);
        dealSnapshot.getDealDetail().setEndDate(endDate);
        dealSnapshot.getDealDetail().setTicketNo("GH-334");

        dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());

        dealSnapshot.getDealDetail().setVolume(
                new Quantity(
                        BigDecimal.valueOf(34.78),
                        UnitOfMeasureCode.GJ));
        dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);
        dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX_PLUS);
        dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

        dealSnapshot.getDetail().setFixedPrice(new Price(
                BigDecimal.ONE,
                CurrencyCode.USD,
                UnitOfMeasureCode.GJ));

        dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

        TransactionResult result = dealRestAdapter.save(dealSnapshot);
        flush();
        PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
        assertEquals("OnBelay", physicalDeal.getCompanyRole().getOrganization().getDetail().getShortName());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRole().getOrganization().getDetail().getShortName());
        assertEquals(CommodityCode.NATGAS, physicalDeal.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.SELL, physicalDeal.getDealDetail().getBuySell());
        assertEquals("GH-334", physicalDeal.getDealDetail().getTicketNo());
        assertEquals(startDate, physicalDeal.getDealDetail().getStartDate());
        assertEquals(endDate, physicalDeal.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(34.78).compareTo(physicalDeal.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getVolumeUnitOfMeasure());
        assertEquals(FrequencyCode.DAILY, physicalDeal.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.USD, physicalDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getSettlementCurrencyCode());

        assertEquals(dealPriceIndex.getId(), physicalDeal.getDealPriceIndex().getId());
        assertEquals(priceIndex.getId(), physicalDeal.getMarketPriceIndex().getId());

        assertEquals(0, BigDecimal.valueOf(1).compareTo(physicalDeal.getDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDetail().getFixedPriceUnitOfMeasure());
        assertEquals(CurrencyCode.USD, physicalDeal.getDetail().getFixedPriceCurrencyCode());

    }


    @Test
    public void createIndexPhysicalDealUsingCSV() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/dealupload.csv");

        TransactionResult result = dealRestAdapter.saveFile("test.csv", inputStream.readAllBytes());
        flush();
        PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
        assertEquals("OnBelay", physicalDeal.getCompanyRole().getOrganization().getDetail().getShortName());
        assertEquals("OnBelay", physicalDeal.getCounterpartyRole().getOrganization().getDetail().getShortName());
        assertEquals(CommodityCode.NATGAS, physicalDeal.getDealDetail().getCommodityCode());
        assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
        assertEquals(BuySellCode.BUY, physicalDeal.getDealDetail().getBuySell());
        assertEquals("gh_87", physicalDeal.getDealDetail().getTicketNo());
        assertEquals(LocalDate.of(2024, 1, 1), physicalDeal.getDealDetail().getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), physicalDeal.getDealDetail().getEndDate());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(physicalDeal.getDealDetail().getVolumeQuantity()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDealDetail().getVolumeUnitOfMeasure());
        assertEquals(FrequencyCode.DAILY, physicalDeal.getDealDetail().getVolumeFrequencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getReportingCurrencyCode());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getSettlementCurrencyCode());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(physicalDeal.getDetail().getFixedPriceValue()));
        assertEquals(UnitOfMeasureCode.GJ, physicalDeal.getDetail().getFixedPriceUnitOfMeasure());
        assertEquals(CurrencyCode.CAD, physicalDeal.getDetail().getFixedPriceCurrencyCode());
    }

}
