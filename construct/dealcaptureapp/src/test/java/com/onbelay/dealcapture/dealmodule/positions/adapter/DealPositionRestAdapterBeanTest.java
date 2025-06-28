package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter.DealPositionColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter.PhysicalPositionColumnType;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FileReportResult;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WithMockUser
public class DealPositionRestAdapterBeanTest extends DealPositionRestTestCase {

    @Autowired
    private DealPositionRestAdapter dealPositionRestAdapter;

    @Autowired
    private ValuePositionsService valuePositionsService;


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
    public void fetchPositionViewsAsCSV() {
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

        valuePositionsService.valuePositions(
                List.of(physicalDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());

        flush();
        clearCache();
        String query = "WHERE ticketNo eq '" + physicalDeal.getDealDetail().getTicketNo() + "'";

        String positionQuery = query + " ORDER BY startDate";
        FileReportResult fileReportResult = dealPositionRestAdapter.findPositionsAsCSV(positionQuery, 0, 500);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileReportResult.getDocumentInBytes());
        ArrayList<String> list = new ArrayList<>(DealPositionColumnType.getAsList());
        list.addAll(PhysicalPositionColumnType.getAsList());
        String[] header =  list.toArray(new String[0]);

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            CSVParser parser = new CSVParser(
                    reader,
                    CSVFormat.EXCEL.builder()
                            .setHeader(header)
                            .setSkipHeaderRecord(true)
                            .build());


            CSVRecord record = parser.getRecords().get(0);
            String ticketNo = record.get(DealPositionColumnType.TICKET_NO.getCode());
            assertEquals("5566", ticketNo);

            assertEquals(DealTypeCode.PHYSICAL_DEAL.getCode(), record.get(DealPositionColumnType.DEAL_TYPE.getCode()));
            assertEquals("SELL", record.get(DealPositionColumnType.BUY_SELL.getCode()));
            assertEquals("2023-01-01", record.get(DealPositionColumnType.START_DATE.getCode()));
            assertEquals("2023-01-01", record.get(DealPositionColumnType.END_DATE.getCode()));
            assertEquals("D", record.get(DealPositionColumnType.FREQUENCY.getCode()));
            assertEquals("CAD", record.get(DealPositionColumnType.CURRENCY.getCode()));
            assertEquals("10.00",  record.get(DealPositionColumnType.VOL_QUANTITY.getCode()));
            assertEquals("GJ", record.get(DealPositionColumnType.VOL_UNIT_OF_MEASURE.getCode()));
            assertEquals("Daily",record.get(DealPositionColumnType.POWER_FLOW_CODE.getCode()));
            assertEquals("2023-01-01T01:00", record.get(DealPositionColumnType.CREATED_DATE_TIME.getCode()));
            assertNotNull(record.get(DealPositionColumnType.VALUED_DATE_TIME.getCode()));
            assertEquals("-90.00", record.get(DealPositionColumnType.MTM_AMT.getCode()));
            assertEquals("CAD", record.get(DealPositionColumnType.SETTLEMENT_CURRENCY.getCode()));
            assertEquals("0.00", record.get(DealPositionColumnType.COST_SETTLEMENT_AMT.getCode()));
            assertEquals("10.00", record.get(DealPositionColumnType.SETTLEMENT_AMT.getCode()));
            assertEquals("10.00", record.get(DealPositionColumnType.TOTAL_SETTLEMENT_AMT.getCode()));
            assertEquals("0", record.get(DealPositionColumnType.ERROR_CODE.getCode()));
            assertEquals("", record.get(DealPositionColumnType.ERROR_MSG.getCode()));
            assertEquals("Fixed", record.get(PhysicalPositionColumnType.DEAL_PRICE_VALUATION_CODE.getCode()));
            assertEquals("1.000", record.get(PhysicalPositionColumnType.DEAL_PRICE.getCode()));
            assertEquals("", record.get(PhysicalPositionColumnType.DEAL_INDEX_PRICE.getCode()));
            assertEquals("1.000", record.get(PhysicalPositionColumnType.TOTAL_DEAL_PRICE.getCode()));
            assertEquals("Index", record.get(PhysicalPositionColumnType.MARKET_VALUATION_CODE.getCode()));
            assertEquals("10.000", record.get(PhysicalPositionColumnType.MARKET_PRICE.getCode()));
            parser.close();
        } catch (IOException e) {
            throw new OBRuntimeException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
        }

    }

}
