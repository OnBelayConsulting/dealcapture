package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealRepositoryBean;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealPositionServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private DealRepositoryBean dealRepository;

    @Autowired
    private DealPositionService dealPositionService;

    private PricingLocation location;
    private PriceIndex priceIndex;
    private PriceRiskFactor priceRiskFactor;

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;

    private PhysicalDeal physicalDeal;

    private FxIndex fxIndex;

    private FxRiskFactor fxRiskFactor;

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
                CurrencyCode.CAD,
                CurrencyCode.USD);

        fxRiskFactor = FxRiskFactorFixture.createFxRiskFactor(fxIndex, fromMarketDate);

        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        physicalDeal = DealFixture.createFixedPricePhysicalDeal(
                CommodityCode.CRUDE,
                "5566",
                companyRole,
                counterpartyRole,
                priceIndex,
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

        priceRiskFactor = PriceRiskFactorFixture.createPriceRiskFactor(
                priceIndex,
                fromMarketDate);

    }

    @Test
    public void createPositions() {
        List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
                physicalDeal,
                priceRiskFactor,
                fxRiskFactor);

        dealPositionService.saveDealPositions(
                "test",
                snapshots);

        flush();

        List<DealPositionSnapshot> snapshots1 = dealPositionService.findByDeal(
                physicalDeal.generateEntityId());

        assertTrue(snapshots1.size() > 0);

    }



    @Test
    public void fetchPhysicalPositionReports() {
        List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
                physicalDeal,
                priceRiskFactor,
                fxRiskFactor);

        dealPositionService.saveDealPositions(
                "test",
                snapshots);

        flush();

        List<PhysicalPositionReport> reports = dealPositionService.findPhysicalPositionReportsByDeal(
                physicalDeal.generateEntityId());

        assertTrue(reports.size() > 0);

    }

}
