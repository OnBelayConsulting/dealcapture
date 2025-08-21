package com.onbelay.dealcapture.pricing.curvesfilereader;

import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@WithMockUser
public class PriceCurvesReaderTest extends DealCaptureAppSpringTestCase {

    private PriceIndex priceIndex;
    private PriceIndex powerPriceIndex;

    @Autowired
    private DealRepository dealRepository;

    @Override
    public void setUp() {
        super.setUp();

        PricingLocation location = PricingLocationFixture.createPricingLocation("west");
        priceIndex = PriceIndexFixture.createPriceIndex(
                "AECO-M",
                location);
        powerPriceIndex = PriceIndexFixture.createPriceIndex(
                "Power",
                FrequencyCode.HOURLY,
                CurrencyCode.CAD,
                location);

        flush();
    }

    @Test
    public void uploadNormalFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/pricecurves.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<PriceCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("AECO-M", snapshotMap.keySet().iterator().next());
        List<PriceCurveSnapshot> priceCurveSnapshots = snapshotMap.get("AECO-M");
        assertEquals(1, priceCurveSnapshots.size());
        PriceCurveSnapshot priceCurveSnapshot = priceCurveSnapshots.get(0);
        assertEquals(0, priceCurveSnapshot.getDetail().getHourEnding());
        //2020-01-01T01:01:20
        assertEquals(LocalDateTime.of(2027, 1, 1, 1, 1, 20), priceCurveSnapshot.getDetail().getObservedDateTime());
    }

    @Test
    public void uploadFileDefaultObsDate() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/pricecurveswithnoobsdate.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<PriceCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("AECO-M", snapshotMap.keySet().iterator().next());
        List<PriceCurveSnapshot> priceCurveSnapshots = snapshotMap.get("AECO-M");
        assertEquals(1, priceCurveSnapshots.size());
        PriceCurveSnapshot priceCurveSnapshot = priceCurveSnapshots.get(0);
        assertEquals(0, priceCurveSnapshot.getDetail().getHourEnding());
        assertEquals(priceCurveSnapshot.getDetail().getCurveDate().atStartOfDay(), priceCurveSnapshot.getDetail().getObservedDateTime());
    }

    @Test
    public void uploadFileFutureDefaultObsDate() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/pricecurveswithfutureobsdate.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<PriceCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("AECO-M", snapshotMap.keySet().iterator().next());
        List<PriceCurveSnapshot> priceCurveSnapshots = snapshotMap.get("AECO-M");
        assertEquals(1, priceCurveSnapshots.size());
        PriceCurveSnapshot priceCurveSnapshot = priceCurveSnapshots.get(0);
        assertEquals(0, priceCurveSnapshot.getDetail().getHourEnding());
        assertEquals(0, priceCurveSnapshot.getDetail().getObservedDateTime().getNano());
    }


    @Test
    public void uploadHourlyFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/powerpricecurves.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<PriceCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("Power", snapshotMap.keySet().iterator().next());
        List<PriceCurveSnapshot> priceCurveSnapshots = snapshotMap.get("Power");
        assertEquals(1, priceCurveSnapshots.size());
        PriceCurveSnapshot priceCurveSnapshot = priceCurveSnapshots.get(0);
        assertEquals(1, priceCurveSnapshot.getDetail().getHourEnding());
        assertNotNull(priceCurveSnapshot.getDetail().getObservedDateTime());
    }



}
