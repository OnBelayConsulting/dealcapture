package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionView;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhysicalPositionsFileWriterTest extends DealCaptureAppSpringTestCase {

    @Test
    public void writeFixedPhysicalPositionsFile() {

        List<DealPositionView> views = new ArrayList<>();

        PhysicalPositionView view = new PhysicalPositionView();
        view.getViewDetail().setDealTypeCode(DealTypeCode.PHYSICAL_DEAL);
        view.getViewDetail().setTicketNo("myticket");
        view.getViewDetail().setStartDate(LocalDate.of(2024, 1, 1));
        view.getViewDetail().setEndDate(LocalDate.of(2024, 1, 31));
        view.getViewDetail().setBuySellCodeValue(BuySellCode.BUY.getCode());
        view.getViewDetail().setCreatedDateTime(LocalDateTime.of(2024, 1, 1, 1, 2));
        view.getViewDetail().setCurrencyCode(CurrencyCode.CAD);
        view.getViewDetail().setVolumeUnitOfMeasure(UnitOfMeasureCode.GJ);
        view.getViewDetail().setVolumeQuantityValue(BigDecimal.valueOf(100));
        view.getViewDetail().setFrequencyCode(FrequencyCode.DAILY);
        view.getViewDetail().setFixedPriceValue(BigDecimal.TEN);
        view.getViewDetail().setFixedPriceCurrencyCodeValue(CurrencyCode.CAD.getCode());
        view.getViewDetail().setFixedPriceUnitOfMeasureCodeValue(UnitOfMeasureCode.GJ.getCode());
        view.getViewDetail().setErrorCode("0");
        view.getViewDetail().setIsSettlementPosition(false);
        view.getViewDetail().setMtmAmountValue(BigDecimal.valueOf(23.80));
        view.getViewDetail().setSettlementCurrencyCodeValue(CurrencyCode.USD.getCode());
        view.getViewDetail().setValuedDateTime(LocalDateTime.of(2024, 1, 1, 1, 2));

        view.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);
        view.getDetail().setMarketPriceValuationValue(ValuationCode.INDEX.getCode());

        view.getPriceDetail().setDealPriceValue(BigDecimal.valueOf(3));
        view.getPriceDetail().setTotalDealPriceValue(BigDecimal.valueOf(3));
        view.getPriceDetail().setMarketPriceValue(BigDecimal.valueOf(4));
        views.add(view);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            DealPositionFileWriter writer = new DealPositionFileWriter(outStream);
            writer.write(views);
        } catch (IOException e) {
            fail("threw exception", e);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
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
            assertEquals("myticket", ticketNo);

            assertEquals(DealTypeCode.PHYSICAL_DEAL.getCode(), record.get(DealPositionColumnType.DEAL_TYPE.getCode()));
            assertEquals("BUY", record.get(DealPositionColumnType.BUY_SELL.getCode()));
            assertEquals("2024-01-01", record.get(DealPositionColumnType.START_DATE.getCode()));
            assertEquals("2024-01-31", record.get(DealPositionColumnType.END_DATE.getCode()));
            assertEquals("D", record.get(DealPositionColumnType.FREQUENCY.getCode()));
            assertEquals("CAD", record.get(DealPositionColumnType.CURRENCY.getCode()));
            assertEquals("100.00",  record.get(DealPositionColumnType.VOL_QUANTITY.getCode()));
            assertEquals("GJ", record.get(DealPositionColumnType.VOL_UNIT_OF_MEASURE.getCode()));
            assertEquals("", record.get(DealPositionColumnType.POWER_FLOW_CODE.getCode()));
            assertEquals("2024-01-01T01:02", record.get(DealPositionColumnType.CREATED_DATE_TIME.getCode()));
            assertNotNull(record.get(DealPositionColumnType.VALUED_DATE_TIME.getCode()));
            assertEquals("23.80", record.get(DealPositionColumnType.MTM_AMT.getCode()));
            assertEquals("USD", record.get(DealPositionColumnType.SETTLEMENT_CURRENCY.getCode()));
            assertEquals("", record.get(DealPositionColumnType.COST_SETTLEMENT_AMT.getCode()));
            assertEquals("", record.get(DealPositionColumnType.SETTLEMENT_AMT.getCode()));
            assertEquals("", record.get(DealPositionColumnType.TOTAL_SETTLEMENT_AMT.getCode()));
            assertEquals("0", record.get(DealPositionColumnType.ERROR_CODE.getCode()));
            assertEquals("", record.get(DealPositionColumnType.ERROR_MSG.getCode()));
            assertEquals("Fixed", record.get(PhysicalPositionColumnType.DEAL_PRICE_VALUATION_CODE.getCode()));
            assertEquals("3.000", record.get(PhysicalPositionColumnType.DEAL_PRICE.getCode()));
            assertEquals("", record.get(PhysicalPositionColumnType.DEAL_INDEX_PRICE.getCode()));
            assertEquals("3.000", record.get(PhysicalPositionColumnType.TOTAL_DEAL_PRICE.getCode()));
            assertEquals("Index", record.get(PhysicalPositionColumnType.MARKET_VALUATION_CODE.getCode()));
            assertEquals("4.000", record.get(PhysicalPositionColumnType.MARKET_PRICE.getCode()));
            parser.close();
        } catch (IOException e) {
            throw new OBRuntimeException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
        }

    }
}
