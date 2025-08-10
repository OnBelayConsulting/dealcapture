package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideMonthSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealOverrideFileReaderTest extends DealCaptureAppSpringTestCase {

    @Test
    public void testDealOverrideFileReader() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/dealoverrides.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealOverrideFileReader reader = new DealOverrideFileReader(fileStream);
        reader.readContents();
        List<DealOverrideSnapshot> snapshots = reader.getDealOverrideSnapshots();
        assertEquals(2, snapshots.size());
        DealOverrideSnapshot dealOverrideSnapshot = snapshots
                .stream()
                .filter( c-> c.getEntityId().getCode().equals("MyDeal"))
                .findFirst().get();

        assertEquals(2, dealOverrideSnapshot.getOverrideMonths().size());
        DealOverrideMonthSnapshot firstMonth = dealOverrideSnapshot.getOverrideMonths()
                        .stream().filter( c-> c.getMonthDate().equals(LocalDate.of(2025,1,1)))
                        .findFirst().get();

        assertEquals(2, firstMonth.getOverrideDays().size());
        DealOverrideDaySnapshot firstDay = firstMonth.getOverrideDays().get(0);
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(firstDay.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(firstDay.getValues().get(1)));

        DealOverrideDaySnapshot secondDay = firstMonth.getOverrideDays().get(1);
        assertEquals(0, BigDecimal.valueOf(1.35).compareTo(secondDay.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(secondDay.getValues().get(1)));

        DealOverrideMonthSnapshot secondMonth = dealOverrideSnapshot.getOverrideMonths()
                .stream().filter( c-> c.getMonthDate().equals(LocalDate.of(2025,2,1)))
                .findFirst().get();

        assertEquals(1, secondMonth.getOverrideDays().size());
        firstDay = secondMonth.getOverrideDays().get(0);
        assertEquals(0, BigDecimal.valueOf(1.36).compareTo(firstDay.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(firstDay.getValues().get(1)));
    }
}
