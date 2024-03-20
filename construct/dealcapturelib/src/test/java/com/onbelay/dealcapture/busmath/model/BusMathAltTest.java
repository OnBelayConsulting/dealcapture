package com.onbelay.dealcapture.busmath.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.CompactNumberFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class BusMathAltTest {
    private static final Logger logger = LogManager.getLogger();
    private double doubleValue = 23344555.53;
    private double doubleMorePrecisionValue = 23344555.536;
    private float floatValue = 23344555.53f;

    private BigDecimal bgValue = BigDecimal.valueOf(23344555.53);
    private BigDecimal bgMorePrecisionValue = BigDecimal.valueOf(23344555.536);


    @Test
    public void testFloats() {
        float total = floatValue;
        for (int i=0; i < 100; i++)
            total = total + floatValue;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    public void testDouble() {
        double total = doubleValue;
        for (int i=0; i < 100; i++)
            total = total + doubleValue;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    // 2,357,800,108.53
    public void testBigDecimal() {
        BigDecimal total = bgValue;
        for (int i=0; i < 100; i++)
            total = total.add(bgValue, MathContext.DECIMAL128);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }


    @Test
    public void testDoubleMorePrecision() {
        double total = doubleMorePrecisionValue;
        for (int i=0; i < 100; i++)
            total = total + doubleMorePrecisionValue;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    // 2,357,800,108.53
    public void testBigDecimalMorePrecision() {
        BigDecimal total = bgMorePrecisionValue;
        for (int i=0; i < 100; i++)
            total = total.add(bgMorePrecisionValue, MathContext.DECIMAL128);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    public void testDoubleComplex() {
        double total = (doubleMorePrecisionValue * 3.45) / 456678;
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    public void testBigDecimalComplex() {
        BigDecimal total = bgMorePrecisionValue.multiply(BigDecimal.valueOf(3.45), MathContext.DECIMAL128);
        total = total.divide(BigDecimal.valueOf(456678), MathContext.DECIMAL128);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    public void testDoubleWithRepeating() {
        double value = 10;
        double total = value / 3;

        total = total * 3;

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.####");
        logger.debug(decimalFormat.format(total));
    }

    @Test
    public void roundingBigDecimalFail() {
        BigDecimal value = BigDecimal.valueOf(10.4456);
        try {
            BigDecimal price = value.setScale(2);
            fail("Should have thrown an exception");
        } catch (ArithmeticException e) {
            assertEquals("Rounding necessary", e.getMessage());
        } catch (Throwable t) {
            fail("Should have thrown Arithmetic Exception");
        }
    }

    @Test
    public void roundingExampleOne() {

        BigDecimal price = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(3), MathContext.DECIMAL128);
        price = price.setScale(4, RoundingMode.HALF_UP);
        logger.debug(price.toPlainString());
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal quantity = BigDecimal.valueOf(24.5);
        for (int i=0; i < 31; i++) {
            BigDecimal amount = price.multiply(quantity, MathContext.DECIMAL128);
            total = total.add(amount, MathContext.DECIMAL128);
        }

        BigDecimal settlementTotal = total.setScale(2, RoundingMode.HALF_UP);
        logger.debug(settlementTotal.toPlainString());
    }

    @Test
    public void roundingExampleTwo() {

        BigDecimal price = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(3), MathContext.DECIMAL128);
        price = price.setScale(4, RoundingMode.HALF_UP);
        logger.debug(price.toPlainString());
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal quantity = BigDecimal.valueOf(24.5);
        for (int i=0; i < 31; i++) {
            BigDecimal amount = price.multiply(quantity, MathContext.DECIMAL128);
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            total = total.add(amount, MathContext.DECIMAL128);
        }

        BigDecimal settlementTotal = total.setScale(2, RoundingMode.HALF_UP);
        logger.debug(settlementTotal.toPlainString());
    }

    @Test
    public void exploringMathContextArithmeticException() {
        try {
            BigDecimal price = BigDecimal.valueOf(1000000).divide(BigDecimal.valueOf(3), MathContext.UNLIMITED);
            fail("Should have thrown non-terminating decimal expansion ... exception");
        } catch (ArithmeticException e) {
            assertTrue(e.getMessage().startsWith("Non-terminating decimal expansion"));
        } catch (Throwable r) {
            fail("Should have thrown non-terminating decimal expansion ... exception");
        }

    }

    @Test
    public void exploringMathContextMathContext32() {
        BigDecimal price = BigDecimal.valueOf(1000000).divide(BigDecimal.valueOf(3), MathContext.DECIMAL32);
        logger.debug(price.toPlainString());

    }

    @Test
    public void exploringMathContextMathContext128() {
        BigDecimal price = BigDecimal.valueOf(1000000).divide(BigDecimal.valueOf(3), MathContext.DECIMAL128);
        logger.debug(price.toPlainString());

    }

    @Test
    public void exploringRoundingHalfDown() {
        BigDecimal value = BigDecimal.valueOf(10.4456);
        BigDecimal price = value.setScale(2, RoundingMode.DOWN);
        logger.debug(price.toPlainString());
    }


    @Test
    public void exploringRoundingHalfUp() {
        BigDecimal value = BigDecimal.valueOf(10.4456);
        BigDecimal price = value.setScale(2, RoundingMode.HALF_UP);
        logger.debug(price.toPlainString());
    }


    @Test
    void precisionGreaterThan15PrecisionIsLost() {
        BigDecimal bigDecimalValue = new BigDecimal("99999999999999999.99");
        double doubleValue2 = 99999999999999999.99;
        double doubleValue = bigDecimalValue.doubleValue();
        assertEquals(doubleValue2, doubleValue);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.##");
        logger.debug("BG:" + decimalFormat.format(bigDecimalValue));
        logger.debug("Converted from BG:" + decimalFormat.format(doubleValue));
        logger.debug("Direct Double:" + decimalFormat.format(doubleValue2));

        BigDecimal convertedBackToBigDecimal = BigDecimal.valueOf(doubleValue);
        logger.debug("Converted back:" + convertedBackToBigDecimal.toPlainString());
        assertEquals(-1, bigDecimalValue.compareTo(convertedBackToBigDecimal));
    }


    @Test
    void precisionLessThan15ThenPrecisionIsKept() {
        BigDecimal bigDecimalValue = new BigDecimal("9999999999999.99");
        double doubleValue2 = 9999999999999.99;
        double doubleValue = bigDecimalValue.doubleValue();
        assertEquals(doubleValue, doubleValue2);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.##");
        logger.debug("BG:" + decimalFormat.format(bigDecimalValue));
        logger.debug("Converted from BG:" + decimalFormat.format(doubleValue));
        logger.debug("Direct Double:" + decimalFormat.format(doubleValue2));

        BigDecimal convertedBackToBigDecimal = BigDecimal.valueOf(doubleValue);
        logger.debug("Converted back:" + convertedBackToBigDecimal.toPlainString());
        assertEquals(0, bigDecimalValue.compareTo(convertedBackToBigDecimal));
    }

}
