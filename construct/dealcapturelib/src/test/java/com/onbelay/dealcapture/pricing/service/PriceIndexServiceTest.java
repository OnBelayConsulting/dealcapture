package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.core.query.enums.ExpressionConnector;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.enums.ExpressionOrder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.enums.BenchSettlementRuleCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PriceIndexServiceTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex monthlyPriceIndex;
    private PriceIndex priceDailyIndex;

    private LocalDate fromCurveDate = LocalDate.of(2023, 1, 1);
    private LocalDate toCurveDate = LocalDate.of(2023, 3, 31);
    private LocalDateTime firstObserveDateTime = LocalDateTime.of(2023, 1, 1, 12, 59);
    private LocalDateTime secondObserveDateTime = LocalDateTime.of(2023, 1, 2, 12, 59);

    private BigDecimal firstBigDecimal = BigDecimal.valueOf(2.56);
    private BigDecimal secondBigDecimal = BigDecimal.valueOf(4.00);

    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceCurveRepository priceCurveRepository;


    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        monthlyPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        priceDailyIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceIndexFixture.generateDailyPriceCurves(
                priceDailyIndex,
                fromCurveDate,
                toCurveDate,
                firstBigDecimal,
                firstObserveDateTime);
        flush();
        PriceIndexFixture.generateDailyPriceCurves(
                priceDailyIndex,
                fromCurveDate,
                toCurveDate,
                secondBigDecimal,
                secondObserveDateTime);


        PriceIndexFixture.generateMonthlyPriceCurves(
                priceDailyIndex,
                fromCurveDate,
                toCurveDate,
                BigDecimal.ONE,
                firstObserveDateTime);
        flush();
        PriceIndexFixture.generateMonthlyPriceCurves(
                priceDailyIndex,
                fromCurveDate,
                toCurveDate,
                BigDecimal.TEN,
                secondObserveDateTime);

        flush();
    }

    @Test
    public void fetchIndices() {
        PriceIndexSnapshot snapshot = priceIndexService.findPriceIndexByName("ACEE");
        assertNotNull(snapshot);
        assertEquals(FrequencyCode.MONTHLY, snapshot.getDetail().getFrequencyCode());
    }

    @Test
    public void fetchPriceIndexReports() {
        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(new DefinedQuery("PriceIndex"));
        List<PriceIndexReport> reports = priceIndexService.fetchPriceIndexReports(selectedPage);
        assertEquals(2, reports.size());

        PriceIndexReport firstReport = reports.stream().filter(c-> c.getId().equals(priceDailyIndex.getId())).findFirst().get();
        assertNotNull(firstReport);
        PriceIndexReport secondReport = reports.stream().filter(c-> c.getId().equals(monthlyPriceIndex.getId())).findFirst().get();
        assertNotNull(secondReport);

    }

    @Test
    public void saveHubIndex() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.setPricingLocationId(location.generateEntityId());
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setBenchSettlementRuleCode(BenchSettlementRuleCode.NEVER);

        TransactionResult result = priceIndexService.save(snapshot);
        flush();

        PriceIndex index = priceIndexRepository.load(result.getEntityId());
        assertNotNull(index);
        assertEquals(BenchSettlementRuleCode.NEVER, index.getDetail().getBenchSettlementRuleCode());
    }


    @Test
    public void saveBasisIndexFailMissingLocation() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.BASIS);
        snapshot.setBaseIndexId(priceDailyIndex.generateEntityId());

        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setBenchSettlementRuleCode(BenchSettlementRuleCode.NEVER);

        try {
            TransactionResult result = priceIndexService.save(snapshot);
            fail("should have thrown exception");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.MISSING_PRICING_LOCATION.getCode(), e.getErrorCode());
            return;
        }
        fail("should have thrown exception");
    }


    @Test
    public void saveBasisIndexFailMissingHub() {
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.setPricingLocationId(location.generateEntityId());
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIndexType(IndexType.BASIS);

        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setBenchSettlementRuleCode(BenchSettlementRuleCode.NEVER);

        try {
            TransactionResult result = priceIndexService.save(snapshot);
            fail("should have thrown exception");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.MISSING_BASE_INDEX.getCode(), e.getErrorCode());

            return;
        }
        fail("should have thrown exception");
    }

    @Test
    public void fetchPrices() {
        DefinedQuery definedQuery = new DefinedQuery("PriceCurve");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "priceIndexId",
                        ExpressionOperator.EQUALS,
                        priceDailyIndex.getId()));
        definedQuery.getWhereClause().addConnector(ExpressionConnector.AND);
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "curveDate",
                        ExpressionOperator.GREATER_THAN_OR_EQUALS,
                        LocalDate.of(2023, 1, 1)));
        definedQuery.getWhereClause().addConnector(ExpressionConnector.AND);
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "frequencyCode",
                        ExpressionOperator.EQUALS,
                        FrequencyCode.DAILY.getCode()));
        definedQuery.getWhereClause().addConnector(ExpressionConnector.AND);
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "curveDate",
                        ExpressionOperator.LESS_THAN_OR_EQUALS,
                        LocalDate.of(2023, 1, 31)));
        definedQuery.getOrderByClause().addOrderExpression(
                new DefinedOrderExpression("curveDate", ExpressionOrder.ASCENDING));
        definedQuery.getOrderByClause().addOrderExpression(
                new DefinedOrderExpression("observedDateTime", ExpressionOrder.ASCENDING));

        QuerySelectedPage selectedPage = priceIndexService.findPriceCurveIds(definedQuery);
        List<PriceCurveSnapshot> curves = priceIndexService.fetchPriceCurvesByIds(selectedPage);
        assertEquals(62, curves.size());
    }

    @Test
    public void fetchPriceCurveReports() {
        DefinedQuery definedQuery = new DefinedQuery("PriceIndex");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression("name", ExpressionOperator.EQUALS, priceDailyIndex.getDetail().getName()));

        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(definedQuery);
        List<CurveReport> reports = priceIndexService.fetchPriceCurveReports(
                selectedPage,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31),
                firstObserveDateTime);

        assertEquals(32, reports.size());
        CurveReport report = reports.get(0);
        assertEquals(0, firstBigDecimal.compareTo(report.getValue()));

        CurveReport monthlyReport = reports.get(31);
        assertEquals(FrequencyCode.MONTHLY, monthlyReport.getFrequencyCode());

    }

    @Test
    public void fetchPriceCurveReportsLatest() {
        DefinedQuery definedQuery = new DefinedQuery("PriceIndex");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression("name", ExpressionOperator.EQUALS, priceDailyIndex.getDetail().getName()));

        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(definedQuery);
        List<CurveReport> reports = priceIndexService.fetchPriceCurveReports(
                selectedPage,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31),
                LocalDateTime.now());

        assertEquals(32, reports.size());
        CurveReport report = reports.get(0);
        assertEquals(0, secondBigDecimal.compareTo(report.getValue()));

        CurveReport monthlyReport = reports.get(31);
        assertEquals(FrequencyCode.MONTHLY, monthlyReport.getFrequencyCode());

    }



    @Test
    public void savePriceCurves() {
        PriceCurveSnapshot snapshot = new PriceCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        TransactionResult result = priceIndexService.savePrices(
                monthlyPriceIndex.generateEntityId(),
                List.of(snapshot));
        flush();
        assertEquals(1, result.getIds().size());

        PriceCurve curve = priceCurveRepository.load(result.getEntityId());
        assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), curve.getDetail().getObservedDateTime());
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(curve.getDetail().getCurveValue()));
        assertEquals(monthlyPriceIndex.getId(), curve.getPriceIndex().getId());
    }

}
