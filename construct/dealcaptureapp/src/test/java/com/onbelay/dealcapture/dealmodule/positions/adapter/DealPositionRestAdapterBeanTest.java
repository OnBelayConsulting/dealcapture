package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
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
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WithMockUser
public class DealPositionRestAdapterBeanTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private DealPositionRestAdapter dealPositionRestAdapter;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private DealService dealService;

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private PricingLocation location;
    private PriceIndex priceIndex;
    private PriceRiskFactor priceRiskFactor;

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;

    private PhysicalDeal physicalDeal;

    private FxIndex fxIndex;

    private FxRiskFactor fxRiskFactor;

    private LocalDateTime createdDateTime = LocalDateTime.of(2023, 1, 1, 1, 0);
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
                CurrencyCode.USD,
                CurrencyCode.CAD);

        FxIndexFixture.generateDailyFxCurves(
                fxIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 11, 1, 1, 1));

        fxRiskFactor = FxRiskFactorFixture.createFxRiskFactor(fxIndex, fromMarketDate);

        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceIndexFixture.generateMonthlyPriceCurves(
                priceIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 10, 1, 0, 0));

        physicalDeal = DealFixture.createPricePhysicalDeal(
                CommodityCode.CRUDE,
                "5566",
                companyRole,
                counterpartyRole,
                priceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.TEN,
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

        flush();
    }

    @Test
    public void savePositions() {
        List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
                physicalDeal,
                CurrencyCode.CAD,
                createdDateTime,
                priceRiskFactor,
                fxRiskFactor);

        TransactionResult result = dealPositionRestAdapter.save(snapshots);

        DealPositionSnapshotCollection collection = dealPositionRestAdapter.find(
                "WHERE ticketNo eq " + physicalDeal.getDealDetail().getTicketNo(),
                0,
                100);
        assertEquals(31, collection.getCount());

    }

    @Test
    public void generatePositions() {
        EvaluationContextRequest request = new EvaluationContextRequest();
        request.setCurrencyCodeValue(CurrencyCode.CAD.getCode());
        request.setCreatedDateTime(createdDateTime);
        request.setFromDate(fromMarketDate);
        request.setToDate(toMarketDate);
        request.setQueryText( "WHERE dealId eq " + physicalDeal.getId());

        dealPositionRestAdapter.generatePositions(request);

        List<DealPositionSnapshot> snapshots = dealPositionService.findPositionsByDeal(physicalDeal.generateEntityId());
        assertTrue(snapshots.size() > 0);
    }

    @Test
    public void valuePositions() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(physicalDeal.getId()));
        DealPositionsEvaluationContext dealPositionsEvaluationContext = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate);


        generatePositionsService.generatePositions(
                "Test",
                dealPositionsEvaluationContext,
                List.of(physicalDeal.getId()));
        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        String query = "WHERE ticketNo eq '" + physicalDeal.getDealDetail().getTicketNo() + "'";
        EvaluationContextRequest request = new EvaluationContextRequest();
        request.setQueryText(query);
        request.setCurrencyCodeValue(CurrencyCode.CAD.getCode());
        request.setCreatedDateTime(createdDateTime);
        request.setFromDate(fromMarketDate);
        request.setToDate(toMarketDate);

        dealPositionRestAdapter.valuePositions(request);
        flush();
        clearCache();

        DealPositionSnapshotCollection collection = dealPositionRestAdapter.find(query, 0, 500);
        DealPositionSnapshot snapshot = collection.getSnapshots().get(0);
        assertNotNull(snapshot.getSettlementDetail().getMarkToMarketValuation());

    }

}
