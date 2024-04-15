package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionConnector;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.enums.ExpressionOrder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceIndexServiceHourlyTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex priceHourlyIndex;

    private LocalDate fromCurveDate = LocalDate.of(2023, 1, 1);
    private LocalDate toCurveDate = LocalDate.of(2023, 1, 2);
    private LocalDateTime firstObserveDateTime = LocalDateTime.of(2023, 1, 1, 12, 59);
    private LocalDateTime secondObserveDateTime = LocalDateTime.of(2023, 1, 2, 12, 59);

    private BigDecimal firstBigDecimal = BigDecimal.valueOf(2.56);
    private BigDecimal secondBigDecimal = BigDecimal.valueOf(4.00);

    private BigDecimal thirdBigDecimal = BigDecimal.valueOf(6.00);


    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceCurveRepository priceCurveRepository;


    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");

        priceHourlyIndex = PriceIndexFixture.createPriceIndex(
                "MY_HOUR",
                FrequencyCode.HOURLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceIndexFixture.generateDailyPriceCurves(
                priceHourlyIndex,
                fromCurveDate,
                toCurveDate,
                firstBigDecimal,
                firstObserveDateTime);
        flush();
        PriceIndexFixture.generateHourlyPriceCurves(
                priceHourlyIndex,
                fromCurveDate,
                toCurveDate,
                secondBigDecimal,
                firstObserveDateTime);

        PriceIndexFixture.generateHourlyPriceCurves(
                priceHourlyIndex,
                fromCurveDate,
                toCurveDate,
                thirdBigDecimal,
                secondObserveDateTime);



        PriceIndexFixture.generateMonthlyPriceCurves(
                priceHourlyIndex,
                fromCurveDate,
                toCurveDate,
                BigDecimal.ONE,
                firstObserveDateTime);
        flush();

        flush();
    }

    @Test
    public void fetchPrices() {
        DefinedQuery definedQuery = new DefinedQuery("PriceCurve");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "priceIndexId",
                        ExpressionOperator.EQUALS,
                        priceHourlyIndex.getId()));
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
                        FrequencyCode.HOURLY.getCode()));
        definedQuery.getWhereClause().addConnector(ExpressionConnector.AND);
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "curveDate",
                        ExpressionOperator.LESS_THAN_OR_EQUALS,
                        LocalDate.of(2023, 1, 1)));
        definedQuery.getOrderByClause().addOrderExpression(
                new DefinedOrderExpression("curveDate", ExpressionOrder.ASCENDING));
        definedQuery.getOrderByClause().addOrderExpression(
                new DefinedOrderExpression("observedDateTime", ExpressionOrder.ASCENDING));

        QuerySelectedPage selectedPage = priceIndexService.findPriceCurveIds(definedQuery);
        List<PriceCurveSnapshot> curves = priceIndexService.fetchPriceCurvesByIds(selectedPage);
        assertEquals(48, curves.size());
    }

    @Test
    public void fetchPriceCurveReports() {
        DefinedQuery definedQuery = new DefinedQuery("PriceIndex");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression("name", ExpressionOperator.EQUALS, priceHourlyIndex.getDetail().getName()));

        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(definedQuery);
        List<CurveReport> reports = priceIndexService.fetchPriceCurveReports(
                selectedPage,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2),
                firstObserveDateTime);

        assertEquals(51, reports.size());
        List<CurveReport> hourlyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.HOURLY).toList();
        assertEquals(48, hourlyReports.size());

        List<CurveReport> dailyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.DAILY).toList();
        assertEquals(2, dailyReports.size());

        List<CurveReport> monthlyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.MONTHLY).toList();
        assertEquals(1, monthlyReports.size());
    }

    @Test
    public void fetchPriceCurveReportsLatest() {
        DefinedQuery definedQuery = new DefinedQuery("PriceIndex");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression("name", ExpressionOperator.EQUALS, priceHourlyIndex.getDetail().getName()));

        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(definedQuery);
        List<CurveReport> reports = priceIndexService.fetchPriceCurveReports(
                selectedPage,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31),
                LocalDateTime.now());

        assertEquals(51, reports.size());
        List<CurveReport> hourlyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.HOURLY).toList();
        assertEquals(48, hourlyReports.size());
        CurveReport firstReport = hourlyReports.get(0);
        assertEquals(0, thirdBigDecimal.compareTo(firstReport.getValue()));

        List<CurveReport> dailyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.DAILY).toList();
        assertEquals(2, dailyReports.size());

        List<CurveReport> monthlyReports = reports.stream().filter(c-> c.getFrequencyCode() == FrequencyCode.MONTHLY).toList();
        assertEquals(1, monthlyReports.size());
    }



    @Test
    public void savePriceCurves() {
        PriceCurveSnapshot snapshot = new PriceCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
        snapshot.getDetail().setHourEnding(2);
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        TransactionResult result = priceIndexService.savePrices(
                priceHourlyIndex.generateEntityId(),
                List.of(snapshot));
        flush();
        assertEquals(1, result.getIds().size());

        PriceCurve curve = priceCurveRepository.load(result.getEntityId());
        assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
        assertEquals(2, curve.getDetail().getHourEnding().intValue());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), curve.getDetail().getObservedDateTime());
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(curve.getDetail().getCurveValue()));
        assertEquals(priceHourlyIndex.getId(), curve.getPriceIndex().getId());
    }

}
