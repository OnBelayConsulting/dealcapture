package com.onbelay.dealcapture.busmath.model;

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
    public void convertPriceUnitOfMeasure() {
        Conversion fromMMBTUToGJ = new Conversion(
                BigDecimal.valueOf(0.947817),
                UnitOfMeasureCode.GJ,
                UnitOfMeasureCode.MMBTU);

        logger.error(fromMMBTUToGJ.toFormula());

        Price price = new Price(
                BigDecimal.valueOf(2),
                CurrencyCode.CAD,
                UnitOfMeasureCode.MMBTU);

        Price converted = price.apply(fromMMBTUToGJ);

        logger.error(converted.toFormula());
        assertEquals(CurrencyCode.CAD, converted.getCurrency());
        assertEquals(UnitOfMeasureCode.GJ, converted.getUnitOfMeasure());
        assertEquals(0, BigDecimal.valueOf(1.895634).compareTo(converted.value));
    }

    @Test
    public void convertCurrency() {
        FxRate rateCADToUSD = new FxRate(
                BigDecimal.valueOf(.94),
                CurrencyCode.USD,
                CurrencyCode.CAD);

        logger.error(rateCADToUSD.toFormula());

        Price price = new Price(
                BigDecimal.ONE,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ);

        logger.error(price.toFormula());

        Price converted = price.apply(rateCADToUSD);
        logger.error(converted.toFormula());
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
    public void addAmount() {
        Amount amount = new Amount(
                BigDecimal.ONE,
                CurrencyCode.CAD);

        Amount total = amount.add(
                new Amount(
                        BigDecimal.ONE,
                        CurrencyCode.CAD));

        logger.error(amount.toFormula());
        assertEquals(CurrencyCode.CAD, total.getCurrency());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(total.value));
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

        logger.error(quantity.toFormula());
        assertEquals(UnitOfMeasureCode.GJ, quantity.getUnitOfMeasureCode());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(quantity.value));
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
        logger.error(converted.toFormula());
        assertEquals(CurrencyCode.USD, converted.getCurrency());
        assertEquals(0, BigDecimal.valueOf(18.8).compareTo(converted.value));

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

        logger.error(price.toFormula());
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

        logger.error(amount.toFormula());
        assertEquals(CurrencyCode.CAD, total.getCurrency());
        assertEquals(0, BigDecimal.ZERO.compareTo(total.value));
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
        logger.error(expected.toPlainString());
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

        logger.error("Converted Price:" + convertedPrice.toFormula());

        Amount amount = convertedPrice.multiply(quantity);
        logger.error("Amt: " + amount.toFormula());

        Amount usAmount = amount.apply(
                new FxRate(
                        BigDecimal.valueOf(0.97),
                        CurrencyCode.USD,
                        CurrencyCode.CAD));

        logger.error(usAmount.toFormula());
        assertEquals(0, BigDecimal.valueOf(52128.987183).compareTo(usAmount.value));
    }
}
