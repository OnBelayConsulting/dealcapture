package com.onbelay.dealcapture.dealmodule.deal.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.DealFileReader;
import com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader.DealOverrideFileReader;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;
import com.onbelay.dealcapture.job.enums.JobActionCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.enums.JobTypeCode;
import com.onbelay.dealcapture.job.publish.publisher.DealJobRequestPublisher;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DealRestAdapterBean extends BaseRestAdapterBean implements DealRestAdapter {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DealService dealService;

    @Autowired
    private DealJobService dealJobService;

    @Autowired
    private DealJobRequestPublisher dealJobRequestPublisher;

    @Override
    public DealSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("BaseDeal");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("BaseDeal", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("BaseDeal");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        QuerySelectedPage allIds = dealService.findDealIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new DealSnapshotCollection(
                    start,
                    limit,
                    allIds.getIds().size());
        }

        int toIndex = start + limit;

        if (toIndex > allIds.getIds().size())
            toIndex =  allIds.getIds().size();
        int fromIndex = start;

        List<Integer> selected = allIds.getIds().subList(fromIndex, toIndex);
        QuerySelectedPage limitedPageSelection = new QuerySelectedPage(
                selected,
                allIds.getOrderByClause());

        List<BaseDealSnapshot> snapshots = dealService.findByIds(limitedPageSelection);
        return new DealSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public TransactionResult save(BaseDealSnapshot dealSnapshot) {
        initializeSession();
        return dealService.save(dealSnapshot);
    }

    @Override
    public TransactionResult saveDealOverridesByMonth(
            EntityId dealId,
            DealOverrideMonthSnapshot snapshot) {
        initializeSession();
        return dealService.saveDealOverridesByMonth(
                dealId,
                snapshot);
    }

    @Override
    public TransactionResult saveHourlyDealOverrides(
            EntityId dealId,
            DealOverrideHoursForDaySnapshot snapshot) {
        initializeSession();
        return dealService.saveHourlyDealOverrides(
                dealId,
                snapshot);
    }

    @Override
    public TransactionResult save(List<BaseDealSnapshot> snapshots) {
        initializeSession();
        return dealService.save(snapshots);
    }

    @Override
    public BaseDealSnapshot load(EntityId dealId) {
        initializeSession();
        return dealService.load(dealId);
    }

    @Override
    public DealOverrideMonthSnapshot getDealOverridesForMonth(
            EntityId dealId,
            LocalDate overrideDate) {
        initializeSession();

        LocalDate monthDate = overrideDate.withDayOfMonth(1);
        DealOverrideSnapshot overrideSnapshot = dealService.fetchDealOverrides(dealId);
        Optional<DealOverrideMonthSnapshot> search = overrideSnapshot.getOverrideMonths()
                .stream()
                .filter(c-> c.getMonthDate().equals(monthDate)).findFirst();
        return search.orElseGet(() -> new DealOverrideMonthSnapshot(DealErrorCode.INVALID_DEAL_ID.getCode()));
    }

    @Override
    public DealOverrideHoursForDaySnapshot getHourlyDealOverrides(
            EntityId dealId,
            LocalDate dayDate) {
        initializeSession();
        return dealService.fetchHourlyDealOverrides(
                dealId,
                dayDate);
    }

    @Override
    public DealOverrideSnapshotCollection fetchDealOverrides(
            EntityId dealId,
            Integer start,
            Integer limit) {

        initializeSession();
        DealOverrideSnapshot overrideSnapshot = dealService.fetchDealOverrides(dealId);
        List<DealOverrideDaySnapshot> snapshots = new ArrayList<>();
        for (DealOverrideMonthSnapshot month : overrideSnapshot.getOverrideMonths()) {
            snapshots.addAll(month.getOverrideDays());
        }


        int toIndex = start + limit;

        if (toIndex > snapshots.size())
            toIndex =  snapshots.size();
        int fromIndex = start;

        List<DealOverrideDaySnapshot> selected = snapshots.subList(fromIndex, toIndex);


        DealOverrideSnapshotCollection collection = new DealOverrideSnapshotCollection(
                start,
                limit,
                selected.size(),
                selected);

        collection.setStartDate(overrideSnapshot.getStartDate());
        collection.setEndDate(overrideSnapshot.getEndDate());
        collection.setHeadings(overrideSnapshot.getHeadings());
        return collection;
    }

    @Override
    public TransactionResult saveDealCosts(
            Integer dealId,
            List<DealCostSnapshot> dealCostSnapshots) {

        initializeSession();
        return dealService.saveDealCosts(
                new EntityId(dealId),
                dealCostSnapshots);
    }

    @Override
    public TransactionResult saveDealCost(DealCostSnapshot dealCostSnapshot) {
        initializeSession();

        return dealService.saveDealCost(
                new EntityId(dealCostSnapshot.getDealId()),
                dealCostSnapshot);
    }

    @Override
    public DealCostSnapshotCollection fetchDealCosts(Integer dealId) {

        initializeSession();

        List<DealCostSnapshot> snapshots = dealService.fetchDealCosts(new EntityId(dealId));
        return new DealCostSnapshotCollection(
                0,
                100,
                snapshots.size(),
                snapshots);
    }

    @Override
    public DealCostSnapshot loadDealCost(EntityId dealCostId) {
        initializeSession();

        return dealService.loadDealCost(dealCostId);
    }

    @Override
    public MarkToMarketResult queueMarkToMarketJobs(MarkToMarketJobRequest request) {
        initializeSession();

        LocalDateTime createdDateTime = request.getCreatedDateTime();
        if (createdDateTime == null) {
            createdDateTime = LocalDateTime.now();
        }

        DealJobSnapshot powerProfilePositionGenerationJobSnapshot = new DealJobSnapshot();
        powerProfilePositionGenerationJobSnapshot.getDetail().setJobTypeCode(JobTypeCode.PWR_PROFILE_POS_GENERATION);
        powerProfilePositionGenerationJobSnapshot.getDetail().setQueryText(request.getPowerProfileQueryText());
        powerProfilePositionGenerationJobSnapshot.getDetail().setCreatedDateTime(createdDateTime);
        powerProfilePositionGenerationJobSnapshot.getDetail().setCurrencyCode(request.getCurrencyCode());
        powerProfilePositionGenerationJobSnapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);
        powerProfilePositionGenerationJobSnapshot.getDetail().setFromDate(request.getFromDate());
        powerProfilePositionGenerationJobSnapshot.getDetail().setToDate(request.getToDate());

        TransactionResult result = dealJobService.save(powerProfilePositionGenerationJobSnapshot);
        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);
        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));

        DealJobSnapshot dealPositionGenerationJobSnapshot = new DealJobSnapshot();
        dealPositionGenerationJobSnapshot.setDependsOnId(result.getEntityId());
        dealPositionGenerationJobSnapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        dealPositionGenerationJobSnapshot.getDetail().setQueryText(request.getDealQueryText());
        dealPositionGenerationJobSnapshot.getDetail().setCreatedDateTime(createdDateTime);
        dealPositionGenerationJobSnapshot.getDetail().setCurrencyCode(request.getCurrencyCode());
        dealPositionGenerationJobSnapshot.getDetail().setFromDate(request.getFromDate());
        dealPositionGenerationJobSnapshot.getDetail().setToDate(request.getToDate());

        result = dealJobService.save(dealPositionGenerationJobSnapshot);
        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);
        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));

        DealJobSnapshot priceRiskFactorValuationJobSnapshot = new DealJobSnapshot();
        priceRiskFactorValuationJobSnapshot.getDetail().setJobTypeCode(JobTypeCode.PRICE_RF_VALUATION);
        priceRiskFactorValuationJobSnapshot.setDependsOnId(result.getEntityId());
        priceRiskFactorValuationJobSnapshot.getDetail().setQueryText(request.getPriceIndexQueryText());
        priceRiskFactorValuationJobSnapshot.getDetail().setCreatedDateTime(createdDateTime);
        priceRiskFactorValuationJobSnapshot.getDetail().setCurrencyCode(request.getCurrencyCode());

        result = dealJobService.save(priceRiskFactorValuationJobSnapshot);
        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);
        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));


        DealJobSnapshot powerProfilePositionValuationJobSnapshot = new DealJobSnapshot();
        powerProfilePositionValuationJobSnapshot.getDetail().setJobTypeCode(JobTypeCode.PWR_PROFILE_POS_VALUATION);
        powerProfilePositionValuationJobSnapshot.setDependsOnId(result.getEntityId());
        powerProfilePositionValuationJobSnapshot.getDetail().setQueryText(request.getPowerProfileQueryText());
        powerProfilePositionValuationJobSnapshot.getDetail().setCreatedDateTime(createdDateTime);
        powerProfilePositionValuationJobSnapshot.getDetail().setCurrencyCode(request.getCurrencyCode());
        powerProfilePositionValuationJobSnapshot.getDetail().setFromDate(request.getFromDate());
        powerProfilePositionValuationJobSnapshot.getDetail().setToDate(request.getToDate());

        result = dealJobService.save(powerProfilePositionValuationJobSnapshot);
        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);
        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));


        DealJobSnapshot dealPositionValuationSnapshot = new DealJobSnapshot();
        dealPositionValuationSnapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_VALUATION);
        dealPositionValuationSnapshot.setDependsOnId(result.getEntityId());
        dealPositionValuationSnapshot.getDetail().setQueryText(request.getDealQueryText());
        dealPositionValuationSnapshot.getDetail().setCreatedDateTime(createdDateTime);
        dealPositionValuationSnapshot.getDetail().setCurrencyCode(request.getCurrencyCode());
        dealPositionValuationSnapshot.getDetail().setFromDate(request.getFromDate());
        dealPositionValuationSnapshot.getDetail().setToDate(request.getToDate());

        result = dealJobService.save(dealPositionValuationSnapshot);
        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);
        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));

        return new MarkToMarketResult(createdDateTime);
    }


    @Override
    public TransactionResult saveDealFile(String originalFileName, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        DealFileReader dealFileReader = new DealFileReader(fileStream);

        dealFileReader.readContents();
        return dealService.save(dealFileReader.getDealSnapshots());
    }

    @Override
    public TransactionResult saveDealOverridesFile(String originalFileName, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        DealOverrideFileReader fileReader = new DealOverrideFileReader(fileStream);

        fileReader.readContents();
        if (fileReader.isHourly()) {
            return dealService.saveHourlyDealOverrides(fileReader.getHourlyDealOverrideSnapshots());
        } else {
            return dealService.saveDealOverrides(fileReader.getDealOverrideSnapshots());
        }
    }
}
