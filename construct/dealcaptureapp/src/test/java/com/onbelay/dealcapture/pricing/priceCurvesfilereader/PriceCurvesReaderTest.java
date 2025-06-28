package com.onbelay.dealcapture.pricing.priceCurvesfilereader;

import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.DealFileReader;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@WithMockUser
public class PriceCurvesReaderTest extends DealCaptureAppSpringTestCase {

    private PriceIndex priceIndex;

    @Autowired
    private DealRepository dealRepository;

    @Override
    public void setUp() {
        super.setUp();

        PricingLocation location = PricingLocationFixture.createPricingLocation("west");
        priceIndex = PriceIndexFixture.createPriceIndex(
                "AECO-M",
                location);

        flush();
    }

    @Test
    public void uploadFile() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/pricecurves.csv");

        ByteArrayInputStream fileStream = new ByteArrayInputStream(inputStream.readAllBytes());
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);
        fileReader.readContents();
        Map<String, List<PriceCurveSnapshot>> snapshotMap = fileReader.getCurveSnapshotMap();

        assertEquals("AECO-M", snapshotMap.keySet().iterator().next());
    }


}
