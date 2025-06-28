package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestTestCase;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.enums.JobTypeCode;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealPositionGenerationJobRunnerTest extends DealPositionRestTestCase {

    @Autowired
    private DealJobRunnerFactory dealJobRunnerFactory;

    @Autowired
    private DealJobService dealJobService;

    private BusinessContact contact;
    private PhysicalDeal physicalDeal;

    @Override
    public void setUp() {
        super.setUp();
        contact = BusinessContactFixture.createCompanyTrader("hans", "gruber", "gruber@terror.com");

        physicalDeal = DealFixture.createPricePhysicalDeal(
                contact,
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
        flush();
    }

    @Test
    public void runJob() throws Exception {

        DealJobSnapshot snapshot = new DealJobSnapshot();
        snapshot.getDetail().setJobTypeCode(JobTypeCode.DEAL_POS_GENERATION);
        snapshot.getDetail().setJobStatusCode(JobStatusCode.QUEUED);
        snapshot.getDetail().setDomainId(physicalDeal.getId());
        snapshot.getDetail().setCurrencyCode(CurrencyCode.CAD);
        snapshot.getDetail().setFromDate(fromMarketDate);
        snapshot.getDetail().setToDate(toMarketDate);
        TransactionResult result = dealJobService.save(snapshot);
        flush();
        DealJobRequestPublication publication = new DealJobRequestPublication(result.getId());

        DealJobRunner dealJobRunner = dealJobRunnerFactory.getRunner(JobTypeCode.DEAL_POS_GENERATION);

        dealJobRunner.execute(publication);
        flush();
        List<DealPositionSnapshot> snapshots = dealPositionService.findPositionsByDeal(physicalDeal.generateEntityId());
        assertTrue(snapshots.size() > 0);

    }


}
