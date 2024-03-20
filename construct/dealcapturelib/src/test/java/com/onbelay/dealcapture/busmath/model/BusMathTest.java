package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.busmath.exceptions.OBBusinessMathException;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class BusMathTest{
    private static final Logger logger = LogManager.getLogger();

    @Test
    public void addPrice() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        Price totalPrice = price.add(
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.GJ));

        assertEquals(0, totalPrice.getValue().compareTo(BigDecimal.valueOf(2)));
    }

    @Test
    public void addIncompatiblePrice() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        try {
            price.add(
                    new Price(
                            BigDecimal.ONE,
                            CurrencyCode.USD,
                            UnitOfMeasureCode.GJ));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void subtractPrice() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        Price totalPrice = price.subtract(
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.GJ));
        assertEquals(0, totalPrice.getValue().compareTo(BigDecimal.valueOf(0)));
    }

    @Test
    public void subtractIncompatiblePrice() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        try {
            price.subtract(
                    new Price(
                            BigDecimal.ONE,
                            CurrencyCode.USD,
                            UnitOfMeasureCode.GJ));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void convertPriceUnitOfMeasure() {
        Conversion fromMMBTUToGJ = new Conversion(
                BigDecimal.valueOf(0.947817),
                UnitOfMeasureCode.GJ,
                UnitOfMeasureCode.MMBTU);

        logger.debug(fromMMBTUToGJ.toFormula());

        Price price = new Price(
                BigDecimal.valueOf(2),
                CurrencyCode.CAD,
                UnitOfMeasureCode.MMBTU);

        Price converted = price.apply(fromMMBTUToGJ);

        logger.debug(converted.toFormula());
        assertEquals(CurrencyCode.CAD, converted.getCurrency());
        assertEquals(UnitOfMeasureCode.GJ, converted.getUnitOfMeasure());
        assertEquals(0, BigDecimal.valueOf(1.895634).compareTo(converted.value));
    }

    @Test
    public void convertPriceUnitOfMeasureFail() {
        Conversion fromMMBTUToGJ = new Conversion(
                BigDecimal.valueOf(0.947817),
                UnitOfMeasureCode.GJ,
                UnitOfMeasureCode.MMBTU);

        logger.debug(fromMMBTUToGJ.toFormula());

        Price price = new Price(
                BigDecimal.valueOf(2),
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        try {
            price.apply(fromMMBTUToGJ);
            fail("Should have thrown  exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }


    @Test
    public void convertIncompatibleCurrencyFail() {
        FxRate rateCADToUSD = new FxRate(
                BigDecimal.valueOf(.94),
                CurrencyCode.USD,
                CurrencyCode.CAD);

        logger.debug(rateCADToUSD.toFormula());

        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.EURO,
                UnitOfMeasureCode.GJ);

        logger.debug(price.toFormula());

        try {
            price.apply(rateCADToUSD);
            fail("Should have thrown exception.");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void convertCurrency() {
        FxRate rateCADToUSD = new FxRate(
                BigDecimal.valueOf(.94),
                CurrencyCode.USD,
                CurrencyCode.CAD);

        logger.debug(rateCADToUSD.toFormula());

        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        logger.debug(price.toFormula());

        Price converted = price.apply(rateCADToUSD);
        logger.debug(converted.toFormula());
        assertEquals(CurrencyCode.USD, converted.getCurrency());
        assertEquals(UnitOfMeasureCode.GJ, converted.getUnitOfMeasure());
        assertEquals(0, BigDecimal.valueOf(0.94).compareTo(converted.value));
    }

    @Test
    public void comparePriceSuccessful() {
        Price price = new Price(
                BigDecimal.valueOf(1.0),
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

         assertTrue(price.equals(
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.GJ)));

    }


    @Test
    public void comparePriceNotEqualCurrency() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        assertFalse(price.equals(
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.USD,
                        UnitOfMeasureCode.GJ)));

    }

    @Test
    public void comparePriceNotEqualUoM() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        assertFalse(price.equals(
                new Price(
                        BigDecimal.ONE,
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.MMBTU)));

    }


    @Test
    public void comparePriceNotEqualValue() {
        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        assertFalse(price.equals(
                new Price(
                        BigDecimal.valueOf(34),
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.GJ)));

    }

    @Test
    public void calculateAmount() {
        Price price = new Price(
                BigDecimal.valueOf(2),
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        Amount amount = price.multiply(
                new Quantity(
                        BigDecimal.valueOf(10),
                    UnitOfMeasureCode.GJ));

        assertEquals(0, amount.getValue().compareTo(BigDecimal.valueOf(20)));
        assertEquals(CurrencyCode.CAD, amount.getCurrency());

    }

    @Test
    public void calculateAmountFail() {
        Price price = new Price(
                BigDecimal.valueOf(2),
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        try {
            price.multiply(
                    new Quantity(
                            BigDecimal.valueOf(10),
                            UnitOfMeasureCode.MMBTU));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }


    @Test
    public void addAmount() {
        Amount amount = new Amount(
                BigDecimal.ONE,
                CurrencyCode.CAD);

        Amount total = amount.add(
                new Amount(
                        BigDecimal.ONE,
                        CurrencyCode.CAD));

        logger.debug(amount.toFormula());
        assertEquals(CurrencyCode.CAD, total.getCurrency());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(total.value));
    }


    @Test
    public void addAmountFail() {
        Amount amount = new Amount(
                BigDecimal.ONE,
                CurrencyCode.CAD);

        try {
            amount.add(
                    new Amount(
                            BigDecimal.ONE,
                            CurrencyCode.USD));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }


    @Test
    public void divideAmountByPrice() {
        Amount amount = new Amount(
                BigDecimal.valueOf(20),
                CurrencyCode.CAD);

        Quantity quantity = amount.divide(
                new Price(
                        BigDecimal.valueOf(2),
                        CurrencyCode.CAD,
                        UnitOfMeasureCode.GJ));

        logger.debug(quantity.toFormula());
        assertEquals(UnitOfMeasureCode.GJ, quantity.getUnitOfMeasureCode());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(quantity.value));
    }


    @Test
    public void divideAmountByPriceFail() {
        Amount amount = new Amount(
                BigDecimal.valueOf(20),
                CurrencyCode.USD);

        try {
            amount.divide(
                    new Price(
                            BigDecimal.valueOf(2),
                            CurrencyCode.CAD,
                            UnitOfMeasureCode.GJ));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void convertAmount() {
        Amount amount = new Amount(
                BigDecimal.valueOf(20),
                CurrencyCode.CAD);

        FxRate rate = new FxRate(
                BigDecimal.valueOf(.94),
                CurrencyCode.USD,
                CurrencyCode.CAD);

        Amount converted = amount.apply(rate);
        logger.debug(converted.toFormula());
        assertEquals(CurrencyCode.USD, converted.getCurrency());
        assertEquals(0, BigDecimal.valueOf(18.8).compareTo(converted.value));

    }

    @Test
    public void convertAmountFail() {
        Amount amount = new Amount(
                BigDecimal.valueOf(20),
                CurrencyCode.EURO);

        FxRate rate = new FxRate(
                BigDecimal.valueOf(.94),
                CurrencyCode.USD,
                CurrencyCode.CAD);

        try {
            amount.apply(rate);
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");

    }


    @Test
    public void divideAmountByQuantity() {
        Amount amount = new Amount(
                BigDecimal.valueOf(20),
                CurrencyCode.CAD);

        Price price = amount.divide(
                new Quantity(
                        BigDecimal.valueOf(2),
                        UnitOfMeasureCode.GJ));

        logger.debug(price.toFormula());
        assertEquals(UnitOfMeasureCode.GJ, price.getUnitOfMeasure());
        assertEquals(CurrencyCode.CAD, price.getCurrency());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(price.value));
    }

    @Test
    public void subtractAmount() {
        Amount amount = new Amount(
                BigDecimal.valueOf(1.0),
                CurrencyCode.CAD);

        Amount total = amount.subtract(
                new Amount(
                        BigDecimal.ONE,
                        CurrencyCode.CAD));

        logger.debug(amount.toFormula());
        assertEquals(CurrencyCode.CAD, total.getCurrency());
        assertEquals(0, BigDecimal.ZERO.compareTo(total.value));
    }


    @Test
    public void subtractAmountFail() {
        Amount amount = new Amount(
                BigDecimal.valueOf(1.0),
                CurrencyCode.USD);

        try {
            amount.subtract(
                    new Amount(
                            BigDecimal.ONE,
                            CurrencyCode.CAD));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }


    @Test
    public void addQuantity() {
        Quantity quantity = new Quantity(
                BigDecimal.ONE,
                UnitOfMeasureCode.GJ);

        Quantity total = quantity.add(
                new Quantity(
                        BigDecimal.ONE,
                        UnitOfMeasureCode.GJ));

        assertEquals(UnitOfMeasureCode.GJ, total.getUnitOfMeasureCode());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(total.getValue()));
    }


    @Test
    public void addQuantityFail() {
        Quantity quantity = new Quantity(
                BigDecimal.ONE,
                UnitOfMeasureCode.MMBTU);

        try {
            quantity.add(
                    new Quantity(
                            BigDecimal.ONE,
                            UnitOfMeasureCode.GJ));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void subtractQuantity() {
        Quantity quantity = new Quantity(
                BigDecimal.valueOf(23.78),
                UnitOfMeasureCode.GJ);

        Quantity total = quantity.subtract(
                new Quantity(
                        BigDecimal.valueOf(23.78),
                        UnitOfMeasureCode.GJ));

        assertEquals(UnitOfMeasureCode.GJ, total.getUnitOfMeasureCode());
        assertEquals(0, BigDecimal.ZERO.compareTo(total.getValue()));
    }

    @Test
    public void subtractQuantityFail() {
        Quantity quantity = new Quantity(
                BigDecimal.valueOf(23.78),
                UnitOfMeasureCode.GJ);

        try {
            quantity.subtract(
                    new Quantity(
                            BigDecimal.ONE,
                            UnitOfMeasureCode.MMBTU));
            fail("Should have thrown exception");
        } catch (OBBusinessMathException e) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test
    public void convertQuantity() {
        Quantity quantity = new Quantity(
                BigDecimal.valueOf(23.78),
                UnitOfMeasureCode.GJ);

        Conversion fromGJToMMBTU = UnitOfMeasureConverter.findConversion(
                UnitOfMeasureCode.MMBTU,
                UnitOfMeasureCode.GJ);

        Quantity total = quantity.apply(fromGJToMMBTU);
        BigDecimal expected = BigDecimal.valueOf(23.78).multiply(BigDecimal.valueOf(0.947817), MathContext.DECIMAL128);
        expected = expected.setScale(6, RoundingMode.HALF_UP);
        logger.debug(expected.toPlainString());
        assertEquals(UnitOfMeasureCode.MMBTU, total.getUnitOfMeasureCode());
        assertEquals(0, expected.compareTo(total.getValue()));
    }

    @Test
    public void complexCalculationTest() {
        Price price = new Price(
                BigDecimal.valueOf(100),
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        Quantity quantity = new Quantity(
                BigDecimal.valueOf(567),
                UnitOfMeasureCode.MMBTU);

        Price convertedPrice = price.apply(
                UnitOfMeasureConverter.findConversion(
                        UnitOfMeasureCode.MMBTU,
                        UnitOfMeasureCode.GJ));

        logger.debug("Converted Price:" + convertedPrice.toFormula());

        Amount amount = convertedPrice.multiply(quantity);
        logger.debug("Amt: " + amount.toFormula());

        Amount usAmount = amount.apply(
                new FxRate(
                        BigDecimal.valueOf(0.97),
                        CurrencyCode.USD,
                        CurrencyCode.CAD));

        logger.debug(usAmount.toFormula());
        assertEquals(0, BigDecimal.valueOf(52128.987183).compareTo(usAmount.value));
    }
}
