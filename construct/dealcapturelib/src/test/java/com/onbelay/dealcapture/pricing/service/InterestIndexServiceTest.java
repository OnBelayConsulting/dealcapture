package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.core.query.enums.ExpressionConnector;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.enums.ExpressionOrder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.InterestRate;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.InterestCurve;
import com.onbelay.dealcapture.pricing.model.InterestIndex;
import com.onbelay.dealcapture.pricing.model.InterestIndexFixture;
import com.onbelay.dealcapture.pricing.repository.InterestCurveRepository;
import com.onbelay.dealcapture.pricing.repository.InterestIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InterestIndexServiceTest extends DealCaptureSpringTestCase {
    private InterestIndex monthlyInterestIndex;
    private InterestIndex dailyInterestIndex;

    private LocalDate fromCurveDate = LocalDate.of(2023, 1, 1);
    private LocalDate toCurveDate = LocalDate.of(2023, 3, 31);
    private LocalDateTime firstObserveDateTime = LocalDateTime.of(2023, 1, 1, 12, 59);
    private LocalDateTime secondObserveDateTime = LocalDateTime.of(2023, 1, 2, 12, 59);

    private BigDecimal firstBigDecimal = BigDecimal.valueOf(2.56);
    private BigDecimal secondBigDecimal = BigDecimal.valueOf(4.00);

    @Autowired
    private InterestIndexService interestIndexService;

    @Autowired
    private InterestIndexRepository interestIndexRepository;

    @Autowired
    private InterestCurveRepository interestCurveRepository;


    public void setUp() {
        super.setUp();

        monthlyInterestIndex = InterestIndexFixture.createInterestIndex(
                "INTM",
                FrequencyCode.MONTHLY);

        dailyInterestIndex = InterestIndexFixture.createInterestIndex(
                "INTD",
                true,
                FrequencyCode.DAILY);

        InterestIndexFixture.generateDailyInterestCurves(
                dailyInterestIndex,
                fromCurveDate,
                toCurveDate,
                firstBigDecimal,
                firstObserveDateTime);
        flush();
        InterestIndexFixture.generateDailyInterestCurves(
                dailyInterestIndex,
                fromCurveDate,
                toCurveDate,
                secondBigDecimal,
                secondObserveDateTime);


        InterestIndexFixture.generateMonthlyInterestCurves(
                monthlyInterestIndex,
                fromCurveDate,
                toCurveDate,
                BigDecimal.ONE,
                firstObserveDateTime);
        flush();
        InterestIndexFixture.generateMonthlyInterestCurves(
                monthlyInterestIndex,
                fromCurveDate,
                toCurveDate,
                BigDecimal.TEN,
                secondObserveDateTime);

        flush();
    }

    @Test
    public void fetchIndices() {
        InterestIndexSnapshot snapshot = interestIndexService.findInterestIndexByName("INTM");
        assertNotNull(snapshot);
        assertEquals(FrequencyCode.MONTHLY, snapshot.getDetail().getFrequencyCode());
    }

    @Test
    public void saveInterestIndex() {
        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIsRiskFreeRate(false);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        TransactionResult result = interestIndexService.save(snapshot);
        flush();

        InterestIndex index = interestIndexRepository.load(result.getEntityId());
        assertNotNull(index);
    }


    @Test
    public void changeIsRiskFreeRateOnInterestIndex() {
        List<InterestIndexSnapshot> snapshots = new ArrayList<>();

        InterestIndexSnapshot snapshot = interestIndexService.load(dailyInterestIndex.generateEntityId());
        snapshot.getDetail().setIsRiskFreeRate(false);
        snapshot.setEntityState(EntityState.MODIFIED);
        snapshots.add(snapshot);

        snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setName("AAAA");
        snapshot.getDetail().setIsRiskFreeRate(true);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshots.add(snapshot);

        TransactionResult result = interestIndexService.save(snapshots);
        flush();

        InterestIndex index = interestIndexRepository.findInterestIndexByName("AAAA");
        assertEquals(Boolean.TRUE, index.getDetail().getIsRiskFreeRate());
    }



    @Test
    public void saveInterestIndexFailDuplicateName() {
        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setName("INTD");
        snapshot.getDetail().setIsRiskFreeRate(false);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        try {
            interestIndexService.save(snapshot);
            fail("should have thrown exception.");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.DUPLICATE_INTEREST_INDEX_NAME.getCode(), e.getErrorCode());
            return;
        }

        fail("should have thrown exception.");
    }


    @Test
    public void saveInterestIndexFailDuplicateIsRiskFreeRate() {
        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setName("XXSS");
        snapshot.getDetail().setIsRiskFreeRate(true);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        try {
            interestIndexService.save(snapshot);
            fail("should have thrown exception.");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.INVALID_INTEREST_INDEX_IS_RISK_FREE_RATE.getCode(), e.getErrorCode());
            return;
        }

        fail("should have thrown exception.");
    }


    @Test
    public void updateInterestIndexFailDuplicateName() {
        InterestIndexSnapshot snapshot = interestIndexService.load(dailyInterestIndex.generateEntityId());
        snapshot.getDetail().setName("INTM");
        snapshot.setEntityState(EntityState.MODIFIED);

        try {
            interestIndexService.save(snapshot);
            fail("should have thrown exception.");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.DUPLICATE_INTEREST_INDEX_NAME.getCode(), e.getErrorCode());
            return;
        }

        fail("should have thrown exception.");
    }


    @Test
    public void updateInterestIndexFailDuplicateIsRiskFreeRate() {
        InterestIndexSnapshot snapshot = interestIndexService.load(monthlyInterestIndex.generateEntityId());
        snapshot.getDetail().setIsRiskFreeRate(true);
        snapshot.setEntityState(EntityState.MODIFIED);

        try {
            interestIndexService.save(snapshot);
            fail("should have thrown exception.");
        } catch (OBValidationException e) {
            assertEquals(PricingErrorCode.INVALID_INTEREST_INDEX_IS_RISK_FREE_RATE.getCode(), e.getErrorCode());
            return;
        }

        fail("should have thrown exception.");
    }


    @Test
    public void fetchCurrentInterestRate() {
       InterestRate rate =  interestIndexService.getCurrentInterestRate(LocalDate.of(2023, 3, 1));
       assertNotNull(rate);
    }


    @Test
    public void fetchCurrentInterestRateWhenDailyRateIsMissing() {
        InterestCurveSnapshot snapshot = new InterestCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2024, 2, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2024, 3, 1));
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        interestIndexService.saveInterestCurves(
                dailyInterestIndex.generateEntityId(),
                List.of(snapshot));
        flush();


        InterestRate rate =  interestIndexService.getCurrentInterestRate(LocalDate.of(2024, 3, 1));
        assertNotNull(rate);
        assertEquals(0, rate.getValue().compareTo(BigDecimal.valueOf(1.34)));
    }


    @Test
    public void fetchInterestCurves() {
        DefinedQuery definedQuery = new DefinedQuery("InterestCurve");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "interestIndexId",
                        ExpressionOperator.EQUALS,
                        dailyInterestIndex.getId()));
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

        QuerySelectedPage selectedPage = interestIndexService.findInterestCurveIds(definedQuery);
        List<InterestCurveSnapshot> curves = interestIndexService.fetchInterestCurvesByIds(selectedPage);
        assertEquals(62, curves.size());
    }


    @Test
    public void saveInterestCurves() {
        InterestCurveSnapshot snapshot = new InterestCurveSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setObservedDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
        snapshot.getDetail().setCurveValue(BigDecimal.valueOf(1.34));
        TransactionResult result = interestIndexService.saveInterestCurves(
                monthlyInterestIndex.generateEntityId(),
                List.of(snapshot));
        flush();
        assertEquals(1, result.getIds().size());

        InterestCurve curve = interestCurveRepository.load(result.getEntityId());
        assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
        assertEquals(LocalDateTime.of(2022, 1, 1, 12, 0), curve.getDetail().getObservedDateTime());
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(curve.getDetail().getCurveValue()));
        assertEquals(monthlyInterestIndex.getId(), curve.getInterestIndex().getId());
    }

}
