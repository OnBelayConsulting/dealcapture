package com.onbelay.dealcapture.job.model;

import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.enums.JobTypeCode;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DealJobFixture {

    public static DealJob createDealPositionGenerationJob(
            String dealQuery,
            String positionGenerationId,
            LocalDateTime createdDateTime,
            CurrencyCode currencyCode,
            LocalDate fromDate,
            LocalDate toDate) {

        DealJobSnapshot snapshot = new DealJobSnapshot();
        snapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);
        snapshot.getDetail().setCreatedDateTime(createdDateTime);
        snapshot.getDetail().setCurrencyCodeValue(currencyCode.getCode());
        snapshot.getDetail().setFromDate(fromDate);
        snapshot.getDetail().setToDate(toDate);
        snapshot.getDetail().setQueryText(dealQuery);
        snapshot.getDetail().setPositionGenerationId(positionGenerationId);
        DealJob job = new DealJob();
        job.createWith(snapshot);
        return job;
    }


    public static DealJobSnapshot createDealJobSnapshot(
            String dealQuery,
            String positionGenerationId,
            LocalDateTime createdDateTime,
            CurrencyCode currencyCode,
            LocalDate fromDate,
            LocalDate toDate) {

        DealJobSnapshot snapshot = new DealJobSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        snapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);
        snapshot.getDetail().setCreatedDateTime(createdDateTime);
        snapshot.getDetail().setCurrencyCodeValue(currencyCode.getCode());
        snapshot.getDetail().setFromDate(fromDate);
        snapshot.getDetail().setToDate(toDate);
        snapshot.getDetail().setQueryText(dealQuery);
        snapshot.getDetail().setPositionGenerationId(positionGenerationId);
        return snapshot;
    }


    public static DealJob createDealJob(
            LocalDateTime createdDateTime,
            LocalDateTime valuationDateTime,
            CurrencyCode currencyCode,
            LocalDate fromDate,
            LocalDate toDate) {


        DealJobSnapshot snapshot = new DealJobSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        snapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);
        snapshot.getDetail().setCreatedDateTime(createdDateTime);
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setCurrencyCodeValue(currencyCode.getCode());
        snapshot.getDetail().setFromDate(fromDate);
        snapshot.getDetail().setToDate(toDate);
        snapshot.getDetail().setValuationDateTime(valuationDateTime);
        DealJob job = new DealJob();
        job.createWith(snapshot);
        return job;
    }


    public static DealJobSnapshot createDealJobSnapshot(
            LocalDateTime createdDateTime,
            LocalDateTime valuationDateTime,
            CurrencyCode currencyCode,
            LocalDate fromDate,
            LocalDate toDate) {

        DealJobSnapshot snapshot = new DealJobSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        snapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);

        snapshot.getDetail().setCreatedDateTime(createdDateTime);
        snapshot.getDetail().setCurrencyCodeValue(currencyCode.getCode());
        snapshot.getDetail().setFromDate(fromDate);
        snapshot.getDetail().setToDate(toDate);
        snapshot.getDetail().setValuationDateTime(valuationDateTime);
        return snapshot;
    }

}
