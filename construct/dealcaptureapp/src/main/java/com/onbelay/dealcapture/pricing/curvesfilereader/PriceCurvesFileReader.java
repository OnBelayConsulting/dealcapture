package com.onbelay.dealcapture.pricing.curvesfilereader;

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

public class PriceCurvesFileReader extends CurvesFileReader<PriceCurveSnapshot> {
    private static final Logger logger = LogManager.getLogger();

    public PriceCurvesFileReader(ByteArrayInputStream streamIn) {

       super(streamIn, PriceCurveSnapshot::new);
    }

}
