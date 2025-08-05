package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.pricing.model.InterestIndex;
import com.onbelay.dealcapture.pricing.model.InterestIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithMockUser
public class InterestCurvesReaderTest extends DealCaptureAppSpringTestCase {

    private InterestIndex interestIndex;


    @Override
    public void setUp() {
        super.setUp();

        interestIndex = InterestIndexFixture.createInterestIndex(
                "RiskFree",
                FrequencyCode.DAILY);

        flush();
    }

    @Test
    public void uploadFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/interestratecurves.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        InterestCurvesFileReader fileReader = new InterestCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<InterestCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("RiskFree", snapshotMap.keySet().iterator().next());
    }


}
