package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealRepositoryBean;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DealPositionServiceValuePositionsTest extends DealCaptureSpringTestCase {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DealRepositoryBean dealRepository;

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

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
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                CurrencyCode.USD);

        FxIndexFixture.generateDailyFxCurves(
                fxIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                LocalDateTime.of(2023, 1, 1, 0, 1));

        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.MMBTU,
                location);

        PriceIndexFixture.generateDailyPriceCurves(
                priceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.23),
                LocalDateTime.now());

        secondPriceIndex = PriceIndexFixture.createPriceIndex(
                "ADFS",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);


        physicalDealWithFixedDealPrice = DealFixture.createFixedPricePhysicalDeal(
                CommodityCode.CRUDE,
                "5566",
                companyRole,
                counterpartyRole,
                priceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(100),
                UnitOfMeasureCode.GJ,
                CurrencyCode.USD,
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.MMBTU)
        );

        physicalDealWithIndexDealPrice = DealFixture.createIndexedPricePhysicalDeal(
                CommodityCode.CRUDE,
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
    public void valuePositions() {

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                physicalDealWithFixedDealPrice.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        dealPositionService.valuePositions(
                physicalDealWithFixedDealPrice.generateEntityId(),
                LocalDateTime.now());

        List<DealPosition> positions = dealPositionRepository.findByDeal(physicalDealWithFixedDealPrice.generateEntityId());
        PhysicalPosition physicalPosition = (PhysicalPosition) positions.get(0);
        logger.error(physicalPosition.getDealPrice().toFormula());
        assertEquals(0, BigDecimal.valueOf(2.110112).compareTo(physicalPosition.getDealPrice().getValue()));
        logger.error("Market Price: " + physicalPosition.getMarketIndexPrice().toFormula());
        logger.error("MtM: " + physicalPosition.getDealPositionDetail().getMarkToMarketValuation());
        assertEquals(0, BigDecimal.valueOf(81.24).compareTo(physicalPosition.getDealPositionDetail().getMarkToMarketValuation()));
    }

}
