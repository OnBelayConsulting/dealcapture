package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;

import java.io.ByteArrayInputStream;

public class InterestCurvesFileReader extends CurvesFileReader<InterestCurveSnapshot> {
    public InterestCurvesFileReader(ByteArrayInputStream streamIn) {
        super(streamIn, InterestCurveSnapshot::new);
    }

}
