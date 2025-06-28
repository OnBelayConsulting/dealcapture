package com.onbelay.dealcapture.job.service;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.job.model.DealJobFixture;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealJobServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private DealJobService dealJobService;

    private LocalDate fromDate = LocalDate.of(2025, 1, 1);
    private LocalDate toDate = LocalDate.of(2025, 5, 31);
    private LocalDateTime createdDateTime = LocalDateTime.of(2025, 1, 1, 1, 1);
    private LocalDateTime valuationDateTime = LocalDateTime.of(2025, 1, 1, 1, 1);

    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testCreateDealJob() {
        DealJobSnapshot snapshot = DealJobFixture.createDealJobSnapshot(
                createdDateTime,
                valuationDateTime,
                CurrencyCode.CAD,
                fromDate,
                toDate);
        TransactionResult result = dealJobService.save(snapshot);
        flush();
        DealJobSnapshot created = (DealJobSnapshot) dealJobService.load(result.getEntityId());
        assertNotNull(created);
    }

    @Test
    public void testCreatePositionGenerationJob() {
        DealJobSnapshot snapshot = DealJobFixture.createDealJobSnapshot(
                "WHERE ",
                "pog",
                createdDateTime,
                CurrencyCode.CAD,
                fromDate,
                toDate);
        TransactionResult result = dealJobService.save(snapshot);
        flush();
        DealJobSnapshot created =  dealJobService.load(result.getEntityId());
        assertNotNull(created);
    }
}
