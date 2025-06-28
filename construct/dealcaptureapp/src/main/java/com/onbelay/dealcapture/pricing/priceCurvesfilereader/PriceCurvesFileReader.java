package com.onbelay.dealcapture.pricing.priceCurvesfilereader;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCurvesFileReader {
    private static final Logger logger = LogManager.getLogger();

    private ByteArrayInputStream streamIn;

    private HashMap<String, List<PriceCurveSnapshot>> curveSnapshotMap = new HashMap<>();

    public PriceCurvesFileReader(ByteArrayInputStream streamIn) {

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
                    addSnapshot(parse(record));
            }
            parser.close();
        } catch (IOException e) {
            logger.error("CSV file parsing read failed. ", e);
            throw new OBRuntimeException(PricingErrorCode.INVALID_CURVE_FILE_FORMAT.getCode());
        }

    }

    private void addSnapshot(PriceCurveSnapshot snapshot) {
        List<PriceCurveSnapshot> snapshots = curveSnapshotMap.computeIfAbsent(
                snapshot.getIndexId().getCode(),
                k -> new ArrayList<>());
        snapshots.add(snapshot);
    }

    public Map<String, List<PriceCurveSnapshot>> getCurveSnapshotMap() {
        return curveSnapshotMap;
    }

    private PriceCurveSnapshot parse(CSVRecord record) {

        CurveFileFormat sourceFileFormat = new CurveFileFormat();
        CurveSnapshotMapper mapper = new CurveSnapshotMapper();

        for (int i=0; i < sourceFileFormat.length(); i++) {

            CurveColumnType type = sourceFileFormat.get(i);
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
