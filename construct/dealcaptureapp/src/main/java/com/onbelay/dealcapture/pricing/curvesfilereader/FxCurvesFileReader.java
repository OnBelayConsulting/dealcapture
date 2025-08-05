package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
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

public class FxCurvesFileReader extends CurvesFileReader<FxCurveSnapshot> {
    public FxCurvesFileReader(ByteArrayInputStream streamIn) {
        super(streamIn, FxCurveSnapshot::new);
    }

}
