package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHourSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHoursForDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideSnapshot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DealOverrideFileReader {
    private static final Logger logger = LogManager.getLogger();

    private ByteArrayInputStream streamIn;

    private boolean isHourly = false;

    private Map<String, DealOverrideSnapshot> dealOverrides = new HashMap<>();
    private Map<String, Map<LocalDate, DealOverrideHoursForDaySnapshot>> hourlyDealOverrides = new HashMap<>();

    public DealOverrideFileReader(ByteArrayInputStream streamIn) {

        this.streamIn = streamIn;
    }

    public void readContents()  {

        try (InputStreamReader reader = new InputStreamReader(streamIn)) {

            CSVParser parser = new CSVParser(
                    reader,
                    CSVFormat.EXCEL.builder()
                            .setHeader()
                        .setSkipHeaderRecord(true)
                        .build());

            Iterable<CSVRecord> records = parser;
            List<String> headers = parser.getHeaderNames();
            List<String> overrideHeaders = extractOverrideHeaders(headers);

            // validate headers
            for (CSVRecord record : records) {

                if (record.get(0).isBlank() == false) {
                    DealOverride dealOverride = parse(
                            record,
                            overrideHeaders);

                    if (isHourly) {
                        handleHourlyOverride(
                                dealOverride,
                                overrideHeaders);
                    } else {
                        handleDayOverride(
                                dealOverride,
                                overrideHeaders);
                    }
                }
            }
            parser.close();
        } catch (IOException e) {
            logger.error("CSV file parsing read failed. ", e);
            throw new OBRuntimeException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
        }

    }

    public List<DealOverrideSnapshot> getDealOverrideSnapshots() {
        return new ArrayList<>(dealOverrides.values());
    }

    public List<DealOverrideHoursForDaySnapshot> getHourlyDealOverrideSnapshots() {
        ArrayList<DealOverrideHoursForDaySnapshot> hourlyDealOverrideSnapshots = new ArrayList<>();
        for (Map<LocalDate, DealOverrideHoursForDaySnapshot> map : hourlyDealOverrides.values()) {
            hourlyDealOverrideSnapshots.addAll(map.values());
        }
        return hourlyDealOverrideSnapshots;
    }

    private void handleHourlyOverride(
            DealOverride dealOverride,
            List<String> overrideHeaders) {
        Map<LocalDate, DealOverrideHoursForDaySnapshot> dayForHourMap = hourlyDealOverrides
                .computeIfAbsent(
                        dealOverride.getTicketNo(),
                        k -> new HashMap<>());
        DealOverrideHoursForDaySnapshot day = dayForHourMap.get(dealOverride.getOverrideDate());
        if (day == null) {
            day = new DealOverrideHoursForDaySnapshot();
            day.setEntityId(new EntityId(dealOverride.getTicketNo()));
            day.setDayDate(dealOverride.getOverrideDate());
            day.processHeadings(overrideHeaders);
            day.createHourOverrides();
            dayForHourMap.put(
                    dealOverride.getOverrideDate(),
                    day);
        }
        DealOverrideHourSnapshot hourOverride = day.getOverrideHourAtHourEnding(dealOverride.getHourEnding());
        hourOverride.setValues(dealOverride.getValues());

    }

    private void handleDayOverride(
            DealOverride dealOverride,
            List<String> overrideHeaders) {
        DealOverrideSnapshot dealOverrideSnapshot = dealOverrides.get(dealOverride.getTicketNo());
        if (dealOverrideSnapshot == null) {
            dealOverrideSnapshot = new DealOverrideSnapshot();
            dealOverrideSnapshot.setHeadings(overrideHeaders);
            dealOverrideSnapshot.setEntityId(new EntityId(dealOverride.getTicketNo()));
            dealOverrides.put(dealOverride.getTicketNo(), dealOverrideSnapshot);
        }
        DealOverrideDaySnapshot daySnapshot = new DealOverrideDaySnapshot();
        daySnapshot.setOverrideDate(dealOverride.getOverrideDate());
        daySnapshot.setValues(dealOverride.getValues());
        dealOverrideSnapshot.addDealOverride(daySnapshot);

    }

    private List<String> extractOverrideHeaders(List<String> headers) {
        List<String> overrideHeaders = new ArrayList<>();
        for (String header : headers) {
            if (header.equalsIgnoreCase(DealOverrideColumnType.TICKET_NO.getCode()))
                continue;
            if (header.equalsIgnoreCase(DealOverrideColumnType.OVERRIDE_DATE.getCode()))
                continue;
            if (header.equalsIgnoreCase(DealOverrideColumnType.HOUR_ENDING.getCode())) {
                isHourly = true;
                continue;
            }
            overrideHeaders.add(header);
        }
        return overrideHeaders;
    }

    public boolean isHourly() {
        return isHourly;
    }

    private DealOverride parse(
            CSVRecord record,
            List<String> headers) {

        DealOverride dealOverride = new DealOverride(headers);
        dealOverride.setTicketNo(record.get(DealOverrideColumnType.TICKET_NO.getCode()));
        String date = record.get(DealOverrideColumnType.OVERRIDE_DATE.getCode());
        dealOverride.setOverrideDate((LocalDate) DealOverrideColumnType.OVERRIDE_DATE.getColumnType().getFromCSVConverter().apply(date));
        if (isHourly) {
            String hour = record.get(DealOverrideColumnType.HOUR_ENDING.getCode());
            dealOverride.setHourEnding((Integer) DealOverrideColumnType.HOUR_ENDING.getColumnType().getFromCSVConverter().apply(hour));
        }

        for (String header : headers) {
            String value = record.get(header);
            if (value != null) {
                dealOverride.addValue((BigDecimal) DealOverrideColumnType.VALUE.getColumnType().getFromCSVConverter().apply(value));
            }
        }
        return dealOverride;
    }
}
