package com.onbelay.dealcapture.unitofmeasure;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UnitOfMeasureConverterTest {

    @Test
    public void convertGJToMMBTU() {

        Conversion conversion = UnitOfMeasureConverter.findConversion(
                UnitOfMeasureCode.MMBTU,
                UnitOfMeasureCode.GJ);
        assertNotNull(conversion);

        assertEquals(0, BigDecimal.valueOf(0.947817).compareTo(conversion.getValue()));
    }

    @Test
    public void convertMMBTUToGJ() {

        Conversion conversion = UnitOfMeasureConverter.findConversion(
                UnitOfMeasureCode.GJ,
                UnitOfMeasureCode.MMBTU);
        assertNotNull(conversion);

        assertEquals(0, BigDecimal.valueOf(1.055056).compareTo(conversion.getValue()));
    }


}
