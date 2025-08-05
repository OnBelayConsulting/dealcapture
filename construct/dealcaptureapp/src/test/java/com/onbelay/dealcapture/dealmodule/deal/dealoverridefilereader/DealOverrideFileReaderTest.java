package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideMonthSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealOverrideFileReaderTest extends DealCaptureAppSpringTestCase {

    @Test
    public void testDealOverrideFileReader() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/dealoverrides.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealOverrideFileReader reader = new DealOverrideFileReader(fileStream);
        reader.readContents();
        List<DealOverrideSnapshot> snapshots = reader.getSnapshots();
        assertEquals(2, snapshots.size());
        DealOverrideSnapshot dealOverrideSnapshot = snapshots.stream().filter( c-> c.getEntityId().getCode().equals("gh-34")).findFirst().get();
        assertEquals(2, dealOverrideSnapshot.getOverrideMonths().size());
        DealOverrideMonthSnapshot monthSnapshot1 = dealOverrideSnapshot.getOverrideMonths().get(0);
        assertEquals(2, monthSnapshot1.getOverrideDays().size());
    }
}
