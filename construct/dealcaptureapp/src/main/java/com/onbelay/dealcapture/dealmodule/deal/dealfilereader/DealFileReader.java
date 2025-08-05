package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DealFileReader {
    private static final Logger logger = LogManager.getLogger();

    private ByteArrayInputStream streamIn;

    private List<BaseDealSnapshot> dealSnapshots = new ArrayList<>();

    public DealFileReader(ByteArrayInputStream streamIn) {

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

            for (CSVRecord record : records) {

                if (record.get(0).isBlank() == false)
                    dealSnapshots.add(
                            parse(record));
            }
            parser.close();
        } catch (IOException e) {
            logger.error("CSV file parsing read failed. ", e);
            throw new OBRuntimeException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
        }

    }

    public List<BaseDealSnapshot> getDealSnapshots() {
        return dealSnapshots;
    }

    private BaseDealSnapshot parse(CSVRecord record) {

        String code = record.get(0);
        DealTypeCode dealTypeCode = DealTypeCode.lookUp(code);
        BaseDealSnapshotMapper mapper = DealSnapshotMapperFactory.newMapper(dealTypeCode);
        SourceFileFormat<DealColumnType> sourceFileFormat = mapper.getSourceFileFormat();;

        for (int i=1; i < sourceFileFormat.length(); i++) {

            DealColumnType type = sourceFileFormat.get(i);
            String field = record.get(i);
            if (field == null || field.isEmpty())
                continue;
            Object value = type.getColumnType().getFromCSVConverter().apply(field);

            mapper.setPropertyValue(
                type,
                value);
        }
        return mapper.getSnapshot();
    }
}
