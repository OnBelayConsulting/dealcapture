package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHourSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHoursForDaySnapshot;
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

public class HourlyDealOverrideFileReaderTest extends DealCaptureAppSpringTestCase {

    @Test
    public void testDealOverrideFileReader() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/hourlydealoverrides.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        DealOverrideFileReader reader = new DealOverrideFileReader(fileStream);
        reader.readContents();
        List<DealOverrideHoursForDaySnapshot> snapshots = reader.getHourlyDealOverrideSnapshots();
        List<DealOverrideHoursForDaySnapshot> myDealSnaphots = snapshots
                .stream()
                .filter(c -> c.getEntityId().getCode().equals("MyDeal"))
                .toList();
        assertEquals(2, myDealSnaphots.size());
        DealOverrideHoursForDaySnapshot firstDay = myDealSnaphots
                .stream()
                .filter(c-> c.getDayDate().equals(LocalDate.of(2025, 1, 1)))
                .findFirst().get();
        DealOverrideHourSnapshot firstHour = firstDay.getOverrideHours().get(0);
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(firstHour.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(firstHour.getValues().get(1)));

        DealOverrideHourSnapshot secondHour = firstDay.getOverrideHours().get(1);
        assertEquals(0, BigDecimal.valueOf(1.35).compareTo(secondHour.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(secondHour.getValues().get(1)));


        DealOverrideHoursForDaySnapshot secondDay = myDealSnaphots
                .stream()
                .filter(c-> c.getDayDate().equals(LocalDate.of(2025, 2, 1)))
                .findFirst().get();
        firstHour = secondDay.getOverrideHours().get(4);
        assertEquals(0, BigDecimal.valueOf(1.36).compareTo(firstHour.getValues().get(0)));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(firstHour.getValues().get(1)));

        List<DealOverrideHoursForDaySnapshot> otherDealSnapshots = snapshots
                .stream()
                .filter(c -> c.getEntityId().getCode().equals("gh-35"))
                .toList();
        assertEquals(1, otherDealSnapshots.size());

    }
}
