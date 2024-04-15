package com.onbelay.dealcapture.unitofmeasure;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UnitOfMeasureConverter {

    private static Map<String, Conversion> conversions = new HashMap<>();

    static {
        conversions.put(
                createKey(
                        UnitOfMeasureCode.MMBTU,
                        UnitOfMeasureCode.GJ),
                new Conversion(
                        BigDecimal.valueOf(0.947817),
                        UnitOfMeasureCode.MMBTU,
                        UnitOfMeasureCode.GJ));
        conversions.put(
                createKey(
                        UnitOfMeasureCode.MWH,
                        UnitOfMeasureCode.MMBTU),
                new Conversion(
                        BigDecimal.valueOf(0.293071),
                        UnitOfMeasureCode.MWH,
                        UnitOfMeasureCode.MMBTU));
        conversions.put(
                createKey(
                        UnitOfMeasureCode.MWH,
                        UnitOfMeasureCode.GJ),
                new Conversion(
                        BigDecimal.valueOf(0.2777777777777778),
                        UnitOfMeasureCode.MWH,
                        UnitOfMeasureCode.GJ));

    }

    /**
     * Create a key for conversion look up
     * @param to    - unitOfMeasure to
     * @param from  - unitOfMeasure from
     * @return
     */
    protected static String createKey(
            UnitOfMeasureCode to,
            UnitOfMeasureCode from) {
        return to.getCode() + "_" + from.getCode();
    }

    /**
     * Find a conversion that will take a value in from and converted to to
     * @param to unitOfMeasure to
     * @param from - unitOfMeasure from
     * @return a Conversion that will change the unit of measure to or
     * null if no conversion exists.
     */
    public static Conversion findConversion(
            UnitOfMeasureCode to,
            UnitOfMeasureCode from) {
        Conversion conversion = conversions.get(
                createKey(
                        to,
                        from));

        if (conversion != null) {
            return conversion;
        } else  {

            Conversion other =  conversions.get(
                                            createKey(
                                                    from,
                                                    to));
            if (other != null)
                return other.getInversion();
        }
        return null;
    }

}
