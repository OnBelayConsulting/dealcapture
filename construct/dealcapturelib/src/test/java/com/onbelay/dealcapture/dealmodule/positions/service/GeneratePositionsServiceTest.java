package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealRepositoryBean;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private DealRepositoryBean dealRepository;

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private PricingLocation location;
    private PriceIndex priceIndex;
    private PriceIndex secondPriceIndex;

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;

    private PhysicalDeal physicalDealWithFixedDealPrice;
    private PhysicalDeal physicalDealWithIndexDealPrice;

    private FxIndex fxIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

    @Override
    public void setUp() {
        super.setUp();
        companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
        counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
        location = PricingLocationFixture.createPricingLocation("West");
        fxIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.MONTHLY,
                CurrencyCode.USD,
                CurrencyCode.CAD);

        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                location);

        secondPriceIndex = PriceIndexFixture.createPriceIndex(
                "ADFS",
                FrequencyCode.MONTHLY,
                location);


        physicalDealWithFixedDealPrice = DealFixture.createFixedPricePhysicalDeal(
                "5566",
                companyRole,
                counterpartyRole,
                priceIndex,
                fromMarketDate,
                toMarketDate,
                CurrencyCode.CAD,
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.GJ)
                );

        physicalDealWithIndexDealPrice = DealFixture.createIndexedPricePhysicalDeal(
                "5568",
                companyRole,
                counterpartyRole,
                priceIndex,
                fromMarketDate,
                toMarketDate,
                CurrencyCode.CAD,
                secondPriceIndex);
    }

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                context,
                physicalDealWithFixedDealPrice.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findByDeal(
                physicalDealWithFixedDealPrice.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(physicalDealWithFixedDealPrice.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(physicalDealWithFixedDealPrice.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());
        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                physicalDealWithFixedDealPrice.getDetail().getDealPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getDealPriceValue()));
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                context,
                physicalDealWithIndexDealPrice.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findByDeal(
                physicalDealWithIndexDealPrice.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(physicalDealWithIndexDealPrice.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

    }

}
