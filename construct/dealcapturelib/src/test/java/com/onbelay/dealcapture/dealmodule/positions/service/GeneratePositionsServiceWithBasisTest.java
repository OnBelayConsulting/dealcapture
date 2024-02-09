package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealRepositoryBean;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithBasisTest extends DealCaptureSpringTestCase {

    @Autowired
    private DealRepositoryBean dealRepository;

    @Autowired
    private DealService dealService;

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private PricingLocation location;
    private PriceIndex hubPriceIndex;
    private PriceIndex basisPriceIndex;
    private PriceIndex basisToBasisPriceIndex;

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
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                CurrencyCode.USD);

        hubPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        basisPriceIndex = PriceIndexFixture.createBasisPriceIndex(
                hubPriceIndex,
                "B_ADFS",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        basisToBasisPriceIndex = PriceIndexFixture.createBasisPriceIndex(
                basisPriceIndex,
                "B_VVDD",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);


        physicalDealWithFixedDealPrice = DealFixture.createFixedPricePhysicalDeal(
                CommodityCode.CRUDE,
                "5566",
                companyRole,
                counterpartyRole,
                basisToBasisPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(100),
                UnitOfMeasureCode.GJ,
                CurrencyCode.CAD,
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.GJ)
                );

        physicalDealWithIndexDealPrice = DealFixture.createIndexedPricePhysicalDeal(
                CommodityCode.CRUDE,
                "5568",
                companyRole,
                counterpartyRole,
                hubPriceIndex,
                fromMarketDate,
                toMarketDate,
                CurrencyCode.CAD,
                basisPriceIndex);
    }

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);
        flush();
        dealService.updateDealPositionGenerationStatusToPending(List.of(physicalDealWithFixedDealPrice.getId()));

        generatePositionsService.generatePositions(
                "test",
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
        assertEquals(2, positionSnapshot.getMarketPriceMappings().size());

    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        dealService.updateDealPositionGenerationStatusToPending(List.of(physicalDealWithIndexDealPrice.getId()));


        generatePositionsService.generatePositions(
                "test",
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
        assertEquals(1, positionSnapshot.getDealPriceMappings().size());

    }

}
