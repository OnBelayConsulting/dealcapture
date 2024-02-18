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
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
@WithMockUser
public class DealRestAdapterBeanTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private DealRestAdapter dealRestAdapter;

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

        priceIndex = PriceIndexFixture.createPriceIndex(
                "AECO",
                PricingLocationFixture.createPricingLocation(
                        "west"));

        flush();
    }

    @Test
    public void createDeal() {
        PhysicalDealSnapshot dealSnapshot = DealFixture.createFixedPriceMarketIndexPhysicalDealSnapshot(
                CommodityCode.CRUDE,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31),
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "mydeal",
                companyRole,
                counterpartyRole,
                priceIndex,
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.GJ));

        TransactionResult result = dealRestAdapter.save(dealSnapshot);
        flush();
        PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());

    }

    @Test
    public void createIndexPhysicalDeal() {
        PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

        dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
        dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

        dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
        dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
        dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
        dealSnapshot.getDealDetail().setStartDate(startDate);
        dealSnapshot.getDealDetail().setEndDate(endDate);
        dealSnapshot.getDealDetail().setTicketNo("GH-334");

        dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());

        dealSnapshot.getDealDetail().setVolume(
                new Quantity(
                        BigDecimal.valueOf(34.78),
                        UnitOfMeasureCode.GJ));

        dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX_PLUS);

        dealSnapshot.getDetail().setFixedPrice(new Price(
                BigDecimal.ONE,
                CurrencyCode.USD,
                UnitOfMeasureCode.GJ));

        dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

    }

}
