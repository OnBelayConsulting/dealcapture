package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
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
public class FxCurvesReaderTest extends DealCaptureAppSpringTestCase {

    private FxIndex fxIndex;

    @Autowired
    private DealRepository dealRepository;

    @Override
    public void setUp() {
        super.setUp();
        fxIndex = FxIndexFixture.createFxIndex(
                "CAD-EURO",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                CurrencyCode.EURO);

        flush();
    }

    @Test
    public void uploadFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/fxcurves.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        FxCurvesFileReader fileReader = new FxCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<FxCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals(fxIndex.getDetail().getName(), snapshotMap.keySet().iterator().next());
    }


}
