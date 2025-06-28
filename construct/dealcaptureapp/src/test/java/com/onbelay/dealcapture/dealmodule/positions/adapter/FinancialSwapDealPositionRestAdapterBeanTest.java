package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter.DealPositionColumnType;
import com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter.FinancialSwapPositionColumnType;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FileReportResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.shared.enums.*;
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
public class FinancialSwapDealPositionRestAdapterBeanTest extends DealPositionRestTestCase {

    @Autowired
    private DealPositionRestAdapter dealPositionRestAdapter;

    @Autowired
    private DealRepository dealRepository;

    protected PriceIndex receivesIndex;
    protected PriceIndex paysIndex;

    protected FinancialSwapDeal fixed4FloatSellDeal;
    protected FinancialSwapDeal fixed4FloatBuyDeal;
    protected FinancialSwapDeal float4FloatBuyDeal;
    protected FinancialSwapDeal float4FloatPlusBuyDeal;
    protected FinancialSwapDeal fixed4PowerProfileBuyDeal;

    protected PriceIndex settledHourlyIndex;
    protected PriceIndex onPeakDailyIndex;
    protected PriceIndex offPeakDailyIndex;

    protected PowerProfile powerProfile;

    @Autowired
    private ValuePositionsService valuePositionsService;

    @Override
    public void setUp() {
        super.setUp();



        settledHourlyIndex = PriceIndexFixture.createPriceIndex(
                "SETTLE",
                FrequencyCode.HOURLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        onPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "ON PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        offPeakDailyIndex = PriceIndexFixture.createPriceIndex(
                "OFF PEAK",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);


        powerProfile = PowerProfileFixture.createPowerProfileAllDaysAllHours(
                "24By7",
                settledHourlyIndex,
                offPeakDailyIndex,
                onPeakDailyIndex);



        receivesIndex = PriceIndexFixture.createPriceIndex(
                "Receives",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        paysIndex = PriceIndexFixture.createPriceIndex(
                "Pays",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        flush();


        PriceIndexFixture.generateDailyPriceCurves(
                paysIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.34),
                LocalDateTime.of(2023, 1, 1, 10, 1));

        PriceIndexFixture.generateDailyPriceCurves(
                receivesIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2.78),
                LocalDateTime.of(2023, 1, 1, 10, 1));



        FinancialSwapDealSnapshot snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.SELL,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "f4floatsell",
                companyRole,
                counterpartyRole,
                receivesIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.setCompanyTraderId(contact.generateEntityId());
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

        fixed4FloatSellDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "f4floatbuy",
                companyRole,
                counterpartyRole,
                receivesIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.setCompanyTraderId(contact.generateEntityId());
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

        fixed4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4floatbuy",
                companyRole,
                counterpartyRole,
                paysIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                receivesIndex);
        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        snapshot.setCompanyTraderId(contact.generateEntityId());
        float4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4floatPlusbuy",
                companyRole,
                counterpartyRole,
                paysIndex,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                receivesIndex,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
        snapshot.setCompanyTraderId(contact.generateEntityId());
        float4FloatPlusBuyDeal = FinancialSwapDeal.create(snapshot);


        snapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
                CommodityCode.NATGAS,
                BuySellCode.BUY,
                startDate,
                endDate,
                DealStatusCode.VERIFIED,
                CurrencyCode.CAD,
                "float4powerprofbuy",
                companyRole,
                counterpartyRole,
                powerProfile,
                BigDecimal.TEN,
                UnitOfMeasureCode.GJ,
                new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));


        snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
        snapshot.setCompanyTraderId(contact.generateEntityId());
        fixed4PowerProfileBuyDeal = FinancialSwapDeal.create(snapshot);

        flush();
        clearCache();

        fixed4FloatSellDeal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());
        float4FloatBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatBuyDeal.generateEntityId());
        float4FloatPlusBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusBuyDeal.generateEntityId());
        fixed4PowerProfileBuyDeal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

    }


    @Test
    public void findPositions() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4FloatSellDeal.getId()));
        DealPositionsEvaluationContext dealPositionsEvaluationContext = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate);


        generatePositionsService.generatePositions(
                "Test",
                dealPositionsEvaluationContext,
                List.of(fixed4FloatSellDeal.getId()));
        flush();

        DealPositionSnapshotCollection collection = dealPositionRestAdapter.find("WHERE ", 0, 100);
        assertTrue(collection.getCount() > 0);
        FinancialSwapPositionSnapshot snapshot = (FinancialSwapPositionSnapshot) collection.getSnapshots().get(0);
        assertEquals(fixed4FloatSellDeal.getId(), snapshot.getDealId().getId());
    }




    @Test
    public void fetchPositionViewsAsCSV() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4FloatSellDeal.getId()));
        DealPositionsEvaluationContext dealPositionsEvaluationContext = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate);

        generatePositionsService.generatePositions(
                "Test",
                dealPositionsEvaluationContext,
                List.of(fixed4FloatSellDeal.getId()));
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
                List.of(fixed4FloatSellDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        String query = "WHERE ticketNo eq '" + fixed4FloatSellDeal.getDealDetail().getTicketNo() + "'";
        String positionQuery = query + " ORDER BY startDate";
        FileReportResult fileReportResult = dealPositionRestAdapter.findPositionsAsCSV(positionQuery, 0, 500);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileReportResult.getDocumentInBytes());
        ArrayList<String> list = new ArrayList<>(DealPositionColumnType.getAsList());
        list.addAll(FinancialSwapPositionColumnType.getAsList());
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
            assertEquals("f4floatsell", ticketNo);

            assertEquals("FinancialSwap", record.get(DealPositionColumnType.DEAL_TYPE.getCode()));
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
            assertEquals("-17.80", record.get(DealPositionColumnType.MTM_AMT.getCode()));
            assertEquals("CAD", record.get(DealPositionColumnType.SETTLEMENT_CURRENCY.getCode()));
            assertEquals("0.00", record.get(DealPositionColumnType.COST_SETTLEMENT_AMT.getCode()));
            assertEquals("-17.80", record.get(DealPositionColumnType.SETTLEMENT_AMT.getCode()));
            assertEquals("-17.80", record.get(DealPositionColumnType.TOTAL_SETTLEMENT_AMT.getCode()));
            assertEquals("0", record.get(DealPositionColumnType.ERROR_CODE.getCode()));
            assertEquals("", record.get(DealPositionColumnType.ERROR_MSG.getCode()));
            assertEquals("Fixed", record.get(FinancialSwapPositionColumnType.PAYS_PRICE_VALUATION_CODE.getCode()));
            assertEquals("1.000", record.get(FinancialSwapPositionColumnType.PAYS_PRICE.getCode()));
            assertEquals("Index", record.get(FinancialSwapPositionColumnType.RECEIVES_VALUATION_CODE.getCode()));
            assertEquals("2.780", record.get(FinancialSwapPositionColumnType.RECEIVES_PRICE.getCode()));
            parser.close();
        } catch (IOException e) {
            throw new OBRuntimeException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
        }

    }

}
